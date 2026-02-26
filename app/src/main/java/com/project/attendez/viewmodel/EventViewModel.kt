package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.data.local.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: EventRepository
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

    fun getEventById(id: Long) = repository.getEventById(id)

    fun createEvent(name: String, date: LocalDate, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.create(EventEntity(name = name, date = date, description = description))
            _isLoading.value = false
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch { repository.delete(event) }
    }

    fun updateEvent(event: EventEntity) {
        viewModelScope.launch { repository.update(event) }
    }
}
