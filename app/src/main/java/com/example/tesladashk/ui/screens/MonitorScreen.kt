package com.example.tesladashk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonitorScreen() {
    var selectedSubTab by remember { mutableStateOf("vehicle") }

    Column {
        TabRow(selectedTabIndex = listOf("vehicle", "driving", "monthly", "battery").indexOf(selectedSubTab)) {
            Tab(selected = selectedSubTab == "vehicle", onClick = { selectedSubTab = "vehicle" }) { Text("차량 정보") }
            Tab(selected = selectedSubTab == "driving", onClick = { selectedSubTab = "driving" }) { Text("주행 지도") }
            Tab(selected = selectedSubTab == "monthly", onClick = { selectedSubTab = "monthly" }) { Text("월간 리포트") }
            Tab(selected = selectedSubTab == "battery", onClick = { selectedSubTab = "battery" }) { Text("배터리") }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (selectedSubTab) {
                "vehicle" -> Text("Model Y Juniper (신형 YL) 상태 대시보드", style = MaterialTheme.typography.titleLarge)
                "driving" -> Text("카카오맵 주행 경로 연동 뷰", style = MaterialTheme.typography.titleLarge)
                "monthly" -> Text("월간 주행/충전 리포트", style = MaterialTheme.typography.titleLarge)
                "battery" -> Text("배터리 상태 및 충전 로그", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
