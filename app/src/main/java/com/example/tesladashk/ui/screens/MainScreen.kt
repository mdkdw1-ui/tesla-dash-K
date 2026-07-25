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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tesladashk.viewmodel.DashboardViewModel
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun MainScreen(
    viewModel: DashboardViewModel,
    onOpenSettings: () -> Unit
) {
    val teslaViewModel: TeslaViewModel = viewModel()
    var mainTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
    ) {
        // 상단 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Tesla Command Hub",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Monitor & Guardian",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.triggerSyncAndFetch() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("⚡ Sync", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onOpenSettings,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("⚙️ 설정", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // 메인 탭 (테슬라 모니터 / 감시 가디언)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { mainTab = 0 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mainTab == 0) Color(0xFF2563EB) else Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("⚡ 테슬라 모니터", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { mainTab = 1 },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (mainTab == 1) Color(0xFFDC2626) else Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🛡️ 감시 가디언", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (mainTab == 0) {
                TeslaMonitorScreen(viewModel = viewModel)
            } else {
                GuardianScreen(viewModel = teslaViewModel)
            }
        }
    }
}
