package com.burlaychiki.hakatonapp.ui.control

sealed interface ControlUiEvent {
    data class ShowMessage(val text: String) : ControlUiEvent
}