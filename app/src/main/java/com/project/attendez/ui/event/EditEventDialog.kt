package com.project.attendez.ui.event

import android.widget.Toast
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.viewmodel.EventViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    selectedEvent: EventEntity?,
    eventViewModel: EventViewModel,
    onDismissSheet: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var name by rememberSaveable { mutableStateOf(selectedEvent?.name) }
    var description by rememberSaveable { mutableStateOf(selectedEvent?.description) }
    var selectedDate by rememberSaveable { mutableStateOf(selectedEvent?.date) }

    var showDatePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        AnimatedVisibility(
            visible = true,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut()
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
                        value = name.orEmpty(),
                        onValueChange = { name = it }
                    )

                    InputField(
                        label = "Description (optional)",
                        singleLine = false,
                        value = description.orEmpty(),
                        onValueChange = { description = it }
                    )

                    DatePickerToggleButton(selectedDate) { showDatePicker = true }

                    EditButton(
                        enabled = name != selectedEvent?.name || description != selectedEvent?.description || selectedDate != selectedEvent?.date,
                        onEdit = {
                            selectedEvent?.let { event ->
                                eventViewModel.updateEvent(
                                    event = event.copy(
                                        name = name.orEmpty(),
                                        description = description.orEmpty(),
                                        date = selectedDate ?: selectedEvent.date
                                    )
                                )
                            }

                            Toast.makeText(context, "Event updated", Toast.LENGTH_SHORT).show()

                            onDismiss()
                            onDismissSheet()
                        }
                    )
                }
            }
        }
    }

    if (showDatePicker) {
        EventDatePickerDialog(onDateSelected = { selectedDate = it }) { showDatePicker = false }
    }
}

@Composable
private fun EditButton(enabled: Boolean, onEdit: () -> Unit) {
    Button(
        onClick = onEdit,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Green.copy(alpha = 0.5f),
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.DarkGray
        ),
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null
        )

        Spacer(Modifier.width(8.dp))

        Text(text = "Edit", fontWeight = FontWeight.Black)
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
    onValueChange: (String) -> Unit,
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