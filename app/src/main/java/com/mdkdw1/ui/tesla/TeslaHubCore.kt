package com.mdkdw1.ui.tesla

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TeslaHubCoreScreen(
    viewModel: TeslaViewModel
) {
    val vehicleData by viewModel.vehicleData.collectAsState()
    val batteryDegradation by viewModel.batteryDegradation.collectAsState()
    val chargeRecords by viewModel.chargeRecords.collectAsState()
    val consumableItems by viewModel.consumableItems.collectAsState()
    val dailyTrips by viewModel.dailyTrips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchConsumableItems()
        viewModel.fetchDailyTrips()
    }

    Scaffold(
        containerColor = DarkBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeaderSection(
                    vehicleData = vehicleData,
                    isLoading = isLoading,
                    onRefresh = { viewModel.refreshAllData() }
                )
            }

            item {
                QuickControlSection(
                    vehicleData = vehicleData,
                    onCommand = { cmd -> viewModel.sendVehicleCommand(cmd) }
                )
            }

            item {
                BatteryOverviewSection(
                    vehicleData = vehicleData,
                    batteryDegradationList = batteryDegradation
                )
            }

            item {
                ChargingHistorySection(
                    chargeRecords = chargeRecords
                )
            }

            item {
                ConsumablesSection(
                    consumableItems = consumableItems
                )
            }

            item {
                DailyTripsSection(
                    dailyTrips = dailyTrips
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    vehicleData: VehicleData,
    isLoading: Boolean,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = vehicleData.name,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(
                    statusText = vehicleData.state.uppercase(),
                    isActive = vehicleData.state.lowercase() == "online"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "마지막 업데이트: ${vehicleData.lastUpdated}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Button(
            onClick = onRefresh,
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkCardBg,
                contentColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Text(if (isLoading) "동기화 중..." else "새로고침")
        }
    }
}

@Composable
fun QuickControlSection(
    vehicleData: VehicleData,
    onCommand: (String) -> Unit
) {
    Column {
        Text(
            text = "빠른 제어",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlTile(
                title = if (vehicleData.locked) "잠금 해제" else "차량 잠금",
                subtitle = if (vehicleData.locked) "잠김" else "열림",
                active = vehicleData.locked,
                modifier = Modifier.weight(1f),
                onClick = { onCommand("toggle_lock") }
            )
            ControlTile(
                title = "공조 장치",
                subtitle = if (vehicleData.climateOn) "켜짐 (${vehicleData.insideTemp}°C)" else "꺼짐",
                active = vehicleData.climateOn,
                modifier = Modifier.weight(1f),
                onClick = { onCommand("toggle_climate") }
            )
            ControlTile(
                title = "감시 모드",
                subtitle = if (vehicleData.sentryMode) "활성화" else "비활성화",
                active = vehicleData.sentryMode,
                modifier = Modifier.weight(1f),
                onClick = { onCommand("toggle_sentry") }
            )
        }
    }
}

@Composable
fun ControlTile(
    title: String,
    subtitle: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) AccentBlue.copy(alpha = 0.15f) else DarkCardBg)
            .border(1.dp, if (active) AccentBlue else DarkBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                color = if (active) AccentBlue else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun BatteryOverviewSection(
    vehicleData: VehicleData,
    batteryDegradationList: List<BatteryDegradation>
) {
    val latestDegradation = batteryDegradationList.firstOrNull()?.degradationPercent ?: 2.4f

    Column {
        Text(
            text = "배터리 & 열화율 상태",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "배터리 잔량",
                value = "${vehicleData.batteryLevel}%",
                subValue = "주행 가능 거리: ${vehicleData.rangeKm} km",
                modifier = Modifier.weight(1f),
                valueColor = AccentGreen
            )
            MetricCard(
                title = "배터리 열화율",
                value = "${latestDegradation}%",
                subValue = "최대 완충: ${vehicleData.maxRangeKm} km",
                modifier = Modifier.weight(1f),
                valueColor = AccentRed
            )
        }
    }
}

@Composable
fun ChargingHistorySection(
    chargeRecords: List<ChargeRecord>
) {
    Column {
        Text(
            text = "최근 충전 기록",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (chargeRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardBg)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "등록된 충전 기록이 없습니다.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                chargeRecords.take(3).forEach { record ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCardBg)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = record.location,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${record.date} • ${if (record.fastCharge) "급속" else "완속"}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "+${record.energyAddedKwh} kWh",
                                color = AccentGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "₩${record.costKrw}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsumablesSection(
    consumableItems: List<ConsumableItem>
) {
    Column {
        Text(
            text = "소모품 관리",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(consumableItems) { item ->
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkCardBg)
                        .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Text(
                            text = item.name,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StatusBadge(
                            statusText = item.status,
                            isActive = item.status == "양호"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { item.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (item.progressPercent > 80f) AccentRed else AccentBlue,
                            trackColor = DarkBorder
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${item.lastChangedKm}km / ${item.intervalKm}km",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyTripsSection(
    dailyTrips: List<DailyTrip>
) {
    Column {
        Text(
            text = "일별 주행 데이터",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            dailyTrips.take(3).forEach { trip ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCardBg)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = trip.date,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${trip.startLocation} → ${trip.endLocation}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${trip.distanceKm} km",
                            color = AccentBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${trip.efficiencyWhKm} Wh/km",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
