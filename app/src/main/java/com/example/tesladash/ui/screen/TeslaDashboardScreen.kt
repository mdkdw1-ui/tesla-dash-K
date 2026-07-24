package com.example.tesladash.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladash.data.TeslaDataProcessor
import com.example.tesladash.model.Trip
import com.example.tesladash.model.VehicleLog
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.chart.line.rememberLineSpec
import com.patrykandpatrick.vico.compose.chart.rememberCartesianChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.model.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.model.lineSeries
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// 웹 대시보드 다크 테마 컬러 매핑
val BgDark = Color(0xFF090A0F)
val CardDark = Color(0xFF0F1015)
val BorderDark = Color(0xFF1F2937)
val AccentGreen = Color(0xFF34D399)
val AccentIndigo = Color(0xFF818CF8)

@Composable
fun TeslaDashboardScreen(
    trips: List<Trip>,
    vehicleLogs: List<VehicleLog>,
    isLoading: Boolean = false
) {
    val context = LocalContext.current
    val top5Trips = remember(trips) { TeslaDataProcessor.getMonthlyTop5(trips) }
    val batteryMetrics = remember(vehicleLogs) { TeslaDataProcessor.processBatteryMetrics(vehicleLogs) }

    val latestMetric = batteryMetrics.lastOrNull()
    val latestDegradation = latestMetric?.degradation ?: 100.0
    val latestFullRange = latestMetric?.fullRangeKm ?: 430

    // Vico 차트 데이터 모델 프로듀서
    val degModelProducer = remember { CartesianChartModelProducer() }
    val rangeModelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(batteryMetrics) {
        if (batteryMetrics.isNotEmpty()) {
            degModelProducer.runTransaction {
                lineSeries { series(batteryMetrics.map { it.degradation }) }
            }
            rangeModelProducer.runTransaction {
                lineSeries { series(batteryMetrics.map { it.fullRangeKm }) }
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentGreen)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. 배터리 요약 카드 ---
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "배터리 열화율",
                        value = "$latestDegradation%",
                        valueColor = AccentIndigo
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        title = "100% 환산 거리",
                        value = "$latestFullRange km",
                        valueColor = AccentGreen
                    )
                }
            }

            // --- 2. Vico 배터리 열화율 차트 ---
            item {
                VicoChartCard(
                    title = "배터리 열화율 추이 (%)",
                    modelProducer = degModelProducer,
                    lineColor = AccentIndigo
                )
            }

            // --- 3. Vico 100% 환산 주행거리 차트 ---
            item {
                VicoChartCard(
                    title = "100% 환산 주행거리 추이 (km)",
                    modelProducer = rangeModelProducer,
                    lineColor = AccentGreen
                )
            }

            // --- 4. 이달의 TOP 5 목록 ---
            item {
                Text(
                    text = "이달의 최장 운행 TOP 5",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            if (top5Trips.isEmpty()) {
                item {
                    Text("기록 없음", color = Color.Gray, fontSize = 12.sp)
                }
            } else {
                itemsIndexed(top5Trips) { index, trip ->
                    TopTripCard(
                        rank = index + 1,
                        trip = trip,
                        onClick = { dateStr ->
                            Toast.makeText(context, "$dateStr 운행 기록 선택됨", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

// Vico 라이브러리 기반 곡선 & 영역 그라데이션 차트
@Composable
fun VicoChartCard(
    title: String,
    modelProducer: CartesianChartModelProducer,
    lineColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Chart(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lines = listOf(
                        rememberLineSpec(
                            shader = DynamicShaders.color(lineColor),
                            backgroundShader = DynamicShaders.fromBrush(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                        )
                    ),
                    pointSpacing = 24.dp
                ),
                startAxis = rememberStartAxis(),
                bottomAxis = rememberBottomAxis()
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
    }
}

// 상단 요약 정보 카드
@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    valueColor: Color
) {
    Column(
        modifier = modifier
            .background(CardDark, RoundedCornerShape(16.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(title, color = Color.Gray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

// TOP 5 리스트 항목 카드
@Composable
fun TopTripCard(
    rank: Int,
    trip: Trip,
    onClick: (String) -> Unit
) {
    val dateParam = remember(trip.timestamp) {
        try { trip.timestamp.split("T")[0] } catch (e: Exception) { "" }
    }
    val dateStr = remember(trip.timestamp) {
        try {
            val parsed = ZonedDateTime.parse(trip.timestamp)
            parsed.format(DateTimeFormatter.ofPattern("M/d"))
        } catch (e: Exception) { "" }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(12.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .clickable { onClick(dateParam) }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(AccentGreen.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$rank", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text(dateStr, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = String.format(Locale.US, "%.1f km", trip.moveKM),
                color = AccentGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${trip.startDong ?: "-"} → ${trip.endDong ?: "-"}", color = Color.Gray, fontSize = 12.sp)
            Text("${trip.durationMin}분", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
