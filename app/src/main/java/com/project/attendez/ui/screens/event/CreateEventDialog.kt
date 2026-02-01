package com.project.attendez.ui.screens.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.project.attendez.R
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.util.BackIcon
import java.time.LocalDate
import java.time.Year

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
            .padding(top = 24.dp/*, start = 16.dp, end = 16.dp*/)
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
