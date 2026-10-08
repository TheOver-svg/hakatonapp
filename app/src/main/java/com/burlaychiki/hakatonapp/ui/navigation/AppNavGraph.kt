package com.burlaychiki.hakatonapp.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.burlaychiki.hakatonapp.ui.control.ControlScreen
import com.burlaychiki.hakatonapp.ui.monitor.MonitorScreen
import com.burlaychiki.hakatonapp.ui.pairing.PairingScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = hiltViewModel()
) {
    val isPaired by appViewModel.isPaired.collectAsStateWithLifecycle()
    val paired = isPaired

    if (paired == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        AppNavHost(
            navController = navController,
            isPaired = paired,
            modifier = modifier
        )
    }
}

@Composable
private fun AppNavHost(
    navController: NavHostController,
    isPaired: Boolean,
    modifier: Modifier
) {
    // Стартовий екран визначається один раз, інакше NavHost перестворює граф
    val startDestination = remember {
        if (isPaired) Screen.Monitor.route else Screen.Pairing.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Pairing.route) {
            PairingScreen(
                onPaired = {
                    navController.navigate(Screen.Monitor.route) {
                        popUpTo(Screen.Pairing.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Monitor.route) { MonitorScreen() }
        composable(Screen.Control.route) { ControlScreen() }
    }

    // Якщо підключення скасовано (unpair), повертаємось на екран сканування
    LaunchedEffect(isPaired) {
        val current = navController.currentDestination?.route
        if (!isPaired && current != null && current != Screen.Pairing.route) {
            navController.navigate(Screen.Pairing.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }
}