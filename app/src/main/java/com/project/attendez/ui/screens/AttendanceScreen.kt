package com.project.attendez.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueTertiary
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
        topBar = {
            TopAppBar(
                title = { Text("Attendance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        attendee?.let { person ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // ─── PROFILE CARD ───────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundGradient, RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Badge(modifier = Modifier.size(56.dp)) {
                        Text(
                            text = person.fullName.first().toString(),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    Column {
                        Text(
                            text = person.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                        Text(
                            text = "${person.course} • Year ${person.yearLevel}",
                            color = Color.Black
                        )
                    }
                }

                HorizontalDivider(color = Color.Black)

                // ─── STATUS CARD ────────────────────────────────────────
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Attendance Status",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Image(
                                painter = painterResource(if (attendance?.isPresent == true) R.drawable.present else R.drawable.pending),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AssistChip(
                                onClick = {
                                    attendanceViewModel.updateAttendanceStatus(
                                        eventId,
                                        attendeeId,
                                        true
                                    )
                                },
                                label = { Text("Present") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (attendance?.isPresent == true) BlueTertiary else MaterialTheme.colorScheme.surface,
                                    labelColor = if (attendance?.isPresent == true) Color.White else Color.Black
                                ),
                                border = BorderStroke(width = 1.dp, color = BlueTertiary)
                            )

                            AssistChip(
                                onClick = {
                                    attendanceViewModel.updateAttendanceStatus(
                                        eventId,
                                        attendeeId,
                                        false
                                    )
                                },
                                label = { Text("Absent") },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = if (attendance?.isPresent == false) Color.Red.copy(
                                        alpha = 0.7f
                                    )
                                    else MaterialTheme.colorScheme.surface,
                                    labelColor = if (attendance?.isPresent == false) Color.White else Color.Black
                                ),
                                border = BorderStroke(width = 1.dp, color = Color.Red)
                            )
                        }
                    }
                }

                // ─── DELETE BUTTON ──────────────────────────────────────
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Red.copy(alpha = 0.7f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Remove Attendee")
                }
            }
        }
    }

    // ─── DELETE CONFIRMATION ─────────────────────────────────────
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Remove Attendee") },
            text = {
                Text("This will remove the attendee from this event. Continue?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        attendanceViewModel.deleteAttendance(eventId, attendeeId)
                        Toast.makeText(
                            context,
                            "Attendee removed",
                            Toast.LENGTH_SHORT
                        ).show()
                        showDeleteDialog = false
                        onBack()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
