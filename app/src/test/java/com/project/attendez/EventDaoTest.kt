package com.project.attendez

import com.google.common.truth.Truth.assertThat
import com.project.attendez.data.local.dao.EventDao
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
class EventDaoTest : BaseRoomTest() {

    private lateinit var dao: EventDao

    @Before
    fun setupDao() {
        dao = db.eventDao()
    }

    @Test
    fun insertEvent_andReadIt() = runTest {
        val event = EventEntity(
            name = "General Assembly",
            date = LocalDate.of(2026, 2, 10),
            description = "Org meeting"
        )

        dao.insert(event)

        val events = dao.getAll().first()

        assertThat(events).hasSize(1)
        assertThat(events[0].name).isEqualTo("General Assembly")
    }
}
