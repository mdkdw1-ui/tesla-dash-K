package com.example.tesladashk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonitorScreen(
    vehicleName: String,
    batteryLevel: Int,
    isLocked: Boolean,
    onLockToggle: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = vehicleName, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Battery: $batteryLevel%", style = MaterialTheme.typography.titleLarge)
                Text(text = if (isLocked) "Status: Locked 🔒" else "Status: Unlocked 🔓")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onLockToggle) {
                Text(if (isLocked) "Unlock" else "Lock")
            }
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
        }
    }
}
