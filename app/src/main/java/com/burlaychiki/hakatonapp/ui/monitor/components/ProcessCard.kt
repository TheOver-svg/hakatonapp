package com.burlaychiki.hakatonapp.ui.monitor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.burlaychiki.hakatonapp.domain.model.PcProcess
import java.util.Locale

@Composable
fun ProcessCard(
    process: PcProcess,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.width(170.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = process.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "PID ${process.pid}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "CPU: ${String.format(Locale.US, "%.1f", process.cpuPercent)}%",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "RAM: ${process.memoryMb} МБ",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}