package com.burlaychiki.hakatonapp.data.repository

import com.burlaychiki.hakatonapp.data.local.ConnectionProvider
import com.burlaychiki.hakatonapp.data.local.ConnectionStore
import com.burlaychiki.hakatonapp.data.remote.api.PcCommandApi
import com.burlaychiki.hakatonapp.data.remote.dto.PairRequestDto
import com.burlaychiki.hakatonapp.domain.model.PairingPayload
import com.burlaychiki.hakatonapp.domain.repository.PairingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PairingRepositoryImpl @Inject constructor(
    private val api: PcCommandApi,
    private val store: ConnectionStore,
    private val provider: ConnectionProvider
) : PairingRepository {

    override suspend fun pair(payload: PairingPayload): Result<Unit> = runCatching {
        // 1. Зберігаємо адресу, щоб DynamicHostInterceptor знав, куди слати запит
        store.saveAddress(payload.host, payload.port, payload.pcName)

        // 2. Чекаємо, поки ConnectionProvider побачить нову адресу
        withTimeout(2_000) {
            while (provider.current?.host != payload.host ||
                provider.current?.port != payload.port
            ) delay(20)
        }

        // 3. Обмінюємо pairingCode (id з QR) на токен
        val response = api.pair(PairRequestDto(payload.pairingCode))

        // 4. Зберігаємо токен
        store.saveToken(response.token)
    }.onFailure {
        store.clear()
    }

    override fun isPaired(): Flow<Boolean> =
        store.connection.map { it?.token != null }.distinctUntilChanged()

    override suspend fun unpair() {
        store.clear()
    }
}