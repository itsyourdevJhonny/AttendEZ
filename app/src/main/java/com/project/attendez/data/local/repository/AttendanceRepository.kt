package com.project.attendez.data.local.repository

import androidx.room.Transaction
import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.util.AttendanceSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val attendeeDao: AttendeeDao
) {

    fun getAttendance(eventId: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getByEvent(eventId)

    fun getSummary(eventId: Long): Flow<AttendanceSummary> = attendanceDao.getSummary(eventId)

    suspend fun mark(eventId: Long, attendeeId: Long, isPresent: Boolean) {
        attendanceDao.mark(
            AttendanceEntity(
                eventId = eventId,
                attendeeId = attendeeId,
                isPresent = isPresent
            )
        )
    }

    suspend fun delete(eventId: Long, attendeeId: Long) = attendanceDao.delete(eventId, attendeeId)

    fun getAttendanceByAttendee(eventId: Long, attendeeId: Long) =
        attendanceDao.getByEventAndAttendee(eventId, attendeeId)

    @Transaction
    suspend fun addAttendeeAndMarkAttendance(
        eventId: Long,
        studentId: String,
        fullName: String,
        course: String?,
        yearLevel: Int?
    ): AddAttendeeResult {
        val existing = attendeeDao.getByStudentIdOnce(studentId)

        val attendeeId = existing?.id ?: attendeeDao.insert(
            AttendeeEntity(
                studentId = studentId,
                fullName = fullName,
                course = course,
                yearLevel = yearLevel
            )
        )

        attendanceDao.mark(
            AttendanceEntity(
                eventId = eventId,
                attendeeId = attendeeId,
                isPresent = false
            )
        )

        return if (existing != null) AddAttendeeResult.Existing else AddAttendeeResult.New
    }

    suspend fun getAttendanceHistory(): List<com.project.attendez.viewmodel.AttendanceSummary> {
        return attendanceDao.getAttendanceHistory()
    }
}

sealed class AddAttendeeResult {
    data object Existing : AddAttendeeResult()
    data object New : AddAttendeeResult()
}
