package com.burlaychiki.hakatonapp.data.repository

import com.burlaychiki.hakatonapp.data.remote.dto.MetricsDto
import com.burlaychiki.hakatonapp.data.remote.socket.HubClient
import com.burlaychiki.hakatonapp.domain.model.ConnectionState
import com.burlaychiki.hakatonapp.domain.model.PcMetrics
import com.burlaychiki.hakatonapp.domain.model.PcProcess
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsRepositoryImpl @Inject constructor(
    private val hub: HubClient
) : MetricsRepository {

    override fun observeMetrics(): Flow<PcMetrics> = hub.metrics.map { it.toPcMetrics() }

    override fun observeProcesses(): Flow<List<PcProcess>> = hub.metrics.map { dto ->
        dto.processes.orEmpty().map {
            PcProcess(
                pid = it.pid,
                name = it.name ?: "?",
                cpuPercent = it.cpuPercent,
                memoryMb = it.memoryMb
            )
        }
    }

    override fun observeConnectionState(): Flow<ConnectionState> = hub.state

    // CPU і RAM приходять частками 0..1, GPU вже у відсотках, totalRam у мегабайтах
    private fun MetricsDto.toPcMetrics(): PcMetrics = PcMetrics(
        cpu = (cpuLoad * 100f).coerceIn(0f, 100f),
        gpu = gpuLoad.coerceIn(0f, 100f),
        ram = (ramLoad * 100f).coerceIn(0f, 100f),
        totalRamMb = totalRam.toFloat().coerceAtLeast(0f)
    )
}