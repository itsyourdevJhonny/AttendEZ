package com.project.attendez.data.local.repository

import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendeeEntity
import kotlinx.coroutines.flow.Flow

class AttendeeRepository(
    private val dao: AttendeeDao
) {

    fun getAttendees(): Flow<List<AttendeeEntity>> = dao.getAll()

    suspend fun add(attendee: AttendeeEntity): Long = dao.insert(attendee)
}
