package com.burlaychiki.hakatonapp.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.burlaychiki.hakatonapp.domain.repository.PairingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    pairingRepository: PairingRepository
) : ViewModel() {

    val isPaired: StateFlow<Boolean?> = pairingRepository.isPaired()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)
}