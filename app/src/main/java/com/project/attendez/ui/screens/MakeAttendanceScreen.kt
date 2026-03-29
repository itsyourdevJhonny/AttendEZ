package com.project.attendez.ui.screens

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.ui.attendance.ImportPreviewDialog
import com.project.attendez.ui.attendee.AddAttendeeDialog
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.AttendeeViewModel

enum class SortType {
    NAME,
    ATTENDED,
    YEAR,
    COURSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeAttendanceScreen(
    eventId: Long,
    onAttendance: (Long, Long) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val attendeeViewModel = hiltViewModel<AttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val previewList by attendanceViewModel.previewList.collectAsState()

    val attendees by attendeeViewModel
        .getAttendeesByEventId(eventId)
        .collectAsState(initial = emptyList())

    val attendanceList by attendanceViewModel
        .attendance(eventId)
        .collectAsState(initial = emptyList())

    var sortType by remember { mutableStateOf(SortType.NAME) }
    var status by remember { mutableStateOf<AttendanceStatus?>(null) }

    val updatedAttendees by remember {
        derivedStateOf {
            val attendanceMap = attendanceList.associateBy { it.attendeeId }

            val filtered = if (status == null) {
                attendees
            } else {
                attendees.filter {
                    attendanceMap[it?.id]?.status == (status ?: (status == AttendanceStatus.ABSENT))
                }
            }.filterNotNull()

            when (sortType) {
                SortType.NAME -> filtered.sortedBy { it.fullName }
                SortType.COURSE -> filtered.sortedBy { it.course }
                SortType.YEAR -> filtered.sortedBy { it.yearLevel }
                SortType.ATTENDED -> filtered.sortedBy {
                    attendanceMap[it.id]?.date
                }
            }
        }
    }

    var value by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    var sortExpanded by remember { mutableStateOf(false) }
    var filterExpanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            attendanceViewModel.loadPreview(context, it, eventId)
        }
    }

    Box {
        Scaffold(
            topBar = { Header(onBack) }
        ) { paddingValues ->
            Box {
                BackgroundImage()

                Column(modifier = Modifier.padding(paddingValues)) {
                    Spacer(Modifier.height(16.dp))

                    SearchFieldAndAddAttendeeButton(
                        value,
                        onValueChange = { value = it },
                        onShowDialog = { showAddDialog = it }
                    )

                    ImportBulkButton(launcher)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SortDropDown(
                            sortType,
                            expanded = sortExpanded,
                            onSortTypeChanged = { sortType = it },
                            onExpanded = { sortExpanded = it }
                        )

                        FilterDropDown(
                            status,
                            filterExpanded,
                            onExpanded = { filterExpanded = it },
                            onStatusUpdated = { status = it }
                        )
                    }

                    HorizontalDivider(
                        color = Color.White,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    AttendeeList(updatedAttendees, attendanceViewModel, eventId, onAttendance)
                }
            }
        }

        if (showAddDialog) {
            AddAttendeeDialog(eventId) { showAddDialog = false }
        }

        if (previewList.isNotEmpty()) {
            ImportPreviewDialog(
                previewList = previewList,
                onConfirm = { attendanceViewModel.importToDatabase(eventId) },
                onCancel = { attendanceViewModel.clearPreview() }
            )
        }
    }
}

@Composable
private fun ImportBulkButton(launcher: ManagedActivityResultLauncher<Array<String>, Uri?>) {
    TextButton(
        onClick = {
            launcher.launch(
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
            )
        },
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.excel),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(8.dp))

        Text(text = "Import Excel Data", color = Color.White)
    }
}

@Composable
fun SortDropDown(
    sortType: SortType,
    expanded: Boolean,
    onSortTypeChanged: (SortType) -> Unit,
    onExpanded: (Boolean) -> Unit,
) {
    TextButton(
        onClick = { onExpanded(true) },
        modifier = Modifier.animateContentSize()
    ) {
        Text(text = "Sort By:", color = Color.White)

        Spacer(Modifier.width(8.dp))

        Box {
            Text(
                text = sortType.name.lowercase().replaceFirstChar { it.titlecase() },
                color = Color.White,
                fontWeight = FontWeight.Black
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpanded(false) },
                containerColor = Color.White
            ) {
                SortType.entries.forEach { type ->
                    val selected = sortType == type

                    DropdownMenuItem(
                        modifier = Modifier.background(if (selected) BluePrimary else Color.White),
                        text = { Text(text = type.name) },
                        colors = MenuDefaults.itemColors(textColor = if (selected) Color.White else BluePrimary),
                        onClick = {
                            onSortTypeChanged(type)
                            onExpanded(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendeeList(
    updatedAttendees: List<AttendeeEntity>?,
    attendanceViewModel: AttendanceViewModel,
    eventId: Long,
    onAttendance: (Long, Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(updatedAttendees ?: emptyList()) { attendee ->
            val attendanceRecord by remember(attendee.id) {
                attendanceViewModel.getAttendanceByAttendee(eventId, attendeeId = attendee.id)
            }.collectAsState()

            AttendeeItem(
                eventId = eventId,
                attendee = attendee,
                status = attendanceRecord?.status,
                attendanceViewModel = attendanceViewModel,
                onClick = { onAttendance(eventId, attendee.id) }
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Mark Attendance") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    )
}

@Composable
private fun BackgroundImage() {
    Image(
        painter = painterResource(R.drawable.background),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.5f,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun SearchFieldAndAddAttendeeButton(
    value: String,
    onValueChange: (String) -> Unit,
    onShowDialog: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = "Search attendee...") },
            shape = CircleShape,
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = Color.White,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedTextColor = Color.White
            )
        )

        IconButton(
            onClick = { onShowDialog(true) },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = BluePrimary,
                contentColor = Color.White
            )
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
private fun FilterDropDown(
    status: AttendanceStatus?,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onStatusUpdated: (AttendanceStatus?) -> Unit,
) {
    TextButton(
        onClick = { onExpanded(true) },
        modifier = Modifier.animateContentSize()
    ) {
        Text(text = "Filter By:", color = Color.White)

        Spacer(Modifier.width(8.dp))

        Box {
            Text(text = status?.name ?: "All", color = Color.White, fontWeight = FontWeight.Black)

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpanded(false) },
                containerColor = Color.White
            ) {
                DropdownMenuItem(
                    modifier = Modifier.background(if (status == null) BluePrimary else Color.White),
                    text = { Text(text = "All") },
                    colors = MenuDefaults.itemColors(textColor = if (status == null) Color.White else BluePrimary),
                    onClick = {
                        onStatusUpdated(null)
                        onExpanded(false)
                    }
                )

                AttendanceStatus.entries.forEach { attendanceStatus ->
                    val selected = status == attendanceStatus

                    DropdownMenuItem(
                        modifier = Modifier.background(if (selected) BluePrimary else Color.White),
                        text = { Text(text = attendanceStatus.name) },
                        colors = MenuDefaults.itemColors(textColor = if (selected) Color.White else BluePrimary),
                        onClick = {
                            onStatusUpdated(attendanceStatus)
                            onExpanded(false)
                        }
                    )
                }
            }
        }
    }
}