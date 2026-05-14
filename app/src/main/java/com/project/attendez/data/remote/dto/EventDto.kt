package com.project.attendez.data.remote.dto

import java.time.LocalDate

data class EventDto(
    val id: String = "",
    val name: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val color: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val deleted: Boolean = false
)

fun String.toLocalDateTime(): LocalDate {
    return LocalDate.parse(this)
}
