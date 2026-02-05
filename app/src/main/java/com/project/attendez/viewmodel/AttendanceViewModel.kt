package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.repository.AddAttendeeResult
import com.project.attendez.data.local.repository.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState

    fun loadEventAttendance(eventId: Long) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val attendees = repository.getAttendance(eventId).first()
                _uiState.value = AttendanceUiState.Success(attendees)
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(
                    e.message ?: "Failed to load attendees"
                )
            }
        }
    }

    fun attendance(eventId: Long): StateFlow<List<AttendanceEntity>> =
        repository.getAttendance(eventId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addMultipleAttendance(attendance: List<AttendanceEntity>) {
        viewModelScope.launch { repository.addAll(attendance) }
    }

    fun getAttendanceByAttendee(
        eventId: Long,
        attendeeId: Long
    ): StateFlow<AttendanceEntity?> =
        repository.getAttendanceByAttendee(eventId, attendeeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun updateAttendanceStatus(
        eventId: Long,
        attendeeId: Long,
        status: AttendanceStatus
    ) {
        viewModelScope.launch { repository.mark(eventId, attendeeId, status) }
    }

    fun deleteAttendance(eventId: Long, attendeeId: Long) {
        viewModelScope.launch { repository.delete(eventId, attendeeId) }
    }

    fun deleteAllAttendance(attendance: List<AttendanceEntity>) {
        viewModelScope.launch { repository.deleteAll(attendance) }
    }

    fun addAttendeeToEvent(
        eventId: Long,
        studentId: String,
        fullName: String,
        course: String,
        yearLevel: Int,
        isPresent: Boolean,
        status: AttendanceStatus,
        onResult: (AddAttendeeResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.addAttendeeAndMarkAttendance(
                eventId,
                studentId,
                fullName,
                course,
                yearLevel,
                isPresent,
                status
            )
            onResult(result)
        }
    }
}

sealed interface AttendanceUiState {
    object Loading : AttendanceUiState
    data class Success(val attendance: List<AttendanceEntity>) : AttendanceUiState
    data class Error(val message: String) : AttendanceUiState
}
