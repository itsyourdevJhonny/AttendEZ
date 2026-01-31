package com.project.attendez.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.util.AttendanceSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mark(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE eventId = :eventId")
    fun getByEvent(eventId: Long): Flow<List<AttendanceEntity>>

    @Query("""
        SELECT
            SUM(CASE WHEN isPresent = 1 THEN 1 ELSE 0 END) AS presentCount,
            SUM(CASE WHEN isPresent = 0 THEN 1 ELSE 0 END) AS absentCount
        FROM attendance
        WHERE eventId = :eventId
    """)
    fun getSummary(eventId: Long): Flow<AttendanceSummary>
}

