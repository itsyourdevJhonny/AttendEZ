package com.project.attendez.ui.attendee

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.util.drawGradient
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
    val selectedAttendees = remember { mutableStateListOf<AttendeeEntity?>() }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var useAll by remember { mutableStateOf(false) }

    val existingIds = remember(attendance) { attendance.map { it.attendeeId }.toSet() }

    LaunchedEffect(Unit) { viewModel.loadAttendees() }

    BackHandler { onDismiss() }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Header(selectedAttendees, attendanceViewModel, eventId, context, onDismiss)

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name or student ID...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .drawGradient()
                            .size(28.dp)
                    )
                },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    unfocusedBorderColor = Color.Black,
                    focusedTextColor = Color.Black,
                    focusedBorderColor = BluePrimary,
                    cursorColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            when (uiState) {
                ExistingAttendeeUiState.Loading -> LoadingState()

                is ExistingAttendeeUiState.Error -> ErrorState(
                    message = (uiState as ExistingAttendeeUiState.Error).message,
                    onRetry = { viewModel.loadAttendees() }
                )

                is ExistingAttendeeUiState.Success -> {
                    val attendees = filterAttendees(uiState, existingIds, searchQuery)

                    if (attendees.isEmpty()) {
                        EmptyAttendeeState()
                    } else {
                        if (attendees.size > 1) {
                            AddAllCheckBox(useAll, selectedAttendees, attendees) { useAll = it }
                            HorizontalDivider(thickness = 0.5.dp)
                        }

                        LazyColumn {
                            items(attendees, key = { it.id }) { attendee ->
                                val isSelected = attendee in selectedAttendees

                                AttendeeRow(attendee, isSelected) {
                                    if (isSelected) selectedAttendees.remove(attendee)
                                    else selectedAttendees.add(attendee)
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
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(
    selectedAttendees: SnapshotStateList<AttendeeEntity?>,
    attendanceViewModel: AttendanceViewModel,
    eventId: Long,
    context: Context,
    onDismiss: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Add Attendee")

                Button(
                    onClick = {
                        if (selectedAttendees.isNotEmpty()) {
                            attendanceViewModel.addMultipleAttendance(
                                attendance = selectedAttendees.map {
                                    AttendanceEntity(
                                        eventId = eventId,
                                        attendeeId = it?.id ?: 0L,
                                        isPresent = false
                                    )
                                }
                            )

                            Toast.makeText(
                                context,
                                "Attendees added successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BluePrimary
                    ),
                    enabled = selectedAttendees.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null)
                    Text(text = " Done")
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    )
}

@Composable
private fun ColumnScope.AddAllCheckBox(
    useAll: Boolean,
    selectedAttendees: SnapshotStateList<AttendeeEntity?>,
    attendees: List<AttendeeEntity>,
    onAllUsed: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.align(Alignment.End)
    ) {
        Text(text = "Add All", color = Color.Black)

        Checkbox(
            checked = useAll,
            onCheckedChange = { used ->
                onAllUsed(used)
                if (used) selectedAttendees.addAll(attendees)
                else selectedAttendees.clear()
            },
            colors = CheckboxDefaults.colors(
                uncheckedColor = BluePrimary,
                checkedColor = BluePrimary,
                checkmarkColor = Color.White
            )
        )
    }
}

private fun filterAttendees(
    uiState: ExistingAttendeeUiState,
    existingIds: Set<Long>,
    searchQuery: String
): List<AttendeeEntity> {
    return (uiState as ExistingAttendeeUiState.Success).attendees
        .filterNot { it.id in existingIds }
        .filter {
            it.fullName.contains(searchQuery, true) || it.studentId.contains(searchQuery, true)
        }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawGradient(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(Modifier.drawGradient())
    }
}

@Composable
fun ErrorState(message: String, onRetry: () -> Unit) {
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
private fun AttendeeRow(attendee: AttendeeEntity, isSelected: Boolean, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(text = attendee.fullName, fontWeight = FontWeight.Black)
        },
        supportingContent = {
            Text(text = "${attendee.studentId} • ${attendee.course.orEmpty()}")
        },
        trailingContent = {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.Close else Icons.Default.AddCircle,
                    contentDescription = "Add",
                    tint = if (isSelected) Color.Red else BluePrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
            headlineColor = Color.Black,
            supportingColor = Color.DarkGray
        )
    )

    HorizontalDivider(modifier = Modifier.drawGradient())
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
                modifier = Modifier
                    .size(64.dp)
                    .drawGradient()
            )
            Text(
                text = "No attendees found",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
            )
            Text(
                text = "Try a different name or ID",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}