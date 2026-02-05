package com.project.attendez

import com.google.common.truth.Truth.assertThat
import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.dao.EventDao
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.entity.EventEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceDaoTest : BaseRoomTest() {

    private lateinit var eventDao: EventDao
    private lateinit var attendeeDao: AttendeeDao
    private lateinit var attendanceDao: AttendanceDao

    @Before
    fun setupDao() {
        eventDao = db.eventDao()
        attendeeDao = db.attendeeDao()
        attendanceDao = db.attendanceDao()
    }

    @Test
    fun markAttendance_andVerifyPresence() = runTest {

        val eventId = eventDao.insert(
            EventEntity(
                name = "Orientation",
                date = LocalDate.now(),
                description = "Freshmen orientation"
            )
        )

        val attendeeId = attendeeDao.insert(
            AttendeeEntity(
                studentId = "2024-0001",
                fullName = "Maria Santos"
            )
        )

        attendanceDao.mark(
            AttendanceEntity(
                eventId = eventId,
                attendeeId = attendeeId
            )
        )

        val attendance = attendanceDao.getByEvent(eventId).first()

        assertThat(attendance).hasSize(1)
        assertThat(attendance[0].isPresent).isTrue()
    }

    @Test
    fun attendanceSummary_returnsCorrectCounts() = runTest {

        val eventId = eventDao.insert(
            EventEntity(
                name = "Workshop",
                date = LocalDate.now(),
                description = "Android basics"
            )
        )

        val a1 = attendeeDao.insert(
            AttendeeEntity(
                studentId = "001",
                fullName = "Student One"
            )
        )

        val a2 = attendeeDao.insert(
            AttendeeEntity(
                studentId = "002",
                fullName = "Student Two"
            )
        )

        attendanceDao.mark(AttendanceEntity(eventId, a1))
        attendanceDao.mark(AttendanceEntity(eventId, a2))

        val summary = attendanceDao.getSummary(eventId).first()

        assertThat(summary.presentCount).isEqualTo(1)
        assertThat(summary.absentCount).isEqualTo(1)
    }
}
