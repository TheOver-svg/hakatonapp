package com.burlaychiki.hakatonapp.domain.repository

import com.burlaychiki.hakatonapp.domain.model.ConnectionState
import com.burlaychiki.hakatonapp.domain.model.PcMetrics
import com.burlaychiki.hakatonapp.domain.model.PcProcess
import kotlinx.coroutines.flow.Flow

interface MetricsRepository {
    fun observeMetrics(): Flow<PcMetrics>
    fun observeProcesses(): Flow<List<PcProcess>>
    fun observeConnectionState(): Flow<ConnectionState>
}