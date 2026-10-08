package com.burlaychiki.hakatonapp.ui.monitor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.burlaychiki.hakatonapp.domain.model.ConnectionState
import com.burlaychiki.hakatonapp.ui.monitor.components.CircularMetric
import com.burlaychiki.hakatonapp.ui.monitor.components.ConnectionStatusBadge
import com.burlaychiki.hakatonapp.ui.monitor.components.ProcessCard
import com.burlaychiki.hakatonapp.ui.monitor.components.loadColor
import java.util.Locale

@Composable
fun MonitorScreen(
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Монітор ПК",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (state.connectionState == ConnectionState.Connected && !state.hasData) {
                    Text(
                        text = "Очікування даних від ПК...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            ConnectionStatusBadge(state = state.connectionState)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularMetric(
                label = "CPU",
                value = state.metrics.cpu,
                icon = Icons.Filled.Memory,
                modifier = Modifier.weight(1f)
            )
            CircularMetric(
                label = "GPU",
                value = state.metrics.gpu,
                icon = Icons.Filled.DeveloperBoard,
                modifier = Modifier.weight(1f)
            )
            CircularMetric(
                label = "RAM",
                value = state.metrics.ram,
                icon = Icons.Filled.Storage,
                modifier = Modifier.weight(1f)
            )
        }

        RamCard(
            ramPercent = state.metrics.ram,
            totalRamMb = state.metrics.totalRamMb
        )

        Text(
            text = "Активні процеси",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (state.processes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Text(
                    text = "Дані про процеси ще не надходять",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            // Без key: pid може повторюватись, ключ у LazyRow крашив би застосунок
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = state.processes) { process ->
                    ProcessCard(process = process)
                }
            }
        }
    }
}

@Composable
private fun RamCard(
    ramPercent: Float,
    totalRamMb: Float,
    modifier: Modifier = Modifier
) {
    val hasTotal = totalRamMb > 0f
    val usedMb = totalRamMb * ramPercent / 100f

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Оперативна пам'ять",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (hasTotal) "${formatGb(usedMb)} з ${formatGb(totalRamMb)}" else "Усього: невідомо",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (ramPercent / 100f).coerceIn(0f, 1f) },
                color = loadColor(ramPercent),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private fun formatGb(mb: Float): String =
    String.format(Locale.forLanguageTag("uk"), "%.1f ГБ", mb / 1024f)