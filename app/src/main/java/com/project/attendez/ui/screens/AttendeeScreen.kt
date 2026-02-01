package com.project.attendez.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.R
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.screens.attendee.AddAttendeeDialog
import com.project.attendez.ui.screens.attendee.ExistingAttendeeDialog
import com.project.attendez.ui.screens.event.EmptyAttendance
import com.project.attendez.ui.theme.BackgroundGradient
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.BlueSecondary
import com.project.attendez.ui.util.drawGradient
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.AttendeeViewModel
import com.project.attendez.viewmodel.EventViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendeeScreen(eventId: Long, onAttendance: (Long, Long) -> Unit, onBack: () -> Unit) {
    Scaffold(
        containerColor = BlueSecondary.copy(alpha = 0.3f),
        topBar = { Header(onBack) }
    ) { paddingValues ->
        AttendeeContent(paddingValues, eventId, onAttendance)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Manage Attendee") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}

@Composable
fun AttendeeContent(
    paddingValues: PaddingValues,
    eventId: Long,
    onAttendance: (Long, Long) -> Unit
) {
    val eventViewModel = hiltViewModel<EventViewModel>()
    val attendeeViewModel = hiltViewModel<AttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val event by remember(eventId) {
        eventViewModel.getEventById(eventId)
    }.collectAsState(initial = null)

    val attendance by remember(eventId) {
        attendanceViewModel.attendance(eventId)
    }.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showExistingDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Header()

        EventCard(event)

        Spacer(Modifier.height(16.dp))

        ActionBars(onExisting = { showExistingDialog = true }, onAdd = { showAddDialog = true })

        Spacer(Modifier.height(20.dp))

        AttendeeHeader(
            attendanceSize = attendance.size,
            onExisting = { showExistingDialog = true }
        )

        Spacer(Modifier.height(8.dp))

        if (attendance.isEmpty()) {
            EmptyAttendance(label = "No attendees yet.\nAdd students to start tracking attendance.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(attendance) { record ->
                    val attendee by remember(record.attendeeId) {
                        attendeeViewModel.getAttendeeById(record.attendeeId)
                    }.collectAsState(initial = null)

                    attendee?.let { person ->
                        AttendeeItem(
                            attendee = person,
                            isPresent = record.isPresent,
                            onClick = { onAttendance(eventId, person.id) }
                        )
                    }
                }
            }
        }
    }

    when {
        showAddDialog -> AddAttendeeDialog(eventId) { showAddDialog = false }
        showExistingDialog -> ExistingAttendeeDialog(eventId, attendance) {
            showExistingDialog = false
        }
    }
}

@Composable
private fun Header() {
    Text(
        text = "Event Attendance",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun EventCard(event: EventEntity?) {
    event?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(BackgroundGradient)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = it.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = it.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.date),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Text(text = "${it.date}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
fun AttendeeHeader(attendanceSize: Int, onExisting: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Attendees ($attendanceSize)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier
                .drawGradient()
                .clickable { onExisting() }
        )
    }
}

@Composable
fun ActionBars(onExisting: () -> Unit, onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onExisting,
            border = BorderStroke(width = 1.dp, color = BluePrimary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Use Existing")
        }

        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(14.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.add),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text("Add New")
        }
    }
}

@Composable
private fun AttendeeItem(
    attendee: AttendeeEntity,
    isPresent: Boolean? = null,
    @DrawableRes trailingIcon: Int? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Badge(modifier = Modifier.size(40.dp)) {
                    Text(
                        text = attendee.fullName.first().toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column {
                    Text(text = attendee.fullName, fontWeight = FontWeight.SemiBold)

                    Row {
                        Text(
                            text = attendee.course ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = " / ${
                                when (attendee.yearLevel) {
                                    1 -> "1st"
                                    2 -> "2nd"
                                    3 -> "3rd"
                                    else -> "4th"
                                }
                            } Year",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (isPresent != null) {
                Image(
                    painter = painterResource(if (isPresent) R.drawable.present else R.drawable.pending),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                trailingIcon?.let { icon ->
                    Image(
                        painter = painterResource(icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
