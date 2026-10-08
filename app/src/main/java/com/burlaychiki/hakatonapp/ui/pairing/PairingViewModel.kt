package com.burlaychiki.hakatonapp.ui.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burlaychiki.hakatonapp.data.qr.QrPayloadParser
import com.burlaychiki.hakatonapp.data.qr.QrScanResult
import com.burlaychiki.hakatonapp.data.qr.QrScanner
import com.burlaychiki.hakatonapp.domain.repository.PairingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val qrScanner: QrScanner,
    private val parser: QrPayloadParser,
    private val pairingRepository: PairingRepository
) : ViewModel() {

    private val _state = MutableStateFlow<PairingUiState>(PairingUiState.Idle)
    val state: StateFlow<PairingUiState> = _state.asStateFlow()

    fun onScanClick() {
        viewModelScope.launch {
            _state.value = PairingUiState.Scanning
            when (val result = qrScanner.scan()) {
                is QrScanResult.Success -> {
                    val payload = parser.parse(result.rawValue)
                    if (payload == null) {
                        _state.value = PairingUiState.Error("Невірний QR-код")
                        return@launch
                    }
                    _state.value = PairingUiState.Pairing
                    pairingRepository.pair(payload)
                        .onSuccess { _state.value = PairingUiState.Success }
                        .onFailure { _state.value = PairingUiState.Error(it.message ?: "Помилка") }
                }
                QrScanResult.Cancelled -> _state.value = PairingUiState.Idle
                is QrScanResult.Error -> _state.value = PairingUiState.Error(result.message)
            }
        }
    }
}