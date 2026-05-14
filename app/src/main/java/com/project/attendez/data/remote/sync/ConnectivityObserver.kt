package com.project.attendez.data.remote.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    private val syncManager: SyncManager
) {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private val connectivityManager =
        context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

    fun observe() {

        connectivityManager.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {

                    super.onAvailable(network)

                    scope.launch {

                        try {

                            syncManager.syncAll()

                        } catch (_: Exception) {

                        }
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                }
            }
        )
    }
}