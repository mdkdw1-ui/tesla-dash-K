package com.example.tesladashk.ui

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
import com.example.tesladashk.ui.screens.GuardianScreen
import com.example.tesladashk.ui.screens.MonitorScreen
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun DashboardApp(viewModel: TeslaViewModel) {
    var mainTab by remember { mutableStateOf("monitor") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
    ) {
        HeaderBar(
            onRefresh = { },
            onSync = { },
            onOpenConfig = { }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF12141C))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { mainTab = "monitor" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mainTab == "monitor") Color(0xFF2563EB) else Color(0xFF1F2937)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("⚡ 테슬라 모니터", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { mainTab = "guardian" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mainTab == "guardian") Color(0xFFDC2626) else Color(0xFF1F2937)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🛡️ 감시 가디언", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        if (mainTab == "monitor") {
            MonitorScreen(viewModel)
        } else {
            GuardianScreen(viewModel)
        }
    }
}

@Composable
fun HeaderBar(onRefresh: () -> Unit, onSync: () -> Unit, onOpenConfig: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF12141C))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Tesla Command Hub", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text("Monitor & Guardian", color = Color.Gray, fontSize = 9.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(onClick = onRefresh, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Text("🔄 갱신", fontSize = 11.sp)
            }
            Button(onClick = onSync, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Text("⚡ Sync", fontSize = 11.sp)
            }
            Button(onClick = onOpenConfig, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                Text("⚙️ 설정", fontSize = 11.sp)
            }
        }
    }
}
