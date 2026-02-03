package com.project.attendez.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.ui.attendee.AddAttendeeDialog
import com.project.attendez.ui.attendee.ErrorState
import com.project.attendez.ui.attendee.ExistingAttendeeDialog
import com.project.attendez.ui.attendee.LoadingState
import com.project.attendez.ui.attendee.SearchAttendeeDialog
import com.project.attendez.ui.event.EmptyAttendance
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueTertiary
import com.project.attendez.ui.util.AttendeeUtils
import com.project.attendez.viewmodel.AttendanceUiState
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.AttendeeViewModel
import com.project.attendez.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendeeScreen(eventId: Long, onAttendance: (Long, Long) -> Unit, onBack: () -> Unit) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showExistingDialog by remember { mutableStateOf(false) }
    var showSearchDialog by rememberSaveable { mutableStateOf(false) }

    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val attendance by remember(eventId) {
        attendanceViewModel.attendance(eventId)
    }.collectAsState()

    Box {
        Scaffold(
            containerColor = Color.White,
            topBar = { Header(onBack) }
        ) { paddingValues ->
            AttendeeContent(
                paddingValues,
                eventId,
                attendance,
                onAttendance,
                onExisting = { showExistingDialog = true },
                onAdd = { showAddDialog = true },
                onSearch = { showSearchDialog = true }
            )
        }

        when {
            showAddDialog -> {
                AddAttendeeDialog(eventId) { showAddDialog = false }
            }

            showExistingDialog -> {
                ExistingAttendeeDialog(eventId, attendance) { showExistingDialog = false }
            }

            showSearchDialog -> {
                SearchAttendeeDialog(eventId, attendance, onAttendance) { showSearchDialog = false }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Manage Attendee") },
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
fun AttendeeContent(
    paddingValues: PaddingValues,
    eventId: Long,
    attendance: List<AttendanceEntity>,
    onAttendance: (Long, Long) -> Unit,
    onExisting: () -> Unit,
    onAdd: () -> Unit,
    onSearch: () -> Unit
) {
    val eventViewModel = hiltViewModel<EventViewModel>()
    val attendeeViewModel = hiltViewModel<AttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val uiState by attendanceViewModel.uiState.collectAsState()

    LaunchedEffect(attendance) { attendanceViewModel.loadEventAttendance(eventId) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(Color.White)
    ) {
        Header()

        EventCard(eventId, eventViewModel)

        Spacer(Modifier.height(16.dp))

        ActionBars(onExisting = onExisting, onAdd = onAdd)

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(bottom = 8.dp))

        AttendeeHeader(
            attendanceSize = attendance.size,
            attendance,
            attendanceViewModel,
            onSearch
        )

        HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(top = 8.dp))

        when (uiState) {
            AttendanceUiState.Loading -> LoadingState()

            is AttendanceUiState.Error -> {
                ErrorState(
                    message = (uiState as AttendanceUiState.Error).message,
                    onRetry = { /*attendanceViewModel.loadEventAttendance(eventId)*/ }
                )
            }

            is AttendanceUiState.Success -> {
                val attendance = (uiState as AttendanceUiState.Success).attendance

                if (attendance.isEmpty()) {
                    EmptyAttendance(label = "No attendees yet.\nAdd students to start tracking attendance.")
                } else {
                    val attendees = attendeeViewModel.getAttendeesByEventId(eventId)
                        .collectAsState(initial = emptyList()).value.associate { it?.id to it?.fullName }

                    val sortedAttendance = attendance
                        .sortedWith(
                            comparator = compareByDescending<AttendanceEntity> { it.isPresent }
                                .thenBy { attendees[it.attendeeId]?.lowercase() }
                        )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = sortedAttendance, key = { it.attendeeId }) { record ->
                            val attendee by remember(record.attendeeId) {
                                attendeeViewModel.getAttendeeById(record.attendeeId)
                            }.collectAsState(initial = null)

                            attendee?.let { person ->
                                AttendeeItem(
                                    attendee = person,
                                    isPresent = record.isPresent,
                                    onClick = { onAttendance(eventId, person.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Header() {
    Text(
        text = "Event Attendance",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun EventCard(eventId: Long, eventViewModel: EventViewModel) {
    val event by remember(eventId) {
        eventViewModel.getEventById(eventId)
    }.collectAsState(initial = null)

    event?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(BackgroundGradient)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = it.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.date),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(
                        text = "${it.date}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttendeeHeader(
    attendanceSize: Int,
    attendance: List<AttendanceEntity>,
    attendanceViewModel: AttendanceViewModel,
    onSearch: () -> Unit
) {
    val context = LocalContext.current
    var confirmRemoveAll by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Attendees ($attendanceSize)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            modifier = Modifier.animateContentSize()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.animateContentSize()
        ) {
            IconButton(
                onClick = onSearch,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = attendance.size > 1) {
                TextButton(
                    onClick = { confirmRemoveAll = true },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(text = " Remove All")
                }
            }
        }
    }

    if (confirmRemoveAll) {
        ConfirmDialog(
            context,
            attendance,
            attendanceViewModel
        ) { confirmRemoveAll = false }
    }
}

@Composable
private fun ConfirmDialog(
    context: Context,
    attendance: List<AttendanceEntity>,
    attendanceViewModel: AttendanceViewModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(38.dp)
            )
        },
        title = { Text(text = "Remove All Records", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                text = "Are you sure you want to remove all attendees to this event?",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    attendanceViewModel.deleteAllAttendance(attendance)
                    Toast.makeText(context, "Attendance records deleted", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Confirm", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
            ) {
                Text(text = "Cancel", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color.White,
        iconContentColor = Color.Red,
        titleContentColor = Color.Black,
        textContentColor = Color.DarkGray
    )
}

@Composable
private fun ActionBars(onExisting: () -> Unit, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onExisting,
            border = BorderStroke(width = 1.dp, color = BluePrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Use Existing")
        }

        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary,
                contentColor = Color.White
            ),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.add),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Add New")
        }
    }
}

@Composable
fun LazyItemScope.AttendeeItem(
    attendee: AttendeeEntity,
    isPresent: Boolean? = null,
    @DrawableRes trailingIcon: Int? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateItem(),
        colors = CardDefaults.cardColors(containerColor = BlueTertiary),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Badge(
                    modifier = Modifier.size(40.dp),
                    contentColor = Color.White,
                    containerColor = BluePrimary
                ) {
                    Text(
                        text = attendee.fullName.first().toString().uppercase(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column {
                    Text(
                        text = attendee.fullName,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.widthIn(max = 216.dp)
                    )

                    Text(
                        text = "${attendee.course} • ${AttendeeUtils.getYearLevel(attendee.yearLevel)} • ${attendee.studentId}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.widthIn(max = 236.dp)
                    )
                }
            }

            if (isPresent != null) {
                Image(
                    painter = painterResource(if (isPresent) R.drawable.present else R.drawable.pending),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                trailingIcon?.let { icon ->
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
