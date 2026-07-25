package com.example.tesladashk.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.network.TripItem
import com.example.tesladashk.network.VehicleState
import com.example.tesladashk.ui.*

@Composable
fun SubTabBar(selectedSubTab: String, onSubTabSelected: (String) -> Unit) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                "vehicle" to "차량 정보",
                "driving" to "주행 지도",
                "monthly" to "월간 리포트",
                "battery" to "배터리"
            )
            tabs.forEach { (key, label) ->
                val isSelected = selectedSubTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AccentBlue else Color.Transparent)
                        .clickable { onSubTabSelected(key) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (isSelected) Color.White else TextGray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleInfoScreen(state: VehicleState, trips: List<TripItem>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AccentAmber)
                )
                Text(state.statusText, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🔋 ${state.batteryLevel}%", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("📍 ${String.format("%,d", state.odometer)} km", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AccentAmber),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🅿️", fontSize = 24.sp)
                Column {
                    Text(state.parkDurationStr, color = AccentAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("주차 상태 정보 수신됨", color = TextGray, fontSize = 11.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚡ 최근 운행일 전체 기록", color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDarker)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("총 ${trips.size}건", color = TextGray, fontSize = 10.sp)
                }
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("6.45", color = AccentIndigo, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("km/kWh (평균)", color = TextGray, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
            }

            Divider(color = BorderGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricColumn("총 이동거리", "26.6 km")
                MetricColumn("총 사용배터리", "-9.7%")
                MetricColumn("총 운전시간", "57 분")
                MetricColumn("총 사용에너지", "-5.8 kWh")
            }

            trips.forEach { trip ->
                TripDetailCard(trip)
            }
        }
    }

    Spacer(modifier = Modifier.height(10.dp))

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛞 타이어 공기압 (TPMS)", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("🌡️ ${state.outsideTemp}°C", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TpmsBox("앞 왼쪽 (FL)", "${state.tpmsFl} psi", Modifier.weight(1f))
                TpmsBox("앞 오른쪽 (FR)", "${state.tpmsFr} psi", Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TpmsBox("뒤 왼쪽 (RL)", "${state.tpmsRl} psi", Modifier.weight(1f))
                TpmsBox("뒤 오른쪽 (RR)", "${state.tpmsRr} psi", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MonthlyReportScreen() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("📊 월간 리포트", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("총 주행거리", "452.8 km", AccentEmerald, Modifier.weight(1f))
                MetricCard("주행한 날", "14 일", AccentBlue, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("충전 횟수", "8 회", AccentTeal, Modifier.weight(1f))
                MetricCard("추정 충전 비용", "32,400 원", AccentIndigo, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BatteryScreen() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("🔋 배터리 상태 및 열화율", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("최근 열화율", "98.5%", AccentIndigo, Modifier.weight(1f))
                MetricCard("100% 예상거리", "428 km", AccentEmerald, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DrivingMapPlaceholder() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🗺️", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("카카오맵 주행 경로 화면", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGray, fontSize = 10.sp)
        Text(value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TpmsBox(label: String, pressure: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardDarker)
            .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = TextGray, fontSize = 10.sp)
            Text(pressure, color = AccentBlue, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun TripDetailCard(trip: TripItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardDarker)
            .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(trip.startDong, color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("→", color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(trip.endDong, color = AccentEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(
                "주행 ${trip.moveKm}km, 시간 ${trip.durationMin}분, 배터리 -${trip.useBattery}% (${trip.startBat}% → ${trip.endBat}%), 누적 ${String.format("%,d", trip.odometer)}km",
                color = TextGray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardDarker)
            .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = TextGray, fontSize = 10.sp)
            Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
