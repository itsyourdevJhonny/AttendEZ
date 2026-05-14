package com.project.attendez.data.remote.mapper

import com.project.attendez.data.local.entity.EventEntity
import com.project.attendez.data.remote.dto.EventDto
import com.project.attendez.data.remote.dto.toLocalDateTime

fun EventEntity.toDto(): EventDto {
    return EventDto(
        id = id,
        name = name,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        description = description,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deleted = deleted
    )
}

fun EventDto.toEntity(): EventEntity {
    return EventEntity(
        id = id,
        name = name,
        startDate = startDate.toLocalDateTime(),
        endDate = endDate.toLocalDateTime(),
        description = description,
        color = color,
        createdAt = createdAt,
        updatedAt = updatedAt,
        synced = true,
        deleted = deleted
    )
}