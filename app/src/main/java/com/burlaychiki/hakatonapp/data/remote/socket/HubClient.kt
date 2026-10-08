package com.burlaychiki.hakatonapp.data.remote.socket

import android.util.Log
import com.burlaychiki.hakatonapp.data.remote.dto.MetricsDto
import com.burlaychiki.hakatonapp.domain.model.ConnectionState
import com.burlaychiki.hakatonapp.util.Constants
import com.microsoft.signalr.Action1
import com.microsoft.signalr.Action2
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HubClient @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()

    @Volatile private var connection: HubConnection? = null
    @Volatile private var sessionId: String? = null
    private var reconnectJob: Job? = null

    private val _state = MutableStateFlow(ConnectionState.Disconnected)
    val state: StateFlow<ConnectionState> = _state.asStateFlow()

    private val _metrics = MutableSharedFlow<MetricsDto>(
        replay = 1,
        extraBufferCapacity = 8,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val metrics: SharedFlow<MetricsDto> = _metrics.asSharedFlow()

    /** Для парінгу: підключається й кидає виняток, якщо сервер недоступний. */
    suspend fun connect(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            _metrics.resetReplayCache()
            reconnectJob?.cancel()
            closeCurrent()
            sessionId = id
            try {
                open(id)
            } catch (e: Exception) {
                sessionId = null
                throw e
            }
        }
    }

    /** При старті застосунку: не кидає винятків, повторює спроби у фоні. */
    fun restore(id: String) {
        // Зʼєднання для цього id вже є або вже відновлюється: нічого не чіпаємо
        if (sessionId == id && (connection != null || reconnectJob?.isActive == true)) return
        sessionId = id
        scheduleReconnect(immediate = true)
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        mutex.withLock {
            _metrics.resetReplayCache()
            sessionId = null
            reconnectJob?.cancel()
            closeCurrent()
            _state.value = ConnectionState.Disconnected
        }
    }

    /** Викликає метод хаба; sessionId підставляється першим аргументом. */
    suspend fun command(method: String, vararg args: Any) {
        val id = sessionId
        if (id == null) {
            Log.d(TAG, "command $method: немає sessionId")
            throw IOException("ПК не підключено")
        }
        val hub = connection
        if (hub == null) {
            Log.d(TAG, "command $method: немає зʼєднання (state=${_state.value})")
            throw IOException("Немає зʼєднання з сервером")
        }
        withContext(Dispatchers.IO) {
            val argsText = args.joinToString { "[$it]" }
            Log.d(TAG, "command -> $method(session=$id, args=$argsText)")
            try {
                hub.invoke(method, id, *args).blockingAwait()
                Log.d(TAG, "command <- $method OK")
            } catch (e: Exception) {
                Log.d(TAG, "command <- $method FAILED: ${e.javaClass.simpleName}: ${e.message}")
                throw e
            }
        }
    }

    private suspend fun open(id: String) = withContext(Dispatchers.IO) {
        _state.value = ConnectionState.Connecting
        val hub = HubConnectionBuilder.create(Constants.HUB_URL).build()

        // ТИМЧАСОВА ДІАГНОСТИКА
        hub.on("ReceiveMetrics", Action1<MetricsDto> {
            Log.d(TAG, "metrics parsed: $it")
            _metrics.tryEmit(it)
        }, MetricsDto::class.java)
        hub.on("ReceiveMetrics", Action1<Any> {
            Log.d(TAG, "ReceiveMetrics raw (1 arg): $it")
        }, Any::class.java)
        hub.on("ReceiveMetrics", Action2<Any, Any> { a, b ->
            Log.d(TAG, "ReceiveMetrics raw (2 args): $a | $b")
        }, Any::class.java, Any::class.java)

        hub.onClosed {
            Log.d(TAG, "closed: $it")
            if (connection === hub) {
                connection = null
                scheduleReconnect()
            }
        }

        try {
            hub.start().blockingAwait()
            Log.d(TAG, "started, connectionId=${hub.connectionId}")
            // Після кожного нового підключення потрібно заново вступати в групу
            hub.invoke("JoinSession", id).blockingAwait()
            Log.d(TAG, "JoinSession ok: $id")
        } catch (e: Exception) {
            Log.d(TAG, "open failed: $e")
            runCatching { hub.stop().blockingAwait() }
            _state.value = ConnectionState.Disconnected
            throw e
        }

        connection = hub
        _state.value = ConnectionState.Connected
    }

    private fun closeCurrent() {
        val hub = connection ?: return
        connection = null
        runCatching { hub.stop().blockingAwait() }
    }

    private fun scheduleReconnect(immediate: Boolean = false) {
        val id = sessionId ?: return
        _state.value = ConnectionState.Connecting
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            if (!immediate) delay(RETRY_DELAY_MS)
            while (isActive && sessionId == id) {
                try {
                    mutex.withLock {
                        if (sessionId == id && connection == null) open(id)
                    }
                    return@launch
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.d(TAG, "reconnect failed: $e")
                    _state.value = ConnectionState.Connecting
                    delay(RETRY_DELAY_MS)
                }
            }
        }
    }

    private companion object {
        const val RETRY_DELAY_MS = 3_000L
        const val TAG = "HubDebug"
    }
}