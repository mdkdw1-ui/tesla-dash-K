package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun GuardianScreen(viewModel: DashboardViewModel) {
    val config by viewModel.config.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        Text(
            text = "🛡️ 테슬라 가디언 & 감시모드",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("현재 연동 상태", color = Color(0xFF3B82F6), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Vehicle ID: ${if (config.vehicleId.isNotBlank()) config.vehicleId else "설정되지 않음"}", color = Color.LightGray, fontSize = 13.sp)
                Text("Ntfy Topic: ${if (config.ntfyTopic.isNotBlank()) config.ntfyTopic else "설정되지 않음"}", color = Color.LightGray, fontSize = 13.sp)
                Text("Access Token: ${if (config.accessToken.isNotBlank()) "등록됨 (••••)" else "설정되지 않음"}", color = Color.LightGray, fontSize = 13.sp)
            }
        }
    }
}
