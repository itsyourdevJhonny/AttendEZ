package com.project.attendez.data.local.util

data class Summary(
    val eventId: Long,
    val eventName: String,
    val description: String,
    val date: String,
    val total: Int,
    val present: Int,
    val absent: Int
)