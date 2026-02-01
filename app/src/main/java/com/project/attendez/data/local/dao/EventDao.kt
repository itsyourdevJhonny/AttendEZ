package com.project.attendez.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.project.attendez.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Insert
    suspend fun insert(event: EventEntity): Long

    @Query("SELECT * FROM events ORDER BY date DESC")
    fun getAll(): Flow<List<EventEntity>>

    @Delete
    suspend fun delete(event: EventEntity)
}
