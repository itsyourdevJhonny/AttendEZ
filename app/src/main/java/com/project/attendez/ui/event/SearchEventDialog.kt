package com.project.attendez.ui.event

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.util.drawGradient

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchEventDialog(
    selectedTabIndex: Int,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredEvents: List<EventEntity>,
    onDismiss: () -> Unit,
    onEventClick: (Long) -> Unit,
    onEventSelected: (EventEntity) -> Unit,
    onDelete: () -> Unit
) {
    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.White,
        topBar = { Header(onDismiss) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SearchField(searchQuery, placeholder = "events", onSearchQueryChange)

            if (filteredEvents.isEmpty()) {
                EmptyAttendance(label = "There are no ${if (selectedTabIndex == 0) "ongoing" else "past"} events available at this time.")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = filteredEvents, key = { it.id }) { event ->
                        EventItem(event, onEventClick) {
                            onEventSelected(event)
                            onDelete()
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onDismiss: () -> Unit) {
    TopAppBar(
        title = { Text("Search Event") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = Modifier.clip(RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp))
    )
}

@Composable
fun SearchField(searchQuery: String, placeholder: String, onSearchQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { onSearchQueryChange(it) },
        placeholder = { Text(text = "Search $placeholder...", color = Color.DarkGray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.drawGradient()
            )
        },
        trailingIcon = {
            if (searchQuery.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onSearchQueryChange("") }
                        .drawGradient()
                )
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedLabelColor = Color.DarkGray,
            unfocusedTextColor = Color.Black,
            unfocusedBorderColor = Color.Black,
            focusedTextColor = Color.Black,
            focusedBorderColor = Color.Black,
            cursorColor = Color.Black
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}