package com.project.attendez.data.local.repository

import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendeeEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class AttendeeRepository @Inject constructor(
    private val attendeeDao: AttendeeDao,
) {
    fun getAttendees(): Flow<List<AttendeeEntity>> = attendeeDao.getAll()

    fun getAttendeeById(id: Long): Flow<AttendeeEntity> = attendeeDao.getById(id)

    fun getByEventId(eventId: Long) = attendeeDao.getByEventId(eventId)

    fun getByStudentId(studentId: String) = attendeeDao.getByStudentId(studentId)

    fun getByEventIdAndDate(eventId: Long, startOfDay: LocalDateTime, endOfDay: LocalDateTime) =
        attendeeDao.getByEventIdAndDate(eventId, startOfDay, endOfDay)
}
