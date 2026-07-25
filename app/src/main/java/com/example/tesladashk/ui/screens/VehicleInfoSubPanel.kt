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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.network.DrivingTrip
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun VehicleInfoSubPanel(viewModel: DashboardViewModel) {
    val rows by viewModel.vehicleRows.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val lastSync by viewModel.lastSyncTime.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val latest = rows.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 0. 최상단 타이틀 & 싱크 버튼
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("최근 차량 상태", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(if (lastSync.isNotEmpty()) "$lastSync 기준" else "실시간", color = Color.Gray, fontSize = 12.sp)
                    IconButton(
                        onClick = { viewModel.triggerSyncAndFetch() },
                        modifier = Modifier.size(28.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text("🔄", fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // 1. 헤더 (온라인 상태)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B132B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF8B5CF6), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("● 현재: 온라인", color = Color(0xFFA855F7), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("🔋 ${latest?.batteryLevel ?: 0}%", color = Color.White, fontSize = 13.sp)
                        Text("📍 ${latest?.odometer?.toInt() ?: 0} km", color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }

        // 2. 최근 전비 카드
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("⚡ 최근 전비", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = Color(0xFF2D264A), shape = RoundedCornerShape(4.dp)) {
                                Text("실축 용량", color = Color(0xFFA855F7), fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text("주행 6건", color = Color.Gray, fontSize = 11.sp)
                    }
                    Text("5.94 km/kWh", color = Color(0xFF818CF8), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

                    Divider(color = Color(0xFF2D3748), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 2.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetricItem("주행거리", "74.0 km")
                        MetricItem("사용 배터리", "20.1%")
                        MetricItem("배터리당", "3.69 km/%")
                        MetricItem("사용 에너지", "12.5 kWh")
                    }
                    Text("실축 용량  62.1 kWh", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }

        // 3. 주차 카드
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181510)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFD97706), RoundedCornerShape(12.dp))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = Color(0xFF0284C7), shape = RoundedCornerShape(4.dp)) {
                        Text("P", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Column {
                        Text("주차 1시간 26분", color = Color(0xFFF59E0B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("7월 25일 17:24 부터", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }
        }

        // 4. 타이어 공기압
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("⚙️ 타이어 공기압", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("7월 25일 17:24  🌡️ ${latest?.outsideTemp ?: 0.0}°C", color = Color.Gray, fontSize = 10.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TireBox("앞 왼쪽", viewModel.toPsi(latest?.tpmsFl), modifier = Modifier.weight(1f))
                        TireBox("앞 오른쪽", viewModel.toPsi(latest?.tpmsFr), modifier = Modifier.weight(1f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TireBox("뒤 왼쪽", viewModel.toPsi(latest?.tpmsRl), modifier = Modifier.weight(1f))
                        TireBox("뒤 오른쪽", viewModel.toPsi(latest?.tpmsRr), modifier = Modifier.weight(1f))
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally), modifier = Modifier.fillMaxWidth().padding(top = 2.dp)) {
                        Text("● 정상", color = Color(0xFF10B981), fontSize = 10.sp)
                        Text("● 주의", color = Color(0xFFF59E0B), fontSize = 10.sp)
                        Text("● 위험", color = Color(0xFFEF4444), fontSize = 10.sp)
                    }
                }
            }
        }

        // 5. 날짜 구분 및 로그 타임라인
        item {
            Text("7월 25일", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        }

        items(trips) { trip ->
            LogCardItem(trip)
        }
    }
}

@Composable
fun MetricItem(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 10.sp)
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TireBox(label: String, psi: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF13151C), shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFD97706), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.Gray, fontSize = 10.sp)
            Text("$psi psi", color = Color(0xFFFBBF24), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun LogCardItem(trip: DrivingTrip) {
    val (bgColor, labelText) = when (trip.stateType) {
        "주행" -> Color(0xFF0284C7) to "주행"
        "감시" -> Color(0xFFD97706) to "감시"
        else -> Color(0xFF7C3AED) to "온라인"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161820)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = bgColor, shape = RoundedCornerShape(6.dp)) {
                    Text(labelText, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Text("${trip.startTime} ~ ${trip.endTime} (${trip.durationText})", color = Color.White, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔋 ${trip.startBattery}%", color = Color.White, fontSize = 11.sp)
                Text("▼ ${trip.batteryUsedPercent}%", color = Color(0xFFEF4444), fontSize = 11.sp)
                Text("📍 ${trip.endOdometer.toInt()} km", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
