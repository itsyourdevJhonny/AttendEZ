package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.data.local.repository.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventViewModel(
    private val repository: EventRepository
) : ViewModel() {

    val events: StateFlow<List<EventEntity>> =
        repository.getEvents()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun createEvent(name: String, date: LocalDate, description: String) {
        viewModelScope.launch {
            repository.create(
                EventEntity(
                    name = name,
                    date = date,
                    description = description
                )
            )
        }
    }
}
