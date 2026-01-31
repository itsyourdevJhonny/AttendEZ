package com.project.attendez.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "attendance",
    primaryKeys = ["eventId", "attendeeId"],
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AttendeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["attendeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("eventId"),
        Index("attendeeId")
    ]
)
data class AttendanceEntity(
    val eventId: Long,
    val attendeeId: Long,
    val isPresent: Boolean,
    val markedAt: Long = System.currentTimeMillis()
)
