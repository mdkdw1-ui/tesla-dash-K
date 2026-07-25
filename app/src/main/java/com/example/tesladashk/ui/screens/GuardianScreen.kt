package com.example.tesladashk.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.service.GuardianService
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun GuardianScreen(viewModel: TeslaViewModel) {
    val context = LocalContext.current
    var isGuardianActive by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🛡️ 감시 모드 가디언", color = Color(0xFFEF4444), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isGuardianActive,
                        onCheckedChange = { active ->
                            isGuardianActive = active
                            val intent = Intent(context, GuardianService::class.java).apply {
                                putExtra("ACCESS_TOKEN", viewModel.config.value.accessToken)
                                putExtra("VEHICLE_ID", viewModel.config.value.vehicleId)
                                putExtra("NTFY_TOPIC", viewModel.config.value.ntfyTopic)
                            }
                            if (active) {
                                context.startForegroundService(intent)
                            } else {
                                context.stopService(intent)
                            }
                        }
                    )
                }
                Text("백그라운드에서 실시간 문/트렁크 무단 열림 및 차량 충격을 감시합니다.", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
