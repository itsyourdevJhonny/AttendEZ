package com.project.attendez.di

import com.google.firebase.firestore.FirebaseFirestore
import com.project.attendez.data.remote.dto.EventDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val events = firestore.collection("events")
    private val attendees = firestore.collection("attendees")
    private val attendance = firestore.collection("attendance")

    suspend fun uploadEvent(event: EventDto) {
        events.document(event.id)
            .set(event)
            .await()
    }

    suspend fun getEvents(): List<EventDto> {

        return events
            .get()
            .await()
            .documents
            .mapNotNull {
                it.toObject(EventDto::class.java)
            }
    }

    fun listenToEvents(
        onChange: (List<EventDto>) -> Unit
    ) {

        events.addSnapshotListener { snapshot, _ ->

            val data = snapshot
                ?.documents
                ?.mapNotNull {
                    it.toObject(EventDto::class.java)
                }
                ?: emptyList()

            onChange(data)
        }
    }
}