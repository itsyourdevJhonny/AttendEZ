package com.project.attendez.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.ui.attendee.AddAttendeeDialog
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.AttendeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeAttendanceScreen(
    eventId: Long,
    onAttendance: (Long, Long) -> Unit,
    onBack: () -> Unit,
) {
    val attendeeViewModel = hiltViewModel<AttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val attendees by attendeeViewModel.getAttendeesByEventId(eventId).collectAsState(initial = null)

    var status by remember { mutableStateOf<AttendanceStatus?>(null) }

    val filteredAttendees = if (status == null) {
        attendees?.filterNotNull()
    } else {
        attendees?.filterNotNull()?.filter {
            val attendance by remember(it.id) {
                attendanceViewModel.getAttendanceByAttendee(eventId, attendeeId = it.id)
            }.collectAsState()

            attendance?.status == status
        }
    }

    var value by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onBack,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
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

                        FilterDropDown(
                            status,
                            expanded,
                            onExpanded = { expanded = it },
                            onStatusUpdated = { status = it }
                        )

                        HorizontalDivider(
                            color = Color.White,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        AttendeeList(filteredAttendees, attendanceViewModel, eventId, onAttendance)
                    }
                }
            }

            if (showAddDialog) {
                AddAttendeeDialog(eventId) { showAddDialog = false }
            }
        }
    }
}

@Composable
private fun AttendeeList(
    filteredAttendees: List<AttendeeEntity>?,
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
        items(filteredAttendees ?: emptyList()) { attendee ->
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
private fun ColumnScope.FilterDropDown(
    status: AttendanceStatus?,
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    onStatusUpdated: (AttendanceStatus?) -> Unit,
) {
    Row(
        modifier = Modifier
            .align(Alignment.End)
            .padding(16.dp)
            .clickable { onExpanded(true) }
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Filter By:", color = Color.White)

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