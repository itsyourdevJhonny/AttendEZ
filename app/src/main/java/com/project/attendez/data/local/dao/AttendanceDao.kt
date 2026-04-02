package com.project.attendez.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.util.AttendanceCount
import com.project.attendez.data.local.util.AttendanceWithAttendeeRaw
import com.project.attendez.data.local.util.Summary
import com.project.attendez.data.local.util.TotalAttendance
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun mark(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(attendance: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance WHERE eventId = :eventId")
    fun getByEvent(eventId: Long): Flow<List<AttendanceEntity>>

    @Query("""
    SELECT * FROM attendance 
    WHERE eventId = :eventId 
    AND date BETWEEN :startOfDay AND :endOfDay
""")
    fun getByEventAndDate(
        eventId: Long,
        startOfDay: LocalDateTime,
        endOfDay: LocalDateTime
    ): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE eventId = :eventId AND attendeeId = :attendeeId")
    fun getByEventAndAttendee(eventId: Long, attendeeId: Long): Flow<AttendanceEntity?>

    @Query(
        """
        SELECT
            SUM(CASE WHEN status = 'PRESENT' = 1 THEN 1 ELSE 0 END) AS presentCount,
            SUM(CASE WHEN status = 'ABSENT' = 0 THEN 1 ELSE 0 END) AS absentCount
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

    @Delete
    suspend fun deleteAll(attendance: List<AttendanceEntity>)

    @Query(
        """
        SELECT 
            e.id AS eventId,
            e.name AS eventName,
            e.startDate AS date,
            e.description AS description,
            COUNT(a.attendeeId) AS total,
            SUM(CASE WHEN a.status = 'PRESENT' = 1 THEN 1 ELSE 0 END) AS present,
            SUM(CASE WHEN a.status = 'ABSENT' = 0 THEN 1 ELSE 0 END) AS absent
        FROM attendance a
        INNER JOIN events e ON a.eventId = e.id
        GROUP BY e.id
        ORDER BY e.startDate DESC
        """
    )
    suspend fun getAttendanceHistory(): List<Summary>

    @Query(
        """
    SELECT status, COUNT(*) as count
    FROM attendance
    WHERE eventId = :eventId AND date = :date
    GROUP BY status
"""
    )
    suspend fun getAttendanceSummary(
        eventId: Long,
        date: LocalDateTime,
    ): List<TotalAttendance>

    @Query(
        """
    SELECT * FROM attendance
    INNER JOIN attendees 
    ON attendance.attendeeId = attendees.id
    WHERE attendance.eventId = :eventId 
    AND attendance.date = :date
"""
    )
    suspend fun getAttendanceWithAttendeesByDate(
        eventId: Long,
        date: LocalDateTime,
    ): List<AttendanceWithAttendeeRaw>

    @Query(
        """
    SELECT attendeeId
    FROM attendance
    WHERE eventId = :eventId
"""
    )
    suspend fun getExistingAttendeeIds(
        eventId: Long,
    ): List<Long>
}

