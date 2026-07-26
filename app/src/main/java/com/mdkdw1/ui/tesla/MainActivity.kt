package com.mdkdw1.ui.tesla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeslaDashApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaDashApp() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("내 차량정보", "주행정보", "월간리포트", "배터리")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("테슬라 모니터", color = Color.White, fontSize = 18.sp)
                        Text("감시 가디언 시스템", color = Color.Gray, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF161820))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF161820)) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White)
                                1 -> Icon(Icons.Default.Map, contentDescription = null, tint = Color.White)
                                2 -> Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White)
                                3 -> Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color.White)
                            }
                        },
                        label = { Text(title, color = Color.White, fontSize = 11.sp) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        },
        containerColor = Color(0xFF0D0E12)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> VehicleInfoScreen()
                1 -> DrivingInfoScreen()
                2 -> MonthlyReportScreen()
                3 -> BatteryScreen()
            }
        }
    }
}

@Composable
fun VehicleInfoScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("내 차량정보 (도어, 트렁크, 공조 제어 및 실시간 상태)", color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun DrivingInfoScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("주행정보 (카카오맵 위치 추적 및 주행 기록 로그)", color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun MonthlyReportScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("월간리포트 (충전 기록 및 비용 관리 분석)", color = Color.White, fontSize = 16.sp)
    }
}

@Composable
fun BatteryScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("배터리 (SOH 열화율 및 배터리 상태 그래프)", color = Color.White, fontSize = 16.sp)
    }
}
