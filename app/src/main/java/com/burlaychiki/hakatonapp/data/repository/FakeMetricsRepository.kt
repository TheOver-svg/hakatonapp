package com.burlaychiki.hakatonapp.data.repository

import com.burlaychiki.hakatonapp.domain.model.ConnectionState
import com.burlaychiki.hakatonapp.domain.model.PcMetrics
import com.burlaychiki.hakatonapp.domain.model.PcProcess
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeMetricsRepository @Inject constructor() : MetricsRepository {

    private val fakeProcesses = listOf(
        "chrome.exe" to 1204,
        "Code.exe" to 3320,
        "Discord.exe" to 4188,
        "Steam.exe" to 5012,
        "explorer.exe" to 884,
        "obs64.exe" to 6240,
        "Telegram.exe" to 7021
    )

    override fun observeMetrics(): Flow<PcMetrics> = flow {
        var cpu = 30f
        var gpu = 20f
        var ram = 50f
        while (true) {
            cpu = drift(cpu)
            gpu = drift(gpu)
            ram = drift(ram, step = 3f)
            emit(PcMetrics(cpu = cpu, gpu = gpu, ram = ram))
            delay(1000)
        }
    }

    override fun observeProcesses(): Flow<List<PcProcess>> = flow {
        while (true) {
            val list = fakeProcesses
                .map { (name, pid) ->
                    PcProcess(
                        pid = pid,
                        name = name,
                        cpuPercent = Random.nextFloat() * 25f,
                        memoryMb = Random.nextInt(80, 1800)
                    )
                }
                .sortedByDescending { it.cpuPercent }
            emit(list)
            delay(2000)
        }
    }

    override fun observeConnectionState(): Flow<ConnectionState> = flow {
        emit(ConnectionState.Connecting)
        delay(1500)
        emit(ConnectionState.Connected)
    }

    private fun drift(value: Float, step: Float = 8f): Float =
        (value + Random.nextFloat() * step * 2 - step).coerceIn(2f, 98f)
}