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

@Composable
fun MonthlyReportScreen(viewModel: DashboardViewModel) {
    val trips by viewModel.trips.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        Text("📊 월간 주행 리포트", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (trips.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("주행 기록이 없습니다.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = trips) { trip ->
                    TripItemCard(trip = trip)
                }
            }
        }
    }
}

@Composable
fun TripItemCard(trip: DrivingTrip) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(trip.startTime.ifBlank { "주행 기록" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("${trip.moveKM} km", color = Color(0xFF3B82F6), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${trip.startDong} ➡️ ${trip.endDong}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}
