package com.example.tesladashk.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.tesladashk.ui.screens.GuardianScreen
import com.example.tesladashk.ui.screens.MonitorScreen
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun DashboardApp(viewModel: TeslaViewModel, onToggleService: (Boolean) -> Unit) {
    var selectedMainTab by remember { mutableStateOf("monitor") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("🚗") },
                    label = { Text("테슬라 모니터") },
                    selected = selectedMainTab == "monitor",
                    onClick = { selectedMainTab = "monitor" }
                )
                NavigationBarItem(
                    icon = { Text("🛡️") },
                    label = { Text("감시 가디언") },
                    selected = selectedMainTab == "guardian",
                    onClick = { selectedMainTab = "guardian" }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedMainTab) {
                "monitor" -> MonitorScreen()
                "guardian" -> GuardianScreen(viewModel, onToggleService)
            }
        }
    }
}
