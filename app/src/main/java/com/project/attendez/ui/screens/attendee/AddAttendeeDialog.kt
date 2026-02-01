package com.project.attendez.ui.screens.attendee

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.data.local.repository.AddAttendeeResult
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.util.drawGradient
import com.project.attendez.viewmodel.AttendanceViewModel

@Composable
fun AddAttendeeDialog(eventId: Long, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    var studentId by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var yearLevel by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    val isValid by remember {
        derivedStateOf {
            studentId.isNotBlank() && fullName.isNotBlank() && course.isNotBlank() && yearLevel != 0
        }
    }

    Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Header()

                        HorizontalDivider()

                        InputFields(
                            studentId,
                            fullName,
                            course,
                            onStudentIdChange = { studentId = it },
                            onFullNameChange = { fullName = it },
                            onCourseChange = { course = it }
                        )

                        YearLevel(yearLevel) { yearLevel = it }

                        Spacer(modifier = Modifier.height(8.dp))

                        AddAttendeeButton(
                            isValid,
                            context,
                            attendanceViewModel,
                            eventId,
                            studentId,
                            fullName,
                            course,
                            yearLevel,
                            onDismiss,
                            isLoading,
                            onLoading = { isLoading = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddAttendeeButton(
    isValid: Boolean,
    context: Context,
    attendanceViewModel: AttendanceViewModel,
    eventId: Long,
    studentId: String,
    fullName: String,
    course: String,
    yearLevel: Int,
    onDismiss: () -> Unit,
    isLoading: Boolean,
    onLoading: (Boolean) -> Unit
) {
    Button(
        onClick = {
            if (!isValid) {
                Toast.makeText(context, "Please complete the fields.", Toast.LENGTH_SHORT).show()
                return@Button
            }

            onLoading(true)

            attendanceViewModel.addAttendeeToEvent(
                eventId = eventId,
                studentId = studentId,
                fullName = fullName,
                course = course,
                yearLevel = yearLevel
            ) { result ->
                onLoading(false)

                Toast.makeText(
                    context,
                    when (result) {
                        AddAttendeeResult.New -> "Student added successfully."
                        AddAttendeeResult.Existing -> "Student already exists. Added to event."
                    },
                    Toast.LENGTH_SHORT
                ).show()

                onDismiss()
            }
        },
        enabled = isValid && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(20.dp)
                    .drawGradient()
            )
        } else {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Add Attendee")
        }
    }
}

@Composable
fun YearLevel(yearLevel: Int, onYearLevelChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Year Level", fontWeight = FontWeight.SemiBold)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(1, 2, 3, 4).forEach { level ->
                FilterChip(
                    selected = yearLevel == level,
                    onClick = { onYearLevelChange(level) },
                    label = {
                        Text(
                            when (level) {
                                1 -> "1st"
                                2 -> "2nd"
                                3 -> "3rd"
                                else -> "4th"
                            }
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun InputFields(
    studentId: String,
    fullName: String,
    course: String,
    onStudentIdChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onCourseChange: (String) -> Unit
) {
    listOf(
        "Student ID" to R.drawable.student_id,
        "Full Name" to R.drawable.full_name,
        "Course" to R.drawable.course
    ).forEach { (label, icon) ->
        OutlinedTextField(
            value = when (label) {
                "Student ID" -> studentId
                "Full Name" -> fullName
                else -> course
            },
            onValueChange = {
                when (label) {
                    "Student ID" -> onStudentIdChange(it)
                    "Full Name" -> onFullNameChange(it)
                    else -> onCourseChange(it)
                }
            },
            label = { Text(label) },
            leadingIcon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Header() {
    Column {
        Text(
            text = "Add Attendee",
            style = MaterialTheme.typography.headlineSmall.copy(brush = BackgroundGradient),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Enter student details below",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}