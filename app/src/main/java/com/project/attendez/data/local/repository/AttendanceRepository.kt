package com.project.attendez.data.local.repository

import androidx.room.Transaction
import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.entity.AttendeeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val attendeeDao: AttendeeDao
) {

    fun getAttendance(eventId: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getByEvent(eventId)

    suspend fun mark(
        eventId: Long,
        attendeeId: Long,
        status: AttendanceStatus
    ) {
        attendanceDao.mark(
            AttendanceEntity(
                eventId = eventId,
                attendeeId = attendeeId,
                status = status
            )
        )
    }

    suspend fun addAll(attendance: List<AttendanceEntity>) = attendanceDao.addAll(attendance)

    suspend fun delete(eventId: Long, attendeeId: Long) = attendanceDao.delete(eventId, attendeeId)

    suspend fun deleteAll(attendance: List<AttendanceEntity>) = attendanceDao.deleteAll(attendance)

    fun getAttendanceByAttendee(eventId: Long, attendeeId: Long) =
        attendanceDao.getByEventAndAttendee(eventId, attendeeId)

    @Transaction
    suspend fun addAttendeeAndMarkAttendance(
        eventId: Long,
        studentId: String,
        fullName: String,
        course: String?,
        yearLevel: Int?,
        isPresent: Boolean,
        status: AttendanceStatus
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

        attendanceDao.mark(AttendanceEntity(eventId, attendeeId, status))

        return if (existing != null) AddAttendeeResult.Existing else AddAttendeeResult.New
    }

    suspend fun getAttendanceHistory() = attendanceDao.getAttendanceHistory()
}

sealed class AddAttendeeResult {
    data object Existing : AddAttendeeResult()
    data object New : AddAttendeeResult()
}
