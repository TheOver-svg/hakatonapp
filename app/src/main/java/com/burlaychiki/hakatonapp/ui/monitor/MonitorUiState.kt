package com.burlaychiki.hakatonapp.ui.monitor

import com.burlaychiki.hakatonapp.domain.model.ConnectionState
import com.burlaychiki.hakatonapp.domain.model.PcMetrics
import com.burlaychiki.hakatonapp.domain.model.PcProcess

data class MonitorUiState(
    val metrics: PcMetrics = PcMetrics(cpu = 0f, gpu = 0f, ram = 0f),
    val processes: List<PcProcess> = emptyList(),
    val connectionState: ConnectionState = ConnectionState.Connecting,
    val hasData: Boolean = false
)