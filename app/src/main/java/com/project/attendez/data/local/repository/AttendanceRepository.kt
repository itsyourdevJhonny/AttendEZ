package com.project.attendez.data.local.repository

import androidx.room.Transaction
import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendanceStatus
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.data.local.util.AttendanceWithAttendee
import com.project.attendez.viewmodel.DailyStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val attendanceDao: AttendanceDao,
    private val attendeeDao: AttendeeDao,
) {

    fun getAttendance(eventId: Long): Flow<List<AttendanceEntity>> =
        attendanceDao.getByEvent(eventId)

    suspend fun mark(
        eventId: Long,
        attendeeId: Long,
        status: AttendanceStatus,
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
        status: AttendanceStatus,
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

    suspend fun getDailyStats(eventId: Long, date: LocalDateTime): DailyStats {
        val result = attendanceDao.getAttendanceSummary(eventId, date)

        var present = 0
        var absent = 0
        var excuse = 0

        result.forEach {
            when (it.status) {
                AttendanceStatus.PRESENT -> present = it.count
                AttendanceStatus.ABSENT -> absent = it.count
                AttendanceStatus.EXCUSE -> excuse = it.count
            }
        }

        return DailyStats(present, absent, excuse)
    }

    suspend fun getAllDaysStats(event: EventEntity): List<Pair<LocalDateTime, DailyStats>> {
        val days = ChronoUnit.DAYS.between(event.startDate, event.endDate).toInt()

        return (0..days).map { offset ->
            val date = event.startDate.plusDays(offset.toLong()).atStartOfDay()
            val stats = getDailyStats(event.id, date)
            date to stats
        }
    }

    suspend fun getDailyAttendanceList(
        eventId: Long,
        date: LocalDateTime,
    ): List<AttendanceWithAttendee> {

        return attendanceDao.getAttendanceWithAttendeesByDate(eventId, date)
            .map {
                AttendanceWithAttendee(
                    attendance = AttendanceEntity(
                        eventId = it.eventId,
                        attendeeId = it.attendeeId,
                        status = it.status,
                        markedAt = it.markedAt
                    ),
                    attendee = AttendeeEntity(
                        id = it.id,
                        studentId = it.studentId,
                        fullName = it.fullName,
                        course = it.course,
                        yearLevel = it.yearLevel
                    )
                )
            }
    }

    suspend fun getDailyGroupedAttendance(
        eventId: Long,
        date: LocalDateTime
    ): Map<AttendanceStatus, List<AttendanceWithAttendee>> {

        return getDailyAttendanceList(eventId, date)
            .groupBy { it.attendance.status }
    }

    suspend fun getExistingAttendeeIds(eventId: Long) = attendanceDao.getExistingAttendeeIds(eventId)

    suspend fun getAttendeeMap(): Map<String, Long> {
        return attendeeDao.getAll()
            .first()
            .associate { it.studentId to it.id }
    }
}

sealed class AddAttendeeResult {
    data object Existing : AddAttendeeResult()
    data object New : AddAttendeeResult()
}
