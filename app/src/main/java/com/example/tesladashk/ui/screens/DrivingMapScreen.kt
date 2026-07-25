package com.example.tesladashk.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tesladashk.network.TripItem
import com.example.tesladashk.viewmodel.TeslaViewModel
import java.util.Locale

private val CardBg = Color(0xFF131B2E)
private val CardBorder = Color(0xFF1E293B)
private val DarkBg = Color(0xFF0B0F17)
private val PrimaryText = Color(0xFFF1F5F9)
private val SubText = Color(0xFF94A3B8)
private val AccentBlue = Color(0xFF3B82F6)
private val AccentGreen = Color(0xFF10B981)
private val AccentOrange = Color(0xFFF59E0B)
private val AccentPurple = Color(0xFFA855F7)

@Composable
fun DrivingMapScreen(viewModel: TeslaViewModel) {
    val trips by viewModel.trips.collectAsState()
    val config by viewModel.config.collectAsState()

    var selectedFilter by remember { mutableStateOf("일간") }
    val filters = listOf("단일운행", "일간", "주간", "월간", "분기", "반년", "년")

    val latestDate = trips.firstOrNull()?.date?.take(10) ?: "2026-07-22"
    val dayTrips = trips.filter { it.date.startsWith(latestDate) }

    val totalDistance = dayTrips.sumOf { it.distanceKm.ifNaN(0.0) }
    val totalBattery = dayTrips.sumOf { it.batteryUsed.ifNaN(0.0) }
    val avgEfficiency = if (totalBattery > 0) totalDistance / (totalBattery * 0.6) else 4.78

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 조회 기간 조건 필터
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("📅 조회 기간 조건", fontSize = 13.sp, color = PrimaryText, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    filters.forEach { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AccentBlue else Color(0xFF1E293B))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                filter,
                                fontSize = 11.sp,
                                color = if (isSelected) Color.White else SubText,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // 날짜 넘기기
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E2638))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("◀", fontSize = 12.sp, color = SubText, modifier = Modifier.clickable { })
                    Text(latestDate, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text("▶", fontSize = 12.sp, color = SubText, modifier = Modifier.clickable { })
                }
            }
        }

        // 2. 통계 요약 4칸
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SummaryMetric("운행건수", "${dayTrips.size} 건", AccentBlue)
                    SummaryMetric("이동거리", String.format(Locale.getDefault(), "%.1f km", totalDistance), AccentGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SummaryMetric("소모 배터리/전력", String.format(Locale.getDefault(), "-%.1f%% (%.1fkWh)", totalBattery, totalBattery * 0.6), AccentOrange)
                    SummaryMetric("평균 전비", String.format(Locale.getDefault(), "%.2f km/kWh", avgEfficiency), AccentPurple)
                }
            }
        }

        // 3. 카카오맵 지도 웹뷰 영역
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            ) {
                KakaoMapView(kakaoKey = config.kakaoKey)
            }
        }

        // 4. 단일 운행 상세 목록 헤더
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🚘 $latestDate 단일 운행 상세 목록", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                Text("${dayTrips.size}건", fontSize = 12.sp, color = SubText)
            }
        }

        // 5. 상세 운행 카드리스트
        items(dayTrips) { trip ->
            TripDetailCard(trip)
        }
    }
}

@Composable
fun SummaryMetric(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(label, fontSize = 11.sp, color = SubText)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun TripDetailCard(trip: TripItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#${trip.id} 운행 (${trip.timeStr.takeLast(5)} 출발)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentBlue)
            Text("${trip.distanceKm} km / ${trip.driveTimeMin}분", fontSize = 12.sp, color = PrimaryText)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            AddressChip(trip.startAddress.ifEmpty { "출발지" })
            Text("  ➔  ", color = SubText, fontSize = 12.sp)
            AddressChip(trip.endAddress.ifEmpty { "도착지" })
        }
    }
}

@Composable
fun AddressChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF1E2638))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, color = Color(0xFF60A5FA))
    }
}

@Composable
fun KakaoMapView(kakaoKey: String) {
    val keyToUse = kakaoKey.ifBlank { "1f2b3c4d5e6f7g8h9i0j" }
    val html = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=$keyToUse"></script>
            <style>
                html, body { width:100%; height:100%; margin:0; padding:0; background-color:#0B0F17; }
                #map { width:100%; height:100%; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var container = document.getElementById('map');
                var options = {
                    center: new kakao.maps.LatLng(37.5665, 126.9780),
                    level: 7
                };
                var map = new kakao.maps.Map(container, options);
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://dapi.kakao.com", html, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

private fun Double.ifNaN(default: Double): Double = if (this.isNaN()) default else this
