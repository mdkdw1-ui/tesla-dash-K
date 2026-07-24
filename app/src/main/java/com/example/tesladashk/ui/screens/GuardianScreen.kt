package com.example.tesladashk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun GuardianScreen(viewModel: TeslaViewModel, onToggleService: (Boolean) -> Unit) {
    val isGuardianActive by viewModel.isGuardianActive.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("감시 모드 가디언", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("백그라운드 감시 활성화", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = isGuardianActive,
                onCheckedChange = { checked ->
                    viewModel.setGuardianActive(checked)
                    onToggleService(checked)
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { viewModel.flashHeadlights() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("⚡ 전조등 테스트")
        }
    }
}
