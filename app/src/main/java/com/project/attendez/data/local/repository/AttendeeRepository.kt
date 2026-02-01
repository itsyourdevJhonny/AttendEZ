package com.project.attendez.data.local.repository

import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendeeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendeeRepository @Inject constructor(
    private val dao: AttendeeDao
) {

    fun getAttendees(): Flow<List<AttendeeEntity>> = dao.getAll()

    suspend fun add(attendee: AttendeeEntity): Long = dao.insert(attendee)
}
