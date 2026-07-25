package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun MonitorScreen(viewModel: TeslaViewModel) {
    var subTab by remember { mutableStateOf("vehicle") }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .background(Color(0xFF161820), shape = RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val tabs = listOf("vehicle" to "차량 정보", "driving" to "주행 지도", "monthly" to "월간 리포트", "battery" to "배터리")
            tabs.forEach { (key, label) ->
                TextButton(
                    onClick = { subTab = key },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (subTab == key) Color(0xFF2563EB) else Color.Transparent
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(label, color = if (subTab == key) Color.White else Color.Gray, fontSize = 12.sp)
                }
            }
        }

        when (subTab) {
            "vehicle" -> VehicleInfoSubPanel(viewModel)
            "driving" -> DrivingMapSubPanel(viewModel)
            "monthly" -> MonthlyReportSubPanel(viewModel)
            "battery" -> BatterySubPanel(viewModel)
        }
    }
}

@Composable fun VehicleInfoSubPanel(viewModel: TeslaViewModel) { Text("차량 정보 패널", color = Color.White, modifier = Modifier.padding(16.dp)) }
@Composable fun DrivingMapSubPanel(viewModel: TeslaViewModel) { Text("주행 지도 패널", color = Color.White, modifier = Modifier.padding(16.dp)) }
@Composable fun MonthlyReportSubPanel(viewModel: TeslaViewModel) { Text("월간 리포트 패널", color = Color.White, modifier = Modifier.padding(16.dp)) }
@Composable fun BatterySubPanel(viewModel: TeslaViewModel) { Text("배터리 패널", color = Color.White, modifier = Modifier.padding(16.dp)) }
