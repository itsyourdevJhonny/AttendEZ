package com.project.attendez.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAttendanceDate: LocalDate? = null,
    val color: Long,

    val updatedAt: Long = System.currentTimeMillis(),


    val synced: Boolean = false,
    val deleted: Boolean = false
)
