package com.project.attendez.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.ui.screens.attendance.AttendanceContent
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.AttendeeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(eventId: Long, attendeeId: Long, onBack: () -> Unit) {
    val attendeeViewModel = hiltViewModel<AttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()
    val context = LocalContext.current

    val attendee by remember(attendeeId) {
        attendeeViewModel.getAttendeeById(attendeeId)
    }.collectAsState(initial = null)

    val attendance by remember(attendeeId) {
        attendanceViewModel.getAttendanceByAttendee(eventId, attendeeId)
    }.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { Header(onBack) }
    ) { padding ->
        AttendanceContent(
            attendee,
            padding,
            attendance,
            attendanceViewModel,
            eventId,
            attendeeId,
            onDelete = { showDeleteDialog = true }
        )
    }

    if (showDeleteDialog) {
        DeleteDialog(attendanceViewModel, eventId, attendeeId, context, onBack) {
            showDeleteDialog = false
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Attendance") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
private fun DeleteDialog(
    attendanceViewModel: AttendanceViewModel,
    eventId: Long,
    attendeeId: Long,
    context: Context,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Remove Attendee") },
        text = { Text("This will remove the attendee from this event. Continue?") },
        confirmButton = {
            TextButton(
                onClick = {
                    attendanceViewModel.deleteAttendance(eventId, attendeeId)
                    Toast.makeText(context, "Attendee removed", Toast.LENGTH_SHORT).show()
                    onDismiss()
                    onBack()
                },
                content = { Text("Delete") }
            )
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
