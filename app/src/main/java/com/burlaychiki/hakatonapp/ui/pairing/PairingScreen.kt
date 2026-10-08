package com.burlaychiki.hakatonapp.ui.pairing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PairingScreen(
    onPaired: () -> Unit,
    viewModel: PairingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is PairingUiState.Success) onPaired()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = viewModel::onScanClick,
            enabled = state !is PairingUiState.Pairing
        ) {
            Text("Сканувати QR")
        }

        (state as? PairingUiState.Error)?.let {
            Spacer(Modifier.height(16.dp))
            Text(it.message, color = MaterialTheme.colorScheme.error)
        }
        if (state is PairingUiState.Pairing) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}