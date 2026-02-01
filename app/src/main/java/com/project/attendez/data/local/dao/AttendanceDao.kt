package com.project.attendez.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.util.AttendanceCount
import com.project.attendez.data.local.util.Summary
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mark(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE eventId = :eventId")
    fun getByEvent(eventId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE eventId = :eventId AND attendeeId = :attendeeId")
    fun getByEventAndAttendee(eventId: Long, attendeeId: Long): Flow<AttendanceEntity>

    @Query(
        """
        SELECT
            SUM(CASE WHEN isPresent = 1 THEN 1 ELSE 0 END) AS presentCount,
            SUM(CASE WHEN isPresent = 0 THEN 1 ELSE 0 END) AS absentCount
        FROM attendance
        WHERE eventId = :eventId
    """
    )
    fun getSummary(eventId: Long): Flow<AttendanceCount>

    @Query(
        """
        DELETE FROM attendance 
        WHERE eventId = :eventId AND attendeeId = :attendeeId
        """
    )
    suspend fun delete(eventId: Long, attendeeId: Long)

    @Query(
        """
        SELECT 
            e.id AS eventId,
            e.name AS eventName,
            e.date AS date,
            COUNT(a.attendeeId) AS total,
            SUM(CASE WHEN a.isPresent = 1 THEN 1 ELSE 0 END) AS present,
            SUM(CASE WHEN a.isPresent = 0 THEN 1 ELSE 0 END) AS absent
        FROM attendance a
        INNER JOIN events e ON a.eventId = e.id
        GROUP BY e.id
        ORDER BY e.date DESC
        """
    )
    suspend fun getAttendanceHistory(): List<Summary>
}

