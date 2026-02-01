package com.project.attendez.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.project.attendez.data.local.entity.AttendeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendee: AttendeeEntity): Long

    @Query("SELECT * FROM attendees ORDER BY fullName")
    fun getAll(): Flow<List<AttendeeEntity>>

    @Query("SELECT * FROM attendees WHERE id = :id")
    fun getById(id: Long): Flow<AttendeeEntity>

    @Query("SELECT * FROM attendees WHERE studentId = :studentId")
    fun getByStudentId(studentId: String): Flow<AttendeeEntity?>

    @Query("SELECT * FROM attendees WHERE studentId = :studentId")
    suspend fun getByStudentIdOnce(studentId: String): AttendeeEntity?
}
