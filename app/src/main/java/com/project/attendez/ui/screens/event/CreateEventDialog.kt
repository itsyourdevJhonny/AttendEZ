package com.project.attendez.ui.screens.event

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.project.attendez.R
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueTertiary
import com.project.attendez.ui.util.BackIcon
import java.time.Instant
import java.time.LocalDate
import java.time.Year
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

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    /** HEADER **/
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Create Attendance",
                            style = MaterialTheme.typography.titleLarge.copy(brush = BackgroundGradient),
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    /** EVENT NAME **/
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Event name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    /** DESCRIPTION **/
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description (optional)") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    /** DATE PICKER **/
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selectedDate != LocalDate.now()) BlueTertiary else Color.Transparent,
                            contentColor = if (selectedDate != LocalDate.now()) Color.White else BlueTertiary
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (selectedDate != LocalDate.now()) Color.Transparent else BlueTertiary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = selectedDate.toString())
                    }

                    /** ACTION BUTTON **/
                    Button(
                        onClick = {
                            isLoading = true
                            onCreate(name.trim(), selectedDate, description.trim())
                            isLoading = false
                            onDismiss()
                        },
                        enabled = isFormValid && !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text(
                                text = "Create",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    /** DATE PICKER DIALOG **/
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate =
                            Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
            shape = RectangleShape
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, date: LocalDate, description: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGradient)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EventDialogHeader(label = "Create Attendance") { onDismiss() }

        InputField(
            text = name,
            placeholder = "Name",
            shape = CircleShape,
            singleLine = true,
            onValueChange = { name = it }
        )

        InputField(
            text = description,
            placeholder = "Description",
            shape = RoundedCornerShape(8.dp),
            height = 160.dp,
            onValueChange = { description = it }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .clickable { showDatePicker = true }
                .background(Color.White, CircleShape)
                .padding(16.dp)
        ) {
            Text(text = "Select Date")

            Image(
                painter = painterResource(R.drawable.date),
                contentDescription = "Date",
                modifier = Modifier.size(16.dp)
            )
        }

        Button(
            onClick = {
                onCreate(name, selectedDate, description)
                onDismiss()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BluePrimary,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(R.drawable.create),
                contentDescription = "Create",
                modifier = Modifier.size(28.dp)
            )

            Text(text = " Create", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            shape = RectangleShape,
            confirmButton = {},
            colors = DatePickerDefaults.colors(
                containerColor = Color.Transparent
            )
        ) {
            DatePickerField { selectedDate = it; showDatePicker = false }
        }
    }
}

@Composable
private fun InputField(
    text: String,
    placeholder: String,
    shape: Shape,
    height: Dp = Dp.Unspecified,
    singleLine: Boolean = false,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        singleLine = singleLine,
        placeholder = { Text(text = " $placeholder...") },
        suffix = {
            if (text.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = BluePrimary,
                    modifier = Modifier.clickable { onValueChange("") }
                )
            }
        },
        shape = shape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Transparent,
            unfocusedPlaceholderColor = Color.Gray,
            focusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            focusedLabelColor = Color.DarkGray,
            focusedTextColor = Color.Black,
            cursorColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
fun EventDialogHeader(
    label: String = "",
    okayLabel: String = "Okay",
    enabled: Boolean = true,
    onOkay: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = if (onOkay == null) 16.dp else 0.dp)
            .fillMaxWidth()
            .padding(top = 24.dp*/
/*, start = 16.dp, end = 16.dp*//*
)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BackIcon(onClick = onBack)

            if (label.isNotBlank()) {
                Text(text = label, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (onOkay != null) {
            TextButton(
                onClick = onOkay,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BluePrimary,
                    contentColor = Color.White
                ),
                enabled = enabled,
                content = { Text(text = okayLabel, fontWeight = FontWeight.Bold) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(onDateSelected: (LocalDate) -> Unit) {
    val datePickerState = rememberDatePickerState(
        yearRange = IntRange(Year.now().value, 2100)
    )

    var selectedDate: LocalDate?

    datePickerState.selectedDateMillis?.let { dateMillis ->
        selectedDate = LocalDate.ofEpochDay(dateMillis / (1000 * 60 * 60 * 24))
        onDateSelected(selectedDate)
    }

    EventDatePicker(datePickerState)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EventDatePicker(datePickerState: DatePickerState) {
    DatePicker(
        title = null,
        showModeToggle = false,
        headline = null,
        state = datePickerState,
        colors = DatePickerDefaults.colors(
            headlineContentColor = Color.White,
            containerColor = Color.Transparent,
            weekdayContentColor = Color.White,
            yearContentColor = Color.Black,
            selectedDayContainerColor = Color.Transparent,
            dayInSelectionRangeContainerColor = Color.White,
            dayInSelectionRangeContentColor = Color.White,
            todayContentColor = Color.White,
            todayDateBorderColor = Color.White,
            dayContentColor = Color.White,
            navigationContentColor = Color.White,
            currentYearContentColor = Color.White,
            selectedYearContentColor = Color.Black,
            selectedYearContainerColor = Color.White,
            dividerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    )
}
*/
