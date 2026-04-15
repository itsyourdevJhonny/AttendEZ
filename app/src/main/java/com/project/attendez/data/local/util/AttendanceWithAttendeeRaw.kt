package com.project.attendez.data.local.util

import com.project.attendez.data.local.entity.AttendanceStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class AttendanceWithAttendeeRaw(
    // Attendance fields
    val eventId: Long,
    val attendeeId: Long,
    val status: AttendanceStatus,
    val date: LocalDateTime,
    val markedAt: Long,

    // Attendee fields
    val id: Long,
    val studentId: String,
    val fullName: String,
    val course: String?,
    val yearLevel: Int?
)