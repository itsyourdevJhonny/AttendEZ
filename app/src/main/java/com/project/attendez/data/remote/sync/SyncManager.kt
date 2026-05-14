package com.project.attendez.data.remote.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.project.attendez.data.local.dao.AttendanceDao
import com.project.attendez.data.local.dao.AttendeeDao
import com.project.attendez.data.local.dao.EventDao
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendeeEntity
import com.project.attendez.data.local.entity.EventEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SyncManager @Inject constructor(
    private val eventDao: EventDao,
    private val attendeeDao: AttendeeDao,
    private val attendanceDao: AttendanceDao,
    private val firebase: FirebaseFirestore
) {

    suspend fun syncAll() {
        syncEvents()
        syncAttendees()
        syncAttendance()

        downloadEvents()
        downloadAttendees()
        downloadAttendance()
    }

    // -------------------------
    // UPLOAD (LOCAL → FIREBASE)
    // -------------------------

    private suspend fun syncEvents() {
        eventDao.getUnsynced().forEach { event ->
            firebase.collection("events")
                .document(event.id)
                .set(event)

            eventDao.update(event.copy(synced = true))
        }
    }

    private suspend fun syncAttendees() {
        attendeeDao.getUnsynced().forEach { attendee ->
            firebase.collection("attendees")
                .document(attendee.id)
                .set(attendee)

            attendeeDao.insert(attendee.copy(synced = true))
        }
    }

    private suspend fun syncAttendance() {
        attendanceDao.getUnsynced().forEach { attendance ->
            val id = "${attendance.eventId}_${attendance.attendeeId}"

            firebase.collection("attendance")
                .document(id)
                .set(attendance)

            attendanceDao.mark(attendance.copy(isSynced = true))
        }
    }

    // -------------------------
    // DOWNLOAD (FIREBASE → LOCAL)
    // -------------------------

    private suspend fun downloadEvents() {
        val remote = firebase.collection("events").get().await()

        remote.documents.forEach { doc ->
            val remote = doc.toObject(EventEntity::class.java) ?: return@forEach
            val local = eventDao.getByIdOnce(remote.id)

            if (local == null || remote.updatedAt > local.updatedAt) {
                eventDao.insert(remote)
            }
        }
    }

    private suspend fun downloadAttendees() {
        val remote = firebase.collection("attendees").get().await()

        remote.documents.forEach { doc ->
            val remote = doc.toObject(AttendeeEntity::class.java) ?: return@forEach
            val local = attendeeDao.getByIdOnce(remote.id)

            if (local == null || remote.updatedAt > local.updatedAt) {
                attendeeDao.insert(remote)
            }
        }
    }

    private suspend fun downloadAttendance() {
        val remote = firebase.collection("attendance").get().await()

        remote.documents.forEach { doc ->
            val remote = doc.toObject(AttendanceEntity::class.java) ?: return@forEach
            val local = attendanceDao.getByIdOnce(remote.eventId)

            if (local == null || remote.updatedAt > local.updatedAt) {
                attendanceDao.mark(remote)
            }
        }
    }
}