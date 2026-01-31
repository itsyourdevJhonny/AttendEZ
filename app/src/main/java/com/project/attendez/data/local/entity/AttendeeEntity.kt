package com.project.attendez.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendees",
    indices = [Index(value = ["studentId"], unique = true)]
)
data class AttendeeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val studentId: String,
    val fullName: String,
    val course: String? = null,
    val yearLevel: Int? = null
)