package com.burlaychiki.hakatonapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SettingsRemote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Pairing : Screen(
        route = "pairing",
        title = "Підключення",
        icon = Icons.Filled.QrCodeScanner
    )

    data object Monitor : Screen(
        route = "monitor",
        title = "Монітор",
        icon = Icons.Filled.Speed
    )

    data object Control : Screen(
        route = "control",
        title = "Керування",
        icon = Icons.Filled.SettingsRemote
    )

    companion object {
        val bottomBarItems = listOf(Monitor, Control)
    }
}