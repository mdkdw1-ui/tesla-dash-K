package com.example.tesladashk.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.tesladashk.ui.screens.GuardianScreen
import com.example.tesladashk.ui.screens.MonitorScreen
import com.example.tesladashk.viewmodel.TeslaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardApp(viewModel: TeslaViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("TeslaDash-K Dashboard") })
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Monitor") },
                    icon = {}
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Guardian") },
                    icon = {}
                )
            }
        }
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> MonitorScreen(
                    vehicleName = state.vehicleName,
                    batteryLevel = state.batteryLevel,
                    isLocked = state.isLocked,
                    onLockToggle = { viewModel.toggleLock() },
                    onRefresh = { viewModel.refreshState() }
                )
                1 -> GuardianScreen(
                    logs = state.logs,
                    onSendAlert = { topic, msg -> viewModel.sendNtfyAlert(topic, msg) }
                )
            }
        }
    }
}
