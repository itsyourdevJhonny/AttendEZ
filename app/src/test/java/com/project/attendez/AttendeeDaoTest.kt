package com.project.attendez

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.entity.AttendeeEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AttendeeDaoTest : BaseRoomTest() {

    private lateinit var dao: AttendeeDao

    @Before
    fun setupDao() {
        dao = db.attendeeDao()
    }

    @Test
    fun insertAttendee_andReuseAcrossEvents() = runTest {
        val attendee = AttendeeEntity(
            studentId = "2024-00123",
            fullName = "Juan Dela Cruz",
            course = "BSCS",
            yearLevel = 2
        )

        dao.insert(attendee)

        val attendees = dao.getAll().first()

        assertThat(attendees).hasSize(1)
        assertThat(attendees[0].studentId).isEqualTo("2024-00123")
    }
}
