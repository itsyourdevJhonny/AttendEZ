package com.project.attendez.data.remote.repository

import com.project.attendez.data.local.dao.EventDao
import com.project.attendez.data.local.entity.EventEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepository @Inject constructor(
    private val eventDao: EventDao
) {

    fun getEvents() = eventDao.getAll()

    suspend fun create(event: EventEntity) {

        eventDao.insert(
            event.copy(
                synced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun update(event: EventEntity) {

        eventDao.update(
            event.copy(
                synced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun delete(event: EventEntity) {

        eventDao.update(
            event.copy(
                deleted = true,
                synced = false,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}