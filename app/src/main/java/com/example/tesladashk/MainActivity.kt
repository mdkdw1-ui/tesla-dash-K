package com.example.tesladashk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.tesladashk.ui.screens.MainScreen
import com.example.tesladashk.ui.screens.SettingsScreen
import com.example.tesladashk.ui.theme.TeslaDashKTheme
import com.example.tesladashk.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dashboardViewModel.loadInitialConfig(this)

        setContent {
            TeslaDashKTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D0E12)
                ) {
                    var showSettings by remember { mutableStateOf(false) }

                    if (showSettings) {
                        SettingsScreen(
                            viewModel = dashboardViewModel,
                            onBack = { showSettings = false }
                        )
                    } else {
                        MainScreen(
                            viewModel = dashboardViewModel,
                            onOpenSettings = { showSettings = true }
                        )
                    }
                }
            }
        }
    }
}
