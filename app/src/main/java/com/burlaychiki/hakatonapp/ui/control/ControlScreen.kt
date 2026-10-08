package com.burlaychiki.hakatonapp.ui.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burlaychiki.hakatonapp.ui.control.components.ConfirmDialog
import com.burlaychiki.hakatonapp.ui.control.components.OpenFileCard
import com.burlaychiki.hakatonapp.ui.control.components.ShutdownCard
import com.burlaychiki.hakatonapp.ui.control.components.UnpairButton

@Composable
fun ControlScreen(
    modifier: Modifier = Modifier,
    viewModel: ControlViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ControlUiEvent.ShowMessage -> snackbarHostState.showSnackbar(event.text)
            }
        }
    }

    if (state.showConfirmDialog) {
        val minutes = state.delayMinutes.toIntOrNull() ?: 0
        ConfirmDialog(
            title = "Вимкнути ПК?",
            text = if (minutes == 0) "ПК буде вимкнено негайно."
            else "ПК буде вимкнено через $minutes хв.",
            confirmText = "Вимкнути",
            onConfirm = viewModel::onConfirmShutdown,
            onDismiss = viewModel::onDismissDialog
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Керування ПК",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            ShutdownCard(
                delayMinutes = state.delayMinutes,
                onDelayChange = viewModel::onDelayChange,
                onShutdown = viewModel::onShutdownClick,
                onCancel = viewModel::onCancelShutdown,
                enabled = !state.isLoading
            )

            OpenFileCard(
                path = state.filePath,
                onPathChange = viewModel::onPathChange,
                onOpen = viewModel::onOpenFile,
                enabled = !state.isLoading
            )

            UnpairButton(
                onClick = viewModel::onUnpair,
                enabled = !state.isLoading
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}