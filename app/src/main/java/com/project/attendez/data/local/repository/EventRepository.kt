package com.project.attendez.data.local.repository

import com.project.attendez.data.local.dao.EventDao
import com.project.attendez.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val dao: EventDao
) {

    fun getEvents(): Flow<List<EventEntity>> = dao.getAll()

    suspend fun create(event: EventEntity): Long = dao.insert(event)
}
