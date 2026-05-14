package com.project.attendez.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.attendez.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY startDate DESC")
    fun getAll(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id")
    fun getById(id: String): Flow<EventEntity>

    @Delete
    suspend fun delete(event: EventEntity)

    @Update
    suspend fun update(event: EventEntity)

    @Query("SELECT * FROM events ORDER BY startDate DESC")
    suspend fun getEventsOnce(): List<EventEntity>

    @Query("SELECT * FROM events WHERE synced = 0")
    suspend fun getUnsynced(): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getByIdOnce(id: String): EventEntity?
}
