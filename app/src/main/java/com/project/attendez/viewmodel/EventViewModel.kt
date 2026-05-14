package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.data.local.repository.AttendanceRepository
import com.project.attendez.data.local.repository.EventRepository
import com.project.attendez.data.remote.sync.SyncManager
import com.project.attendez.ui.attendee.mapToDailyUI
import com.project.attendez.ui.history.EventHistoryUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository,
    private val attendanceRepository: AttendanceRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _events = MutableStateFlow<List<EventEntity>>(emptyList())
    val events: StateFlow<List<EventEntity>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        getAllEvents()
    }

    private fun getAllEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getEvents().collect {
                _events.value = it
                _isLoading.value = false
            }
        }
    }

    fun getEventById(id: String) = repository.getEventById(id)

    fun createEvent(name: String, startDate: LocalDate, endDate: LocalDate, description: String, color: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.create(EventEntity(name = name, startDate = startDate, endDate = endDate, description = description, color = color))
            _isLoading.value = false
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { repository.delete(event) }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch { repository.update(event) }
    }

    fun getEventHistory(): Flow<List<EventHistoryUI>> = flow {
        val events = repository.getEventsOnce()

        val result = events.map { event ->
            val raw = attendanceRepository.getDailyAttendanceSummary(event.id)
            val mapped = mapToDailyUI(raw)

            EventHistoryUI(
                event = event,
                days = mapped
            )
        }

        emit(result)
    }

    fun syncNow() {
        viewModelScope.launch {
            syncManager.syncAll()
        }
    }
}
