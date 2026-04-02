package com.project.attendez.extension

import com.project.attendez.data.local.entity.EventEntity
import java.time.LocalDate

fun EventEntity.isOngoing(today: LocalDate) = !today.isBefore(startDate) && !today.isAfter(endDate)

fun EventEntity.isEnded(today: LocalDate) = today.isAfter(endDate)

fun EventEntity.upcoming(today: LocalDate) = today.isBefore(startDate)