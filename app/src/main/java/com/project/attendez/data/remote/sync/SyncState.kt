package com.project.attendez.data.remote.sync

sealed interface SyncState {

    object Idle : SyncState

    object Syncing : SyncState

    object Success : SyncState

    data class Failed(
        val message: String
    ) : SyncState
}