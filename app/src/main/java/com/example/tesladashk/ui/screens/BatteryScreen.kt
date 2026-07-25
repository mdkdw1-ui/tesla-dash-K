package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun BatteryScreen(viewModel: DashboardViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12)),
        contentAlignment = Alignment.Center
    ) {
        Text("배터리 상세 현황 화면", color = Color.Gray, fontSize = 14.sp)
    }
}
