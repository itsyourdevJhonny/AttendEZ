package com.project.attendez.ui.util

import com.project.attendez.data.local.entity.AttendanceStatus

data class ImportPreview(
    val studentId: String?,
    val fullName: String?,
    val course: String?,
    val yearLevel: Int?,
    val status: AttendanceStatus?,

    val isDuplicate: Boolean = false,
    val reason: String? = null
)