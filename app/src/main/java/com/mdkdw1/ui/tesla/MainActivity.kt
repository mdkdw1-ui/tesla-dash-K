package com.mdkdw1.ui.tesla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("내 차량정보", "주행정보", "월간리포트", "배터리")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "테슬라 모니터",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "감시 가디언 시스템",
                            color = Color(0xFFA0A0A0),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF161820)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF161820)
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.DirectionsCar, contentDescription = "내 차량정보", tint = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray)
                                1 -> Icon(Icons.Default.Map, contentDescription = "주행정보", tint = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray)
                                2 -> Icon(Icons.Default.Assessment, contentDescription = "월간리포트", tint = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray)
                                3 -> Icon(Icons.Default.BatteryChargingFull, contentDescription = "배터리", tint = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray)
                            }
                        },
                        label = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) Color(0xFF3B82F6) else Color.Gray,
                                fontSize = 11.sp
                            )
                        },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        },
        containerColor = Color(0xFF0D0E12)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFF0D0E12))
        ) {
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "내 차량정보",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("차량 도어 및 트렁크 상태", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("운전석 도어: 잠김", color = Color(0xFF10B981), fontSize = 14.sp)
                    Text("보조석 도어: 잠김", color = Color(0xFF10B981), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("전면 트렁크(프렁크): 닫힘", color = Color(0xFF10B981), fontSize = 14.sp)
                    Text("후면 트렁크: 닫힘", color = Color(0xFF10B981), fontSize = 14.sp)
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("공조 시스템 (HVAC)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("실내 온도: 22.0°C (설정 온도: 21.5°C)", color = Color(0xFFD1D5DB), fontSize = 14.sp)
                Text("공조 상태: 정상 가동 중", color = Color(0xFF3B82F6), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun DrivingInfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "주행정보",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("실시간 위치 추적 및 카카오맵 연동", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("현재 위치: 서울특별시 강남구 테헤란로", color = Color(0xFFD1D5DB), fontSize = 14.sp)
                Text("주행 속도: 0 km/h (정차 중)", color = Color(0xFFD1D5DB), fontSize = 14.sp)
                Text("누적 주행거리: 24,580 km", color = Color(0xFFD1D5DB), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun MonthlyReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "월간리포트",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("이번 달 충전 및 전비 요약", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("총 충전량: 320 kWh", color = Color(0xFFD1D5DB), fontSize = 14.sp)
                Text("총 충전 비용: 74,500 원", color = Color(0xFFD1D5DB), fontSize = 14.sp)
                Text("평균 전비: 6.8 km/kWh", color = Color(0xFF10B981), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun BatteryScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "배터리 상태",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("SOH (배터리 건강 상태)", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("현재 SOC: 82%", color = Color(0xFF3B82F6), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("배터리 SOH: 97.4% (열화율 2.6%)", color = Color(0xFF10B981), fontSize = 14.sp)
                Text("팩 온도: 24.5°C", color = Color(0xFFD1D5DB), fontSize = 14.sp)
            }
        }
    }
}
