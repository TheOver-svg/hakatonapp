package com.burlaychiki.hakatonapp.data.repository

import com.burlaychiki.hakatonapp.data.local.ConnectionStore
import com.burlaychiki.hakatonapp.data.remote.socket.HubClient
import com.burlaychiki.hakatonapp.domain.model.PairingPayload
import com.burlaychiki.hakatonapp.domain.repository.PairingRepository
import com.burlaychiki.hakatonapp.util.Constants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairingRepositoryImpl @Inject constructor(
    private val hub: HubClient,
    private val store: ConnectionStore
) : PairingRepository {

    override suspend fun pair(payload: PairingPayload): Result<Unit> {
        val previous = store.sessionId.first()
        return try {
            hub.connect(payload.sessionId)

            withTimeoutOrNull(Constants.PAIRING_TIMEOUT_MS) { hub.metrics.first() }
                ?: throw IOException("ПК не відповідає. Перевірте, що агент запущений і QR актуальний")

            store.saveSession(payload.sessionId)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            hub.disconnect()
            previous?.let(hub::restore)   // невдалий парінг не ламає попереднє підключення
            Result.failure(e)
        }
    }

    override suspend fun restore() {
        store.sessionId.first()?.let(hub::restore)
    }

    override fun isPaired(): Flow<Boolean> =
        store.sessionId.map { it != null }.distinctUntilChanged()

    override suspend fun unpair() {
        hub.disconnect()
        store.clear()
    }
}