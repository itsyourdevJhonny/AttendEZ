package com.project.attendez.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.ui.attendee.AnalyticsBar
import com.project.attendez.ui.history.EventDetailDialog
import com.project.attendez.ui.history.EventHistoryUI
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.Typography
import com.project.attendez.viewmodel.EventViewModel
import com.project.attendez.viewmodel.HistoryViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val viewModel = hiltViewModel<HistoryViewModel>()
    val eventViewModel = hiltViewModel<EventViewModel>()

    val histories by eventViewModel.getEventHistory().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedHistory by remember { mutableStateOf<EventHistoryUI?>(null) }

    Scaffold(
        containerColor = Color.White,
        topBar = { Header(onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F6FA)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(histories) { history ->
                EventHistoryCard(history) { showDialog = true; selectedHistory = it }
            }
        }
    }

    if (showDialog) {
        selectedHistory?.let {
            EventDetailDialog(it) { showDialog = false }
        }
    }
}

@Composable
private fun EventHistoryCard(history: EventHistoryUI, onClick: (EventHistoryUI) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { onClick(history) }
            .padding(16.dp)
    ) {

        Text(
            text = history.event.name,
            style = Typography.titleLarge,
            color = Color.Black
        )

        Text(
            text = "${history.days.size} Days Recorded",
            style = Typography.bodySmall,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        history.days.take(3).forEach { day ->
            AnalyticsBar(
                label = day.dayLabel,
                value = day.present,
                total = day.total,
                color = Color(0xFF4CAF50),
                onClick = {}
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Attendance History") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    )
}



