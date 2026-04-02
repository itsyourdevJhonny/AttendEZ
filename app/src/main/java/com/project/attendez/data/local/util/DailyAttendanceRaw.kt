package com.project.attendez.data.local.util

import com.project.attendez.data.local.entity.AttendanceStatus

data class DailyAttendanceRaw(
    val day: String, // YYYY-MM-DD
    val status: AttendanceStatus,
    val count: Int
)