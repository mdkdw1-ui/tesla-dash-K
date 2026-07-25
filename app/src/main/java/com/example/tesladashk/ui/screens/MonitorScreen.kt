package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.tesladashk.network.TripItem
import com.example.tesladashk.network.VehicleState
import com.example.tesladashk.viewmodel.TeslaViewModel
import java.util.Locale

// UI Color Palette
private val CardBg = Color(0xFF131B2E)
private val CardBorder = Color(0xFF1E293B)
private val DarkBg = Color(0xFF0B0F17)
private val PrimaryText = Color(0xFFF1F5F9)
private val SubText = Color(0xFF94A3B8)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentGreen = Color(0xFF10B981)
private val AccentOrange = Color(0xFFF59E0B)

@Composable
fun MonitorScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val trips by viewModel.trips.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. 최근 차량 상태 패널
        item {
            VehicleStatusPanel(vehicleState)
        }

        // 2. 최근 주행일 전체 기록 (최신 주행일 자동 집계)
        val latestTripDate = trips.firstOrNull()?.date?.take(10) ?: ""
        val latestDayTrips = trips.filter { it.date.startsWith(latestTripDate) }
        if (latestDayTrips.isNotEmpty()) {
            item {
                LatestDaySummaryPanel(dateStr = latestTripDate, dayTrips = latestDayTrips)
            }
        }

        // 3. 타이어 공기압 (TPMS) 패널
        item {
            TpmsPanel(vehicleState)
        }

        // 4. 최근 상태 히스토리 (날짜별 그룹화)
        item {
            Text(
                text = "최근 상태 히스토리",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        val groupedTrips = trips.groupBy { it.date.take(10).ifEmpty { "최근 기록" } }
        items(groupedTrips.keys.toList()) { dateKey ->
            val dayTrips = groupedTrips[dateKey] ?: emptyList()
            DateHistoryGroupPanel(dateStr = dateKey, trips = dayTrips)
        }
    }
}

// 1. 최근 차량 상태 패널
@Composable
fun VehicleStatusPanel(state: VehicleState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("최근 차량 상태", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text("실시간 수신", fontSize = 12.sp, color = SubText)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatusChip(text = state.statusText, color = AccentOrange)
            StatusChip(text = "🔋 ${state.batteryLevel}%", color = AccentGreen)
            StatusChip(text = "📍 ${String.format(Locale.getDefault(), "%,d", state.odometer)} km", color = AccentBlue)
        }

        // 주차/상태 세부 정보
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E2638))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AccentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🅿️", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("주차 상태 정보", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(state.parkDurationStr, fontSize = 12.sp, color = SubText)
                }
            }
        }
    }
}

// 2. 최근 주행일 전체 기록 패널
@Composable
fun LatestDaySummaryPanel(dateStr: String, dayTrips: List<TripItem>) {
    val totalDistance = dayTrips.sumOf { it.distanceKm.ifNaN(0.0) }
    val totalBattery = dayTrips.sumOf { it.batteryUsed.ifNaN(0.0) }
    val totalTime = dayTrips.sumOf { it.driveTimeMin }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⚡ 최근 주행일 전체 기록", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            }
            Text("$dateStr (${dayTrips.size}건)", fontSize = 12.sp, color = SubText)
        }

        // 통계 4칸 요약
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MetricItem("총 이동거리", String.format(Locale.getDefault(), "%.1f km", totalDistance))
            MetricItem("총 사용배터리", String.format(Locale.getDefault(), "%.1f %%", totalBattery))
            MetricItem("총 운전시간", "${totalTime} 분")
            MetricItem("주행 건수", "${dayTrips.size} 건")
        }

        Divider(color = CardBorder, thickness = 1.dp)

        // 이동 경로
        val pathStr = dayTrips.reversed().joinToString(" ➔ ") { it.startAddress.ifEmpty { "출발지" } } +
                if (dayTrips.isNotEmpty()) " ➔ ${dayTrips.first().endAddress.ifEmpty { "도착지" }}" else ""

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF161F33))
                .padding(10.dp)
        ) {
            Text("📍 당일 이동 경로", fontSize = 11.sp, color = AccentBlue, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(pathStr, fontSize = 12.sp, color = PrimaryText)
        }
    }
}

// 3. TPMS 타이어 공기압 패널
@Composable
fun TpmsPanel(state: VehicleState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("🛞 타이어 공기압 (TPMS)", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
            Text("psi", fontSize = 12.sp, color = SubText)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TireBox("운전석 앞 (FL)", state.tpmsFl, Modifier.weight(1f))
            TireBox("조수석 앞 (FR)", state.tpmsFr, Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TireBox("운전석 뒤 (RL)", state.tpmsRl, Modifier.weight(1f))
            TireBox("조수석 뒤 (RR)", state.tpmsRr, Modifier.weight(1f))
        }
    }
}

@Composable
fun TireBox(label: String, psi: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF182238))
            .border(1.dp, Color(0xFF263352), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, fontSize = 11.sp, color = SubText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            if (psi > 0) String.format(Locale.getDefault(), "%.1f psi", psi) else "41 psi",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = AccentBlue
        )
    }
}

// 4. 날짜별 히스토리 그룹 패널
@Composable
fun DateHistoryGroupPanel(dateStr: String, trips: List<TripItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 날짜 헤더
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1E293B))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📅 $dateStr", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
        }

        // 해당 날짜의 주행 개별 카드들
        trips.forEach { trip ->
            TripHistoryItemRow(trip)
        }
    }
}

@Composable
fun TripHistoryItemRow(trip: TripItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF161F33))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusChip(text = "🚗 주행", color = AccentBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(trip.timeStr.takeLast(5).ifEmpty { "시간 미상" }, fontSize = 12.sp, color = SubText)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "${trip.startAddress} ➔ ${trip.endAddress}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "주행 ${String.format(Locale.getDefault(), "%.1f", trip.distanceKm)}km, 시간 ${trip.driveTimeMin}분, 배터리 ${String.format(Locale.getDefault(), "%.1f", trip.batteryUsed)}%",
                fontSize = 11.sp,
                color = SubText
            )
        }
    }
}

@Composable
fun StatusChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = SubText)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
    }
}

private fun Double.ifNaN(default: Double): Double = if (this.isNaN()) default else this
