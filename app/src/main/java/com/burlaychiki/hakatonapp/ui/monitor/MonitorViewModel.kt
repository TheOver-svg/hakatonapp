package com.burlaychiki.hakatonapp.ui.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    repository: MetricsRepository
) : ViewModel() {

    val uiState: StateFlow<MonitorUiState> = combine(
        repository.observeMetrics(),
        repository.observeProcesses(),
        repository.observeConnectionState()
    ) { metrics, processes, connection ->
        MonitorUiState(
            metrics = metrics,
            processes = processes,
            connectionState = connection
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MonitorUiState()
    )
}