package com.burlaychiki.hakatonapp.domain.repository

import com.burlaychiki.hakatonapp.domain.model.PairingPayload
import kotlinx.coroutines.flow.Flow

interface PairingRepository {
    suspend fun pair(payload: PairingPayload): Result<Unit>
    fun isPaired(): Flow<Boolean>
    suspend fun unpair()
}
