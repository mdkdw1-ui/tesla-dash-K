package com.example.tesladashk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun MonthlyReportScreen(viewModel: DashboardViewModel) {
    val trips by viewModel.trips.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("월간 리포트", color = Color.White)
        Text("주행 기록 수: ${trips.size}", color = Color.Gray)

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            items(trips) { trip ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161820))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("이동거리: ${trip.moveKM} km", color = Color.White)
                        Text("경로: ${trip.startDong} -> ${trip.endDong}", color = Color.Gray)
                    }
                }
            }
        }
    }
}
