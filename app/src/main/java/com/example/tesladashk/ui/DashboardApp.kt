package com.example.tesladashk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.ui.screens.*
import com.example.tesladashk.viewmodel.TeslaViewModel

private val DarkBg = Color(0xFF0B0F17)
private val CardBg = Color(0xFF131B2E)
private val AccentBlue = Color(0xFF2563EB)
private val PrimaryText = Color(0xFFF1F5F9)
private val SubText = Color(0xFF94A3B8)

@Composable
fun DashboardApp(viewModel: TeslaViewModel) {
    var mainTab by remember { mutableStateOf(0) } // 0: 테슬라 모니터, 1: 감시 가디언
    var subTab by remember { mutableStateOf(0) }  // 0: 차량 정보, 1: 주행 지도, 2: 월간 리포트, 3: 배터리

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            TopBar(viewModel = viewModel)

            // Main Tab Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MainTabButton("🚗 테슬라 모니터", isSelected = mainTab == 0, modifier = Modifier.weight(1f)) {
                    mainTab = 0
                }
                MainTabButton("🛡️ 감시 가디언", isSelected = mainTab == 1, modifier = Modifier.weight(1f)) {
                    mainTab = 1
                }
            }

            if (mainTab == 0) {
                // Sub Tab Bar (차량 정보 | 주행 지도 | 월간 리포트 | 배터리)
                SubTabBar(selectedTab = subTab) { subTab = it }

                // Sub Screen Content
                Box(modifier = Modifier.fillMaxSize()) {
                    when (subTab) {
                        0 -> MonitorScreen(viewModel)
                        1 -> DrivingMapScreen(viewModel)
                        2 -> MonthlyReportScreen(viewModel)
                        3 -> BatteryScreen(viewModel)
                    }
                }
            } else {
                // Guardian Screen placeholder or implementation
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("가디언 모드가 활성화되어 차량 상태를 주기적으로 감시합니다.", color = PrimaryText)
                }
            }
        }
    }
}

@Composable
fun TopBar(viewModel: TeslaViewModel) {
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Tesla Command Hub", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text("Monitor & Guardian", fontSize = 11.sp, color = SubText)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = { viewModel.refreshData() },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1E293B))
            ) {
                Text(if (isRefreshing) "⏳" else "🔄", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun MainTabButton(title: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentBlue else Color(0xFF1E293B))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun SubTabBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabs = listOf("차량 정보", "주행 지도", "월간 리포트", "배터리")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF131B2E))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) AccentBlue else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    title,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) PrimaryText else SubText
                )
            }
        }
    }
}
