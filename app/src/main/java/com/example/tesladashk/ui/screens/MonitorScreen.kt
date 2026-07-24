package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.example.tesladashk.viewmodel.TeslaViewModel

private val TeslaDarkBg = Color(0xFF111113)
private val TeslaCardBg = Color(0xFF1C1C1E)
private val TeslaCardBorder = Color(0xFF2C2C2E)
private val TeslaGreen = Color(0xFF00E676)
private val TeslaBlue = Color(0xFF2997FF)
private val TeslaTextGray = Color(0xFF8E8E93)

@Composable
fun MonitorScreen(viewModel: TeslaViewModel) {
    var selectedSubTab by remember { mutableStateOf(0) }
    val subTabs = listOf("차량 정보", "주행 지도", "월간 리포트", "배터리")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(TeslaDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedSubTab,
                containerColor = TeslaCardBg,
                contentColor = Color.White,
                edgePadding = 0.dp,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                subTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSubTab == index,
                        onClick = { selectedSubTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedSubTab == index) Color.White else TeslaTextGray,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }
        }

        item {
            when (selectedSubTab) {
                0 -> VehicleInfoContent()
                1 -> DrivingMapContent()
                2 -> MonthlyReportContent()
                3 -> BatteryContent()
            }
        }
    }
}

@Composable
private fun VehicleInfoContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = "최근 차량 상태", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TeslaCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TeslaCardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF9800)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "현재: 주차 중", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔋 77%", color = TeslaGreen, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "📍 6,655 km", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TeslaCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TeslaCardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFF007AFF), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "P", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Column {
                    Text(text = "주차 중 7시간 22분", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "2026. 7. 25. 오전 1:10:43 부터 기준", color = TeslaTextGray, fontSize = 11.sp)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TeslaCardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = TeslaCardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "⚡ 최근 운행일 전체 기록", color = Color.White, fontWeight = FontWeight.Bold)
                    Text(text = "7월 22일 (총 3건)", color = TeslaTextGray, fontSize = 12.sp)
                }
                Text(text = "4.78 km/kWh (평균)", color = TeslaBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatItem("총 이동거리", "14.4 km")
                    StatItem("총 사용배터리", "-5.0%")
                    StatItem("총 운전시간", "45 분")
                    StatItem("총 사용에너지", "-3.0 kWh")
                }
            }
        }
    }
}

@Composable
private fun DrivingMapContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TeslaCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = TeslaCardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "🌐 당일 이동 경로", color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "은평구 신사1동 → 덕양구 창릉동 → 덕양구 흥도동 → 은평구 신사1동", color = TeslaBlue, fontSize = 13.sp)
            Divider(color = TeslaCardBorder, thickness = 1.dp)
            Text(text = "주행 4.4km | 시간 20분 | 배터리 2.0% 소비", color = TeslaTextGray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun MonthlyReportContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TeslaCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = TeslaCardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "📊 7월 주행 리포트", color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "• 이번 달 총 주행거리: 1,120 km", color = Color.White, fontSize = 13.sp)
            Text(text = "• 평균 전비: 148 Wh/km", color = Color.White, fontSize = 13.sp)
            Text(text = "• 절감한 수퍼차저/연료비: 약 ₩124,000", color = TeslaGreen, fontSize = 13.sp)
        }
    }
}

@Composable
private fun BatteryContent() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TeslaCardBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = TeslaCardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "🔋 배터리 건강도 (SoH)", color = Color.White, fontWeight = FontWeight.Bold)
            Text(text = "현재 잔량: 77%", color = TeslaGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "배터리 수명 (SoH): 98.4% (정상 및 안정적)", color = TeslaTextGray, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(text = label, color = TeslaTextGray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
