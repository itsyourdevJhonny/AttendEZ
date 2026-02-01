package com.project.attendez.data.local.repository

import com.project.attendez.data.local.dao.EventDao
import com.project.attendez.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val dao: EventDao
) {
    fun getEvents(): Flow<List<EventEntity>> = dao.getAll()

    fun getEventById(id: Long): Flow<EventEntity> = dao.getById(id)

    suspend fun create(event: EventEntity): Long = dao.insert(event)

    suspend fun delete(event: EventEntity) = dao.delete(event)
}
