package com.project.attendez.data.remote.sync

import com.project.attendez.data.local.entity.AttendanceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseSyncManager @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {

    // ---------------------------------------------------
    // SINGLE UPSERT
    // ---------------------------------------------------
    suspend fun queueAttendanceUpsert(entity: AttendanceEntity) {
        withContext(Dispatchers.IO) {
            firebaseDataSource.uploadAttendance(entity)
        }
    }

    // ---------------------------------------------------
    // BULK UPSERT
    // ---------------------------------------------------
    suspend fun queueBulkUpsert(list: List<AttendanceEntity>) {
        withContext(Dispatchers.IO) {
            firebaseDataSource.uploadAttendanceList(list)
        }
    }

    // ---------------------------------------------------
    // DELETE SINGLE
    // ---------------------------------------------------
    suspend fun queueDelete(eventId: String, attendeeId: String) {
        withContext(Dispatchers.IO) {
            firebaseDataSource.deleteAttendance(eventId, attendeeId)
        }
    }

    // ---------------------------------------------------
    // BULK DELETE
    // ---------------------------------------------------
    suspend fun queueBulkDelete(list: List<AttendanceEntity>) {
        withContext(Dispatchers.IO) {
            firebaseDataSource.deleteAttendanceList(list)
        }
    }
}