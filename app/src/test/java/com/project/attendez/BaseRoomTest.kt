package com.project.attendez

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.project.attendez.di.AttendEzDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseRoomTest {

    protected lateinit var db: AttendEzDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AttendEzDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }
}
