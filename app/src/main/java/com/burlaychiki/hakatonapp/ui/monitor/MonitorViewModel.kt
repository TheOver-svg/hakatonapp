package com.burlaychiki.hakatonapp.ui.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burlaychiki.hakatonapp.domain.model.PcMetrics
import com.burlaychiki.hakatonapp.domain.model.PcProcess
import com.burlaychiki.hakatonapp.domain.repository.MetricsRepository
import com.burlaychiki.hakatonapp.domain.repository.PcControlRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonitorViewModel @Inject constructor(
    repository: MetricsRepository,
    private val control: PcControlRepository
) : ViewModel() {

    // onStart дає початкове значення, щоб стан зʼєднання показувався ще до першої метрики
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

    private val _processToKill = MutableStateFlow<PcProcess?>(null)
    val processToKill: StateFlow<PcProcess?> = _processToKill.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onKillClick(process: PcProcess) {
        _processToKill.value = process
    }

    fun onDismissKill() {
        _processToKill.value = null
    }

    fun onConfirmKill() {
        val process = _processToKill.value ?: return
        _processToKill.value = null
        viewModelScope.launch {
            val result = control.killProcess(process.pid)
            _events.send(if (result.success) "Завершення «${process.name}» надіслано" else result.message)
        }
    }
}