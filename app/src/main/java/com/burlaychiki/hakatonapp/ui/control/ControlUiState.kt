package com.burlaychiki.hakatonapp.ui.control

data class ControlUiState(
    val delayMinutes: String = "0",
    val filePath: String = "",
    val isLoading: Boolean = false,
    val showConfirmDialog: Boolean = false
)