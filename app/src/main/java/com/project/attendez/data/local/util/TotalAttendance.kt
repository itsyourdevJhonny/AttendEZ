package com.project.attendez.data.local.util

import com.project.attendez.data.local.entity.AttendanceStatus

data class TotalAttendance(
    val status: AttendanceStatus,
    val count: Int
)
