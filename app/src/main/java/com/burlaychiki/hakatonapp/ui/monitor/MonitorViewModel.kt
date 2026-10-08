package com.burlaychiki.hakatonapp.ui.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burlaychiki.hakatonapp.domain.model.PcMetrics
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    repository: MetricsRepository
) : ViewModel() {

    val uiState: StateFlow<MonitorUiState> = combine(
        repository.observeMetrics().map<PcMetrics, PcMetrics?> { it }.onStart { emit(null) },
        repository.observeProcesses().onStart { emit(emptyList()) },
        repository.observeConnectionState()
    ) { metrics, processes, connection ->
        MonitorUiState(
            metrics = metrics ?: PcMetrics(cpu = 0f, gpu = 0f, ram = 0f),
            processes = processes,
            connectionState = connection,
            hasData = metrics != null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MonitorUiState()
    )
}