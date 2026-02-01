package com.project.attendez.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.ui.screens.event.CreateEventDialog
import com.project.attendez.ui.screens.event.EventContent
import com.project.attendez.ui.screens.event.EventHeader
import com.project.attendez.viewmodel.EventViewModel

@Composable
fun EventScreen(onEventClick: () -> Unit) {
    val eventViewModel = hiltViewModel<EventViewModel>()
    var showDialog by remember { mutableStateOf(false) }

    Box {
        Scaffold(
            containerColor = Color.White,
            modifier = Modifier.padding(top = 38.dp),
            topBar = { EventHeader() }
        ) { paddingValues ->
            EventContent(paddingValues, eventViewModel, onEventClick) { showDialog = it }
        }

        AnimatedVisibility(visible = showDialog) {
            CreateEventDialog(
                onDismiss = { showDialog = false },
                onCreate = { name, date, description ->
                    eventViewModel.createEvent(name, date, description)
                    showDialog = false
                }
            )
        }
    }
}