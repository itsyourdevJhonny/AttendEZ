package com.project.attendez.data.local.util

import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity

data class AttendanceWithAttendee(
    val attendance: AttendanceEntity,
    val attendee: AttendeeEntity
)