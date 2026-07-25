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
fun SettingsScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val config by viewModel.config.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚙️ 앱 설정",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
            ) {
                Text("닫기", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("현재 연동 정보", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Vehicle ID: ${config.vehicleId}", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Supabase URL: ${config.supabaseUrl.ifBlank { "설정됨" }}", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
