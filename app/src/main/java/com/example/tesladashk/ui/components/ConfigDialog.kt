package com.example.tesladashk.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tesladashk.network.AppConfig
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun ConfigDialog(
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val config by viewModel.config.collectAsState()
    var vehicleId by remember { mutableStateOf(config.vehicleId) }
    var ntfyTopic by remember { mutableStateOf(config.ntfyTopic) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("설정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Vehicle ID") }
                )
                OutlinedTextField(
                    value = ntfyTopic,
                    onValueChange = { ntfyTopic = it },
                    label = { Text("Ntfy Topic") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.saveConfig(
                    config.copy(vehicleId = vehicleId, ntfyTopic = ntfyTopic)
                )
                onDismiss()
            }) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
