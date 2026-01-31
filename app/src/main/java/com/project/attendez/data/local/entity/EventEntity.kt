package com.project.attendez.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val date: LocalDate,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)
