package com.project.attendez.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.event.CreateEventDialog
import com.project.attendez.ui.event.EventContent
import com.project.attendez.ui.event.EventHeader
import com.project.attendez.ui.event.SearchEventDialog
import com.project.attendez.viewmodel.EventViewModel
import java.time.LocalDate

@Composable
fun EventScreen(onEventClick: (Long) -> Unit, onHistory: () -> Unit) {
    val eventViewModel = hiltViewModel<EventViewModel>()
    var showCreateDialog by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }

    val events by eventViewModel.events.collectAsState()
    val isLoading by eventViewModel.isLoading.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedEvent by remember { mutableStateOf<EventEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteSheet by remember { mutableStateOf(false) }

    val today = LocalDate.now()
    val ongoingEvents = events.filter { it.date >= today }.sortedByDescending { it.createdAt }
    val pastEvents = events.filter { it.date < today }

    val filteredEvents = (if (selectedTabIndex == 0) ongoingEvents else pastEvents)
        .filter { it.name.contains(searchQuery, ignoreCase = true) }

    Box {
        Scaffold(
            containerColor = Color.White,
            modifier = Modifier.padding(top = 38.dp),
            topBar = { EventHeader() }
        ) { paddingValues ->
            EventContent(
                paddingValues,
                eventViewModel,
                selectedTabIndex,
                ongoingEvents,
                pastEvents,
                filteredEvents,
                selectedEvent,
                isLoading,
                showDeleteSheet,
                onTabSelected = { selectedTabIndex = it },
                onEventSelected = { selectedEvent = it },
                onShowSheet = { showDeleteSheet = it },
                onEventClick = onEventClick,
                onHistory = onHistory,
                onCreate = { showCreateDialog = true },
                onSearching = { isSearching = it }
            )
        }

        AnimatedVisibility(visible = showCreateDialog) {
            CreateEventDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { name, date, description ->
                    eventViewModel.createEvent(name, date, description)
                    showCreateDialog = false
                }
            )
        }

        AnimatedVisibility(visible = isSearching) {
            SearchEventDialog(
                selectedTabIndex,
                searchQuery,
                onSearchQueryChange = { searchQuery = it },
                filteredEvents,
                onDismiss = { isSearching = false },
                onEventClick,
                onEventSelected = { selectedEvent = it },
                onDelete = { showDeleteSheet = true }
            )
        }
    }
}