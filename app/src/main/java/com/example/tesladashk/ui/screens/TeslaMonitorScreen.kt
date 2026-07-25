package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun TeslaMonitorScreen(viewModel: DashboardViewModel) {
    var selectedSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("차량 정보", "주행 지도", "월간 리포트", "배터리")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
    ) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color(0xFF13151C),
            contentColor = Color.White,
            divider = {}
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            color = if (selectedSubTab == index) Color(0xFF3B82F6) else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedSubTab) {
                0 -> VehicleInfoSubPanel(viewModel = viewModel)
                1 -> DrivingMapScreen(viewModel = viewModel)
                2 -> MonthlyReportScreen(viewModel = viewModel)
                3 -> BatteryScreen(viewModel = viewModel)
            }
        }
    }
}
