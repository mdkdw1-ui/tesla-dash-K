package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun BatteryScreen(viewModel: TeslaViewModel) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0B0F17)),
        contentAlignment = Alignment.Center
    ) {
        Text("🔋 배터리 상세 분석 기능 준비 중", color = Color(0xFFF1F5F9), fontSize = 16.sp)
    }
}
