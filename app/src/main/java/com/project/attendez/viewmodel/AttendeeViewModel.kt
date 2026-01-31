package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.repository.AttendeeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendeeViewModel(
    private val repository: AttendeeRepository
) : ViewModel() {

    val attendees: StateFlow<List<AttendeeEntity>> =
        repository.getAttendees()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun addAttendee(
        studentId: String,
        fullName: String,
        course: String?,
        yearLevel: Int?
    ) {
        viewModelScope.launch {
            repository.add(
                AttendeeEntity(
                    studentId = studentId,
                    fullName = fullName,
                    course = course,
                    yearLevel = yearLevel
                )
            )
        }
    }
}
