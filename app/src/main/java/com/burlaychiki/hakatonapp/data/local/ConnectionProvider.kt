package com.burlaychiki.hakatonapp.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionProvider @Inject constructor(
    store: ConnectionStore
) {
    @Volatile
    var current: ConnectionData? = null
        private set

    init {
        store.connection
            .onEach { current = it }
            .launchIn(CoroutineScope(SupervisorJob() + Dispatchers.IO))
    }
}