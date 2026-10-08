package com.burlaychiki.hakatonapp.ui.control

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burlaychiki.hakatonapp.domain.model.CommandResult
import com.burlaychiki.hakatonapp.domain.repository.PairingRepository
import com.burlaychiki.hakatonapp.domain.repository.PcControlRepository
import com.burlaychiki.hakatonapp.util.toUserMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(
    private val repository: PcControlRepository,
    private val pairingRepository: PairingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ControlUiState())
    val uiState: StateFlow<ControlUiState> = _uiState.asStateFlow()

    private val _events = Channel<ControlUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onDelayChange(value: String) {
        _uiState.update { it.copy(delayMinutes = value.filter(Char::isDigit).take(4)) }
    }

    fun onPathChange(value: String) {
        _uiState.update { it.copy(filePath = value) }
    }

    fun onShutdownClick() {
        val minutes = currentDelayMinutes()
        if (minutes > MAX_DELAY_MINUTES) {
            sendMessage("Максимальний час: $MAX_DELAY_MINUTES хв (24 години)")
            return
        }
        _uiState.update { it.copy(showConfirmDialog = true) }
    }

    fun onDismissDialog() {
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun onConfirmShutdown() {
        val seconds = currentDelayMinutes()
        _uiState.update { it.copy(showConfirmDialog = false) }
        execute { repository.shutdown(seconds) }
    }

    fun onCancelShutdown() {
        execute { repository.cancelShutdown() }
    }

    fun onOpenFile() {
        val path = _uiState.value.filePath.trim()
        if (path.isEmpty()) {
            sendMessage("Вкажіть шлях до файлу")
            return
        }
        execute { repository.openFile(path) }
    }

    /** Після успіху isPaired стає false, і навігація сама повертає на екран сканування. */
    fun onUnpair() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                pairingRepository.unpair()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _events.send(ControlUiEvent.ShowMessage(e.toUserMessage()))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun currentDelayMinutes(): Int =
        _uiState.value.delayMinutes.toIntOrNull() ?: 0

    private fun execute(block: suspend () -> CommandResult) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                CommandResult(success = false, message = e.toUserMessage())
            }
            _uiState.update { it.copy(isLoading = false) }
            _events.send(ControlUiEvent.ShowMessage(result.message))
        }
    }

    private fun sendMessage(text: String) {
        viewModelScope.launch { _events.send(ControlUiEvent.ShowMessage(text)) }
    }

    private companion object {
        const val MAX_DELAY_MINUTES = 1440
    }
}