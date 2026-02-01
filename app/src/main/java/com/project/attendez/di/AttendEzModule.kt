package com.project.attendez.di

import android.content.Context
import androidx.room.Room
import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.dao.EventDao
import com.project.attendez.data.local.repository.AttendanceRepository
import com.project.attendez.data.local.repository.AttendeeRepository
import com.project.attendez.data.local.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AttendEzDatabase {
        return Room.databaseBuilder(
            context,
            AttendEzDatabase::class.java,
            "attendez_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    @Singleton
    fun provideEventDao(database: AttendEzDatabase) = database.eventDao()

    @Provides
    @Singleton
    fun provideAttendeeDao(database: AttendEzDatabase) = database.attendeeDao()

    @Provides
    @Singleton
    fun provideAttendanceDao(database: AttendEzDatabase) = database.attendanceDao()

    @Provides
    @Singleton
    fun provideEventRepository(eventDao: EventDao) = EventRepository(eventDao)

    @Provides
    @Singleton
    fun provideAttendeeRepository(attendeeDao: AttendeeDao) = AttendeeRepository(attendeeDao)

    @Provides
    @Singleton
    fun provideAttendanceRepository(
        attendanceDao: AttendanceDao,
        attendeeDao: AttendeeDao
    ) = AttendanceRepository(attendanceDao, attendeeDao)
}