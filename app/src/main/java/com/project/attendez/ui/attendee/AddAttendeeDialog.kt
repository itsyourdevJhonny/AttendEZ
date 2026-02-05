package com.project.attendez.ui.attendee

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.repository.AddAttendeeResult
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueSecondary
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
    var isPresent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val isValid by remember {
        derivedStateOf {
            studentId.isNotBlank() && fullName.isNotBlank() && course.isNotBlank() && yearLevel != 0
        }
    }

    BackHandler { onDismiss() }

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
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(24.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Header(onDismiss)

                HorizontalDivider(Modifier.drawGradient())

                InputFields(
                    studentId,
                    fullName,
                    course,
                    onStudentIdChange = { studentId = it },
                    onFullNameChange = { fullName = it },
                    onCourseChange = { course = it }
                )

                HorizontalDivider(Modifier.drawGradient())

                YearLevel(yearLevel) { yearLevel = it }

                HorizontalDivider(Modifier.drawGradient())

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Mark as Present",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Switch(
                        checked = isPresent,
                        onCheckedChange = { isPresent = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BluePrimary,
                            uncheckedThumbColor = BluePrimary,
                            uncheckedTrackColor = Color.White,
                            uncheckedBorderColor = BlueSecondary
                        )
                    )
                }

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
                    isPresent,
                    onDismiss,
                    isLoading,
                    onLoading = { isLoading = it }
                )
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
    isPresent: Boolean,
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
                eventId,
                studentId,
                fullName,
                course,
                yearLevel,
                isPresent,
                status = if (isPresent) AttendanceStatus.PRESENT else AttendanceStatus.ABSENT
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
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            disabledContentColor = Color.DarkGray,
            disabledContainerColor = Color.Gray,
            containerColor = BluePrimary,
            contentColor = Color.White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier
                    .size(20.dp)
                    .drawGradient()
            )
        } else {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(text = "Add Attendee")
        }
    }
}

@Composable
fun YearLevel(yearLevel: Int, onYearLevelChange: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Year Level", fontWeight = FontWeight.Bold, color = Color.Black)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
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
                            },
                            color = if (yearLevel == level) Color.White else Color.Gray
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White,
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
            label = { Text(text = label) },
            leadingIcon = {
                Image(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Black,
                unfocusedLabelColor = Color.Gray,
                focusedBorderColor = BluePrimary,
                focusedTextColor = Color.Black,
                focusedLabelColor = BluePrimary
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Header(onDismiss: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Text(
                text = "Add Attendee",
                style = MaterialTheme.typography.headlineSmall.copy(brush = BackgroundGradient),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter student details below",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}