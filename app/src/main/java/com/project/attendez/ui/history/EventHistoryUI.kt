package com.project.attendez.ui.history

import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.ui.attendee.DailyAttendanceUI

data class EventHistoryUI(
    val event: EventEntity,
    val days: List<DailyAttendanceUI>
)