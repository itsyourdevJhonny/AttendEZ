package com.project.attendez.data.remote.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.project.attendez.data.local.entity.AttendanceEntity
import com.project.attendez.data.local.entity.AttendanceStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("attendance")

    // ---------------------------------------------------
    // UPSERT SINGLE
    // ---------------------------------------------------
    suspend fun uploadAttendance(entity: AttendanceEntity) {
        val key = "${entity.eventId}_${entity.attendeeId}"

        collection.document(key)
            .set(entity)
            .await()
    }

    fun observeAttendance(eventId: String, onChange: (AttendanceEntity) -> Unit) {
        collection
            .whereEqualTo("eventId", eventId)
            .addSnapshotListener { snapshots, _ ->

                snapshots?.documentChanges?.forEach { change ->
                    val data = change.document.data

                    val entity = AttendanceEntity(
                        eventId = (data["eventId"] as String),
                        attendeeId = (data["attendeeId"] as String),
                        status = AttendanceStatus.valueOf(data["status"] as String),
                        updatedAt = data["updatedAt"] as Long,
                        isSynced = true,
                        isDeleted = data["isDeleted"] as? Boolean ?: false
                    )

                    onChange(entity)
                }
            }
    }

    // ---------------------------------------------------
    // UPSERT BULK
    // ---------------------------------------------------
    suspend fun uploadAttendanceList(list: List<AttendanceEntity>) {
        list.forEach {
            uploadAttendance(it)
        }
    }

    // ---------------------------------------------------
    // DELETE SINGLE (SOFT DELETE SUPPORT)
    // ---------------------------------------------------
    suspend fun deleteAttendance(eventId: String, attendeeId: String) {
        val key = "${eventId}_${attendeeId}"

        collection.document(key)
            .delete()
            .await()
    }

    // ---------------------------------------------------
    // BULK DELETE
    // ---------------------------------------------------
    suspend fun deleteAttendanceList(list: List<AttendanceEntity>) {
        list.forEach {
            deleteAttendance(it.eventId, it.attendeeId)
        }
    }
}