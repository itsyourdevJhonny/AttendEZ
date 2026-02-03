package com.project.attendez.ui.attendee

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.ui.event.EmptyAttendance
import com.project.attendez.ui.event.SearchField
import com.project.attendez.ui.screens.AttendeeItem
import com.project.attendez.ui.theme.BluePrimary
import com.project.attendez.ui.theme.Typography
import com.project.attendez.ui.util.AttendeeUtils
import com.project.attendez.viewmodel.AttendanceUiState
import com.project.attendez.viewmodel.AttendanceViewModel
import com.project.attendez.viewmodel.AttendeeViewModel

@Composable
fun SearchAttendeeDialog(
    eventId: Long,
    attendance: List<AttendanceEntity>,
    onAttendance: (Long, Long) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    Scaffold(
        containerColor = Color.White,
        topBar = { Header { onDismiss() } }
    ) { paddingValues ->
        SearchAttendeeContent(paddingValues, eventId, attendance, onAttendance)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun Header(onBack: () -> Unit) {
    TopAppBar(
        title = { Text("Search Attendee") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        modifier = Modifier.clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
    )
}

@Composable
private fun SearchAttendeeContent(
    paddingValues: PaddingValues,
    eventId: Long,
    attendance: List<AttendanceEntity>,
    onAttendance: (Long, Long) -> Unit
) {
    val attendeeViewModel = hiltViewModel<AttendeeViewModel>()
    val attendanceViewModel = hiltViewModel<AttendanceViewModel>()

    val uiState by attendanceViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { attendanceViewModel.loadEventAttendance(eventId) }

    var searchQuery by remember { mutableStateOf("") }

    val attendees = attendeeViewModel.getAttendeesByEventId(eventId)
        .collectAsState(initial = emptyList()).value.associateBy { it?.id }
    val sortedAttendance by sortAttendance(attendance, attendees)
    val filteredAttendance by filterAttendance(searchQuery, sortedAttendance, attendees)

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "You can search by Full Name, Student ID, Course, or Year Level.",
            color = Color.Black,
            style = Typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        )

        SearchField(searchQuery, placeholder = "attendee") { searchQuery = it }

        HorizontalDivider(Modifier.padding(top = 8.dp))

        when (uiState) {
            AttendanceUiState.Loading -> LoadingState()

            is AttendanceUiState.Error -> {
                ErrorState(
                    message = (uiState as AttendanceUiState.Error).message,
                    onRetry = { attendanceViewModel.loadEventAttendance(eventId) }
                )
            }

            is AttendanceUiState.Success -> {
                val attendance = (uiState as AttendanceUiState.Success).attendance

                if (filteredAttendance.isEmpty()) {
                    EmptyAttendance(label = "No attendees found.")
                } else {
                    val attendees = attendeeViewModel.getAttendeesByEventId(eventId)
                        .collectAsState(initial = emptyList()).value.associateBy { it?.id }
                    val sortedAttendance by sortAttendance(attendance, attendees)
                    val filteredAttendance by filterAttendance(
                        searchQuery,
                        sortedAttendance,
                        attendees
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = filteredAttendance, key = { it.attendeeId }) { record ->
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
        }
    }
}

private fun sortAttendance(
    attendance: List<AttendanceEntity>,
    attendees: Map<Long?, AttendeeEntity?>
) = derivedStateOf {
    attendance.sortedWith(
        comparator = compareByDescending<AttendanceEntity> { it.isPresent }
            .thenBy { attendees[it.attendeeId]?.fullName?.lowercase() }
    )
}

private fun filterAttendance(
    searchQuery: String,
    sortedAttendance: List<AttendanceEntity>,
    attendees: Map<Long?, AttendeeEntity?>
) = derivedStateOf {
    if (searchQuery.isBlank()) sortedAttendance else sortedAttendance
        .filter { attendance ->
            val attendee = attendees[attendance.attendeeId]

            attendee?.let {
                val inFullName = it.fullName.contains(other = searchQuery, ignoreCase = true)
                val inStudentId = it.studentId.contains(other = searchQuery, ignoreCase = true)
                val inCourse = it.course?.contains(other = searchQuery, ignoreCase = true) == true
                val inYearLevel = "${AttendeeUtils.getYearLevel(it.yearLevel)} Year"
                    .contains(searchQuery, ignoreCase = true)

                inFullName || inStudentId || inCourse || inYearLevel
            } == true
        }

}