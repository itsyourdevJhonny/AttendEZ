package com.project.attendez.viewmodel

import androidx.lifecycle.ViewModel
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.repository.AttendeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AttendeeViewModel @Inject constructor(
    private val repository: AttendeeRepository
) : ViewModel() {
    fun getAttendeeById(id: Long) = repository.getAttendeeById(id)

    fun getAttendeesByEventId(eventId: Long) = repository.getByEventId(eventId)

    fun getAttendeeByStudentId(studentId: String) = repository.getByStudentId(studentId)

    fun getAttendeesByEventIdAndDate(eventId: Long): Flow<List<AttendeeEntity?>> {
        val now = LocalDateTime.now()
        val startOfDay = now.toLocalDate().atStartOfDay()
        val endOfDay = now.toLocalDate().atTime(23, 59, 59)

        return repository.getByEventIdAndDate(eventId, startOfDay, endOfDay)
    }
}
