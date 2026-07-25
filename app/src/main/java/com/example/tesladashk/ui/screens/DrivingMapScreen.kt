package com.example.tesladashk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tesladashk.network.DrivingTrip
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun DrivingMapScreen(viewModel: DashboardViewModel) {
    val trips by viewModel.trips.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("주행 지도 화면", color = Color.White)
        Text("총 주행 건수: ${trips.size}", color = Color.Gray)
    }
}
