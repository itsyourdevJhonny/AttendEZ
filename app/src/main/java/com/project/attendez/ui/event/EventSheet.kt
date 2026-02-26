package com.project.attendez.ui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSheet(
    selectedEvent: EventEntity?,
    eventViewModel: EventViewModel,
    onDismiss: () -> Unit,
) {
    var showEditDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, dragHandle = null) {
        Column(modifier = Modifier.padding(16.dp)) {
            EventSheetItem(label = "Edit", icon = Icons.Default.Edit, color = Color.Green) {
                showEditDialog = true
            }

            EventSheetItem(label = "Delete", icon = Icons.Default.Delete, color = Color.Red) {
                selectedEvent?.let { event ->
                    eventViewModel.deleteEvent(event)
                    onDismiss()
                }
            }
        }
    }

    if (showEditDialog) {
        EditEventDialog(
            selectedEvent,
            eventViewModel,
            onDismissSheet = onDismiss,
            onDismiss = { showEditDialog = false }
        )
    }
}

@Composable
private fun EventSheetItem(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.6f), CircleShape)
                .padding(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = "Delete", tint = Color.White)
        }

        Text(text = label, fontWeight = FontWeight.Bold)
    }
}