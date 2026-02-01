package com.project.attendez.ui.screens.attendee

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.repository.AddAttendeeResult
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.ExistingAttendeeUiState
import com.project.attendez.viewmodel.ExistingAttendeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingAttendeeDialog(
    eventId: Long,
    attendance: List<AttendanceEntity>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = hiltViewModel<ExistingAttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val existingIds = remember(attendance) { attendance.map { it.attendeeId }.toSet() }

    LaunchedEffect(Unit) { viewModel.loadAttendees() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    TopAppBar(
                        title = { Text("Add Attendee") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name or student ID", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = CircleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )

                    /** CONTENT **/
                    when (uiState) {
                        ExistingAttendeeUiState.Loading -> LoadingState()

                        is ExistingAttendeeUiState.Error -> ErrorState(
                            message = (uiState as ExistingAttendeeUiState.Error).message,
                            onRetry = { viewModel.loadAttendees() }
                        )

                        is ExistingAttendeeUiState.Success -> {
                            val attendees =
                                (uiState as ExistingAttendeeUiState.Success).attendees
                                    .filterNot { it.id in existingIds }
                                    .filter {
                                        it.fullName.contains(searchQuery, true) ||
                                                it.studentId.contains(searchQuery, true)
                                    }

                            if (attendees.isEmpty()) {
                                EmptyAttendeeState()
                            } else {
                                LazyColumn {
                                    items(attendees, key = { it.id }) { attendee ->
                                        AttendeeRow(attendee) {
                                            attendanceViewModel.addAttendeeToEvent(
                                                eventId = eventId,
                                                studentId = attendee.studentId,
                                                fullName = attendee.fullName,
                                                course = attendee.course.orEmpty(),
                                                yearLevel = attendee.yearLevel ?: 0
                                            ) { result ->
                                                Toast.makeText(
                                                    context,
                                                    if (result == AddAttendeeResult.New) "Student added successfully"
                                                    else "Student already exists, added to event",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onDismiss()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(message)
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun AttendeeRow(attendee: AttendeeEntity, onAdd: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(attendee.fullName, fontWeight = FontWeight.SemiBold)
        },
        supportingContent = {
            Text("${attendee.studentId} • ${attendee.course.orEmpty()}")
        },
        trailingContent = {
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add",
                    tint = BluePrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    )

    HorizontalDivider()
}

@Composable
private fun EmptyAttendeeState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "No attendees found",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Try a different name or ID",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}