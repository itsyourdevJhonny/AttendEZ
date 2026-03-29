package com.project.attendez.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "attendance",
    primaryKeys = ["eventId", "attendeeId", "date"],
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
    val status: AttendanceStatus,
    val date: LocalDateTime = LocalDateTime.now(),
    val markedAt: Long = System.currentTimeMillis()
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    EXCUSE
}
