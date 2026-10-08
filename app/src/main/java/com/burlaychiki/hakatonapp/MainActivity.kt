package com.burlaychiki.hakatonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.burlaychiki.hakatonapp.ui.theme.HakatonappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HakatonappTheme {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("RemotePC: Hilt працює")
                    }
            }
        }
    }
}