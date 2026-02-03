package com.project.attendez.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.ui.attendance.AttendanceContent
import com.project.attendez.ui.theme.BluePrimary
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
        containerColor = Color.White,
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
        title = {
            Text(
                text = "Remove Attendee",
                color = Color.Black,
                fontWeight = FontWeight.Black
            )
        },
        text = {
            Text(
                text = "This will remove the attendee from this event. Continue?",
                color = Color.DarkGray
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    attendanceViewModel.deleteAttendance(eventId, attendeeId)
                    Toast.makeText(context, "Attendee removed", Toast.LENGTH_SHORT).show()
                    onDismiss()
                    onBack()
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                content = {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text(text = "Delete")
                }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White,
                    containerColor = BluePrimary
                )
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(text = "Cancel")
            }
        },
        containerColor = Color.White
    )
}
