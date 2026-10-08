package com.burlaychiki.hakatonapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.burlaychiki.hakatonapp.ui.control.ControlScreen
import com.burlaychiki.hakatonapp.ui.monitor.MonitorScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Monitor.route,
        modifier = modifier
    ) {
        composable(Screen.Monitor.route) {
            MonitorScreen()
        }
        composable(Screen.Control.route) {
            ControlScreen()
        }
    }
}