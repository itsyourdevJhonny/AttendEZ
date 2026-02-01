package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.repository.AddAttendeeResult
import com.project.attendez.data.local.repository.AttendanceRepository
import com.project.attendez.data.local.util.AttendanceSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    fun attendance(eventId: Long): StateFlow<List<AttendanceEntity>> =
        repository.getAttendance(eventId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun summary(eventId: Long): StateFlow<AttendanceSummary> =
        repository.getSummary(eventId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                AttendanceSummary(0, 0)
            )

    fun markAttendance(eventId: Long, attendeeId: Long, isPresent: Boolean) {
        viewModelScope.launch {
            repository.mark(eventId, attendeeId, isPresent)
        }
    }

    fun getAttendanceByAttendee(
        eventId: Long,
        attendeeId: Long
    ): StateFlow<AttendanceEntity?> =
        repository.getAttendanceByAttendee(eventId, attendeeId)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                null
            )

    fun updateAttendanceStatus(
        eventId: Long,
        attendeeId: Long,
        isPresent: Boolean
    ) {
        viewModelScope.launch {
            repository.mark(eventId, attendeeId, isPresent)
        }
    }

    fun deleteAttendance(eventId: Long, attendeeId: Long) {
        viewModelScope.launch {
            repository.delete(eventId, attendeeId)
        }
    }


    fun addAttendeeToEvent(
        eventId: Long,
        studentId: String,
        fullName: String,
        course: String,
        yearLevel: Int,
        onResult: (AddAttendeeResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.addAttendeeAndMarkAttendance(
                eventId,
                studentId,
                fullName,
                course,
                yearLevel
            )
            onResult(result)
        }
    }
}
