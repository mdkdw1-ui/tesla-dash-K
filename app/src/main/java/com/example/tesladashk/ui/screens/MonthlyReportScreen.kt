package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.tesladashk.ui.theme.*
import com.example.tesladashk.viewmodel.TeslaViewModel
import java.util.Locale

@Composable
fun MonthlyReportScreen(viewModel: TeslaViewModel) {
    val trips by viewModel.trips.collectAsState()

    var selectedYear by remember { mutableStateOf("2026년") }
    var selectedMonth by remember { mutableStateOf("7월") }

    val totalDistance = trips.sumOf { it.distanceKm.ifNaN(0.0) }.ifZero(457.1)
    val drivingDays = trips.map { it.date.take(10) }.distinct().size.ifZeroInt(10)
    val chargeCount = 6
    val totalChargeKwh = 123.0
    val estimatedCost = 41820
    val co2Saved = 54.8
    val avgEfficiency = 6.08
    val avgDistPerDrive = if (trips.isNotEmpty()) totalDistance / trips.size else 17.6

    val topTimeTrips = trips.sortedByDescending { it.driveTimeMin }.take(5)
    val topDistTrips = trips.sortedByDescending { it.distanceKm }.take(5)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 월별 리포트 선택 패널
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardDark)
                    .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📊 월별 리포트 선택", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1E293B))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("◀ 지난달 보기", fontSize = 11.sp, color = TextGray)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("◀", fontSize = 12.sp, color = TextGray, modifier = Modifier.clickable { })
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2638))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedYear, fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E2638))
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(selectedMonth, fontSize = 12.sp, color = TextWhite, fontWeight = FontWeight.Bold)
                    }

                    Text("▶", fontSize = 12.sp, color = TextGray, modifier = Modifier.clickable { })
                }
            }
        }

        // 2. 6가지 핵심 통계 그리드
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportStatCard("총 주행거리", String.format(Locale.getDefault(), "%.1f km", totalDistance), AccentEmerald, Modifier.weight(1f))
                    ReportStatCard("주행한 날", "$drivingDays 일", Color(0xFF38BDF8), Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportStatCard("충전 횟수", "$chargeCount 회", AccentEmerald, Modifier.weight(1f))
                    ReportStatCard("총 충전량", String.format(Locale.getDefault(), "%.1f kWh", totalChargeKwh), AccentEmerald, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ReportStatCard("💳 추정 충전 비용", String.format(Locale.getDefault(), "%,d 원", estimatedCost), Color(0xFFA855F7), Modifier.weight(1f))
                    ReportStatCard("🌱 탄소 절감량", String.format(Locale.getDefault(), "%.1f kg CO2", co2Saved), AccentEmerald, Modifier.weight(1f))
                }
            }
        }

        // 3. 와이드 평균 전비 카드
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardDark)
                    .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("평균 전비", fontSize = 12.sp, color = TextGray)
                        Text(
                            String.format(Locale.getDefault(), "운행 1회당 평균 %.1f km", avgDistPerDrive),
                            fontSize = 11.sp,
                            color = SubText
                        )
                    }
                    Text(
                        String.format(Locale.getDefault(), "%.2f km/kWh", avgEfficiency),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF818CF8)
                    )
                }
            }
        }

        // 4. 이달의 최장 운행 시간 TOP 5
        item {
            Text("⏱️ 이달의 최장 운행 시간 TOP 5 (터치 시 주행 지도 이동)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        itemsIndexed(topTimeTrips.ifEmpty { sampleTrips() }) { index, trip ->
            RankedTripCard(
                rank = index + 1,
                dateStr = trip.date.take(10).replace("2026-", "").replace("-", ". ") + ".",
                start = trip.startAddress,
                end = trip.endAddress,
                metricPrimary = "${trip.driveTimeMin}분",
                metricSub = String.format(Locale.getDefault(), "%.1f km", trip.distanceKm),
                rankColor = AccentBlue
            )
        }

        // 5. 이달의 최장 운행 거리 TOP 5
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("📍 이달의 최장 운행 거리 TOP 5 (터치 시 주행 지도 이동)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        itemsIndexed(topDistTrips.ifEmpty { sampleTrips() }) { index, trip ->
            RankedTripCard(
                rank = index + 1,
                dateStr = trip.date.take(10).replace("2026-", "").replace("-", ". ") + ".",
                start = trip.startAddress,
                end = trip.endAddress,
                metricPrimary = String.format(Locale.getDefault(), "%.1f km", trip.distanceKm),
                metricSub = "${trip.driveTimeMin}분",
                rankColor = AccentEmerald
            )
        }
    }
}

@Composable
fun ReportStatCard(title: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Text(title, fontSize = 11.sp, color = TextGray)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun RankedTripCard(
    rank: Int,
    dateStr: String,
    start: String,
    end: String,
    metricPrimary: String,
    metricSub: String,
    rankColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Text("$rank", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = rankColor)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(dateStr, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Spacer(modifier = Modifier.height(2.dp))
                Text("$start ➔ $end", fontSize = 11.sp, color = TextGray)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(metricPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Text(metricSub, fontSize = 11.sp, color = TextGray)
        }
    }
}

private fun sampleTrips(): List<TripItem> = listOf(
    TripItem("1", "2026-07-17", "파주시 운정1동", "은평구 신사1동", 36.5, 118, 5.0),
    TripItem("2", "2026-07-12", "중구 광희동", "은평구 신사1동", 15.7, 113, 2.0),
    TripItem("3", "2026-07-07", "은평구 진관동", "은평구 신사1동", 8.4, 92, 1.5),
    TripItem("4", "2026-07-19", "은평구 신사1동", "중구 광희동", 13.3, 82, 2.1),
    TripItem("5", "2026-07-05", "서대문구 북가좌1동", "중구 광희동", 36.1, 67, 4.8)
)

private fun Double.ifNaN(default: Double): Double = if (this.isNaN()) default else this
private fun Double.ifZero(default: Double): Double = if (this == 0.0) default else this
private fun Int.ifZeroInt(default: Int): Int = if (this == 0) default else this
