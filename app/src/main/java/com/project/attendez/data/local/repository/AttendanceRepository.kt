package com.project.attendez.data.local.repository

import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.util.AttendanceSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val dao: AttendanceDao
) {

    fun getAttendance(eventId: Long): Flow<List<AttendanceEntity>> = dao.getByEvent(eventId)

    fun getSummary(eventId: Long): Flow<AttendanceSummary> = dao.getSummary(eventId)

    suspend fun mark(eventId: Long, attendeeId: Long, isPresent: Boolean) {
        dao.mark(
            AttendanceEntity(
                eventId = eventId,
                attendeeId = attendeeId,
                isPresent = isPresent
            )
        )
    }
}
