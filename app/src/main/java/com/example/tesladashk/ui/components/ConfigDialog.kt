package com.example.tesladashk.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun ConfigDialog(
    viewModel: DashboardViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsState()

    var supabaseUrl by remember { mutableStateOf(config.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(config.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(config.kakaoKey) }
    var vehicleId by remember { mutableStateOf(config.vehicleId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("⚡ 환경 설정") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("카카오맵 API Key") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Vehicle ID") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.saveConfig(
                    context,
                    config.copy(
                        supabaseUrl = supabaseUrl,
                        supabaseKey = supabaseKey,
                        kakaoKey = kakaoKey,
                        vehicleId = vehicleId
                    )
                )
                onDismiss()
            }) { Text("저장") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}
