package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.repository.AttendeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendeeViewModel @Inject constructor(
    private val repository: AttendeeRepository
) : ViewModel() {

    val attendees: StateFlow<List<AttendeeEntity>> =
        repository.getAttendees()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun getAttendeeById(id: Long) = repository.getAttendeeById(id)

    fun getAttendeeByStudentId(studentId: String) = repository.getAttendeeByStudentId(studentId)

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
