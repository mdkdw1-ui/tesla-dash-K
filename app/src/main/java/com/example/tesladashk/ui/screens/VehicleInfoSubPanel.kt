package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
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

fun Double?.toPsi(): String {
    if (this == null) return "-"
    val psi = if (this < 10.0) this * 14.5038 else this
    return "${psi.toInt()} psi"
}

@Composable
fun VehicleInfoSubPanel(viewModel: DashboardViewModel) {
    val vehicleRows by viewModel.vehicleRows.collectAsState()
    val trips by viewModel.trips.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()

    val latestState = vehicleRows.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🚘 차량 상태 정보", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("동기화: $lastSyncTime", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("배터리 잔량", color = Color.Gray, fontSize = 12.sp)
                        Text("${latestState?.batteryLevel ?: "-"}%", color = Color(0xFF22C55E), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("차량 상태", color = Color.Gray, fontSize = 12.sp)
                        Text(latestState?.state ?: "오프라인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                    Column {
                        Text("누적 주행거리", color = Color.Gray, fontSize = 12.sp)
                        Text("${latestState?.odometer?.toInt() ?: "-"} km", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))

                Text("🛞 타이어 공기압 (TPMS)", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("전좌: ${latestState?.tpmsFl.toPsi()}", color = Color.LightGray, fontSize = 13.sp)
                    Text("전우: ${latestState?.tpmsFr.toPsi()}", color = Color.LightGray, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("후좌: ${latestState?.tpmsRl.toPsi()}", color = Color.LightGray, fontSize = 13.sp)
                    Text("후우: ${latestState?.tpmsRr.toPsi()}", color = Color.LightGray, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("🚩 최근 주행 목록", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("주행 데이터가 존재하지 않습니다.", color = Color.Gray, fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = trips) { trip ->
                    RecentTripCard(trip = trip)
                }
            }
        }
    }
}

@Composable
fun RecentTripCard(trip: DrivingTrip) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(trip.startTime.ifBlank { "주행" }, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text("${trip.moveKM} km", color = Color(0xFF3B82F6), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${trip.startDong} ➡️ ${trip.endDong}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
