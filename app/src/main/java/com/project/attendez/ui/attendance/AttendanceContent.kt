package com.project.attendez.ui.attendance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.attendez.R
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueTertiary
import com.project.attendez.viewmodel.AttendanceViewModel

@Composable
fun AttendanceContent(
    attendee: AttendeeEntity?,
    padding: PaddingValues,
    attendance: AttendanceEntity?,
    attendanceViewModel: AttendanceViewModel,
    eventId: Long,
    attendeeId: Long,
    onDelete: () -> Unit
) {
    attendee?.let { person ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ProfileCard(person)
            HorizontalDivider(color = Color.Black)
            StatusCard(attendance, attendanceViewModel, eventId, attendeeId)
            DeleteButton(onDelete)
        }
    }
}

@Composable
private fun ProfileCard(person: AttendeeEntity) {
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
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
        }

        Column {
            Text(
                text = person.fullName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Black
            )

            Text(text = "${person.course} • Year ${person.yearLevel}", color = Color.White)
        }
    }
}

@Composable
private fun StatusCard(
    attendance: AttendanceEntity?,
    attendanceViewModel: AttendanceViewModel,
    eventId: Long,
    attendeeId: Long
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BlueTertiary)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Attendance Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Image(
                    painter = painterResource(if (attendance?.isPresent == true) R.drawable.present else R.drawable.pending),
                    contentDescription = null,
                    colorFilter = if (attendance?.isPresent == true) null else ColorFilter.tint(
                        color = Color.Red
                    ),
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
                            isPresent = true
                        )
                    },
                    label = { Text("Present") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (attendance?.isPresent == true) BluePrimary else Color.White,
                        labelColor = if (attendance?.isPresent == true) Color.White else Color.Black
                    ),
                    border = BorderStroke(width = 1.dp, color = BluePrimary)
                )

                AssistChip(
                    onClick = {
                        attendanceViewModel.updateAttendanceStatus(
                            eventId,
                            attendeeId,
                            isPresent = false
                        )
                    },
                    label = { Text("Absent") },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (attendance?.isPresent == false) Color.Red
                        else Color.White,
                        labelColor = if (attendance?.isPresent == false) Color.White else Color.Black
                    ),
                    border = BorderStroke(width = 1.dp, color = Color.Red)
                )
            }
        }
    }
}

@Composable
private fun DeleteButton(onDelete: () -> Unit) {
    Button(
        onClick = onDelete,
        modifier = Modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Red.copy(alpha = 0.7f),
            contentColor = Color.White
        )
    ) {
        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.White)
        Spacer(Modifier.width(8.dp))
        Text(text = "Remove Attendee", color = Color.White)
    }
}
