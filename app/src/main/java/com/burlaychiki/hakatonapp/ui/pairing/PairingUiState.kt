package com.burlaychiki.hakatonapp.ui.pairing

sealed interface PairingUiState {
    data object Idle : PairingUiState
    data object Scanning : PairingUiState
    data object Pairing : PairingUiState
    data object Success : PairingUiState
    data class Error(val message: String) : PairingUiState
}