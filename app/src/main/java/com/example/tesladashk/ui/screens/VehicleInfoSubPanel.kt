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
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun VehicleInfoSubPanel(viewModel: DashboardViewModel) {
    val rows by viewModel.vehicleRows.collectAsState()
    val latest = rows.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 1. 헤더 (상태 요약)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B132B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(12.dp))
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("⚡ 최근 전비  실축 용량", color = Color(0xFF8B5CF6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("주행 6건", color = Color.Gray, fontSize = 11.sp)
                    }
                    Text("5.94 km/kWh", color = Color(0xFF818CF8), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)

                    Divider(color = Color(0xFF2D3748), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetricItem("주행거리", "74.0 km")
                        MetricItem("사용 배터리", "20.1%")
                        MetricItem("배터리당", "3.69 km/%")
                        MetricItem("사용 에너지", "12.5 kWh")
                    }
                }
            }
        }

        // 3. 주차 카드
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF181510)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFB45309), RoundedCornerShape(12.dp))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🅿️ ", fontSize = 18.sp)
                    Column {
                        Text("주차 1시간 26분", color = Color(0xFFF59E0B), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("7월 25일 17:24 부터", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }
        }

        // 4. 타이어 공기압 2x2
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
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
                }
            }
        }

        // 5. 최근 이력 로그
        items(rows) { row ->
            LogCardItem(row)
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
            .background(Color(0xFF1E2029), shape = RoundedCornerShape(8.dp))
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
fun LogCardItem(row: com.example.tesladashk.network.VehicleRow) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFF0284C7),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("주행", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Text("16:27 ~ 17:24 (56분)", color = Color.White, fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔋 ${row.batteryLevel ?: 0}%", color = Color.White, fontSize = 11.sp)
                Text("📍 ${row.odometer?.toInt() ?: 0}km", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}
