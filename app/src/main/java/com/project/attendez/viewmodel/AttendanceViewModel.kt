package com.project.attendez.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.data.local.repository.AddAttendeeResult
import com.project.attendez.data.local.repository.AttendanceRepository
import com.project.attendez.data.local.util.AttendanceWithAttendee
import com.project.attendez.ui.util.BulkImportUtils.parseExcel
import com.project.attendez.ui.util.ImportPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repository: AttendanceRepository,
) : ViewModel() {
    private val _uiState =
        MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState

    private val _previewList = MutableStateFlow<List<ImportPreview>>(emptyList())
    val previewList: StateFlow<List<ImportPreview>> = _previewList

    /*fun loadPreview(context: Context, uri: Uri) {
        viewModelScope.launch {
            _previewList.value = parseExcel(context, uri)
        }
    }*/

    fun loadPreview(context: Context, uri: Uri, eventId: Long) {
        viewModelScope.launch {
            val rawList = parseExcel(context, uri)

            val existingIds = repository.getExistingAttendeeIds(eventId).toSet()
            val attendeeMap = repository.getAttendeeMap()

            val seen = mutableSetOf<String>()

            _previewList.value = rawList.map { item ->
                val studentId = item.studentId

                val duplicateInFile = studentId?.let { !seen.add(it) } ?: false

                val attendeeId = studentId?.let { attendeeMap[it] }
                val duplicateInDb = attendeeId != null && attendeeId in existingIds

                val isDuplicate = duplicateInFile || duplicateInDb

                item.copy(
                    isDuplicate = isDuplicate,
                    reason = when {
                        duplicateInFile -> "Duplicate in Excel file"
                        duplicateInDb -> "Already exists"
                        else -> null
                    }
                )
            }
        }
    }

    fun clearPreview() {
        _previewList.value = emptyList()
    }

    fun importToDatabase(eventId: Long) {
        viewModelScope.launch {
            _previewList.value.forEach { item ->

                if (item.studentId == null || item.fullName == null) return@forEach

                repository.addAttendeeAndMarkAttendance(
                    eventId = eventId,
                    studentId = item.studentId,
                    fullName = item.fullName,
                    course = item.course,
                    yearLevel = item.yearLevel,
                    status = item.status ?: AttendanceStatus.ABSENT
                )
            }

            _previewList.value = emptyList()
        }
    }

    fun loadEventAttendance(eventId: Long) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val now = LocalDateTime.now()
                val startOfDay = now.toLocalDate().atStartOfDay()
                val endOfDay = now.toLocalDate().atTime(23, 59, 59)
                val attendees = repository.getAttendanceByEventAndDate(eventId, startOfDay, endOfDay).first()
                _uiState.value = AttendanceUiState.Success(attendees)
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(
                    e.message ?: "Failed to load attendees"
                )
            }
        }
    }

    fun getAttendanceByEventAndDate(eventId: Long): StateFlow<List<AttendanceEntity>> {
        val now = LocalDateTime.now()
        val startOfDay = now.toLocalDate().atStartOfDay()
        val endOfDay = now.toLocalDate().atTime(23, 59, 59)
        return repository.getAttendanceByEventAndDate(eventId, startOfDay, endOfDay)
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    fun attendance(eventId: Long): StateFlow<List<AttendanceEntity>> =
        repository.getAttendance(eventId)
            .distinctUntilChanged()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addMultipleAttendance(attendance: List<AttendanceEntity>) {
        viewModelScope.launch { repository.addAll(attendance) }
    }

    fun getAttendanceByAttendee(
        eventId: Long,
        attendeeId: Long,
    ): StateFlow<AttendanceEntity?> =
        repository.getAttendanceByAttendee(eventId, attendeeId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun updateAttendanceStatus(
        eventId: Long,
        attendeeId: Long,
        status: AttendanceStatus,
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
        status: AttendanceStatus,
        onResult: (AddAttendeeResult) -> Unit,
    ) {
        viewModelScope.launch {
            val result = repository.addAttendeeAndMarkAttendance(
                eventId,
                studentId,
                fullName,
                course,
                yearLevel,
                status
            )
            onResult(result)
        }
    }

    suspend fun getDailyStats(eventId: Long, date: LocalDateTime): DailyStats =
        repository.getDailyStats(eventId, date)

    suspend fun getAllDaysStats(event: EventEntity): List<Pair<LocalDateTime, DailyStats>> =
        repository.getAllDaysStats(event)

    suspend fun getDailyGroupedAttendance(
        eventId: Long,
        date: LocalDateTime,
    ): Map<AttendanceStatus, List<AttendanceWithAttendee>> =
        repository.getDailyGroupedAttendance(eventId, date)
}

data class DailyStats(
    val present: Int,
    val absent: Int,
    val excuse: Int,
)

sealed interface AttendanceUiState {
    object Loading : AttendanceUiState
    data class Success(val attendance: List<AttendanceEntity>) : AttendanceUiState
    data class Error(val message: String) : AttendanceUiState
}
