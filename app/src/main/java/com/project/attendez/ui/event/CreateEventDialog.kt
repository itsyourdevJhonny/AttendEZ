package com.project.attendez.ui.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, date: LocalDate, description: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val isFormValid = name.isNotBlank()

    AnimatedVisibility(
        visible = true,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Header(onDismiss)

                InputField(
                    label = "Event name",
                    singleLine = true,
                    value = name,
                    onValueChange = { name = it }
                )

                InputField(
                    label = "Description (optional)",
                    singleLine = false,
                    value = description,
                    onValueChange = { description = it }
                )


                DatePickerToggleButton(selectedDate) { showDatePicker = true }

                CreateButton(
                    onCreate,
                    name,
                    selectedDate,
                    description,
                    onDismiss,
                    isFormValid,
                    isLoading,
                    onLoading = { isLoading = it }
                )
            }
        }
    }

    if (showDatePicker) {
        EventDatePickerDialog(onDateSelected = { selectedDate = it }) { showDatePicker = false }
    }
}

@Composable
private fun Header(onDismiss: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Create Attendance",
            style = MaterialTheme.typography.titleLarge.copy(brush = BackgroundGradient),
            fontWeight = FontWeight.Black
        )

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Red
            )
        }
    }
}

@Composable
private fun InputField(
    label: String,
    singleLine: Boolean,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        singleLine = singleLine,
        minLines = 3,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Black,
            unfocusedLabelColor = Color.Gray,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black,
            focusedLabelColor = BluePrimary,
            focusedBorderColor = BluePrimary
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun DatePickerToggleButton(selectedDate: LocalDate?, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selectedDate != LocalDate.now()) BluePrimary else Color.Transparent,
            contentColor = if (selectedDate != LocalDate.now()) Color.White else BluePrimary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (selectedDate != LocalDate.now()) Color.Transparent else BluePrimary
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text = selectedDate.toString())
    }
}

@Composable
private fun CreateButton(
    onCreate: (String, LocalDate, String) -> Unit,
    name: String,
    selectedDate: LocalDate,
    description: String,
    onDismiss: () -> Unit,
    isFormValid: Boolean,
    isLoading: Boolean,
    onLoading: (Boolean) -> Unit
) {
    Button(
        onClick = {
            onLoading(true)
            onCreate(name.trim(), selectedDate, description.trim())
            onLoading(false)
            onDismiss()
        },
        enabled = isFormValid && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = BluePrimary,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Text(text = "Create", fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDatePickerDialog(onDateSelected: (LocalDate) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        )
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                ),
                content = { Text("OK") }
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                )
            ) { Text("Cancel") }
        },
        colors = DatePickerDefaults.colors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        DatePicker(
            state = datePickerState,
            title = {
                Text(
                    text = "Select Date",
                    color = Color.Black,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(
                        PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
                    )
                )
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                headlineContentColor = BluePrimary,
                weekdayContentColor = Color.Black,
                subheadContentColor = Color.Black,
                navigationContentColor = Color.Black,
                yearContentColor = Color.Black,
                currentYearContentColor = BluePrimary,
                selectedYearContentColor = Color.White,
                selectedYearContainerColor = BluePrimary,
                dayContentColor = Color.Black,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = BluePrimary,
                todayContentColor = BluePrimary,
                todayDateBorderColor = BluePrimary,
            )
        )
    }
}