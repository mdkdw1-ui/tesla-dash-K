package com.mdkdw1.ui.tesla

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// HTML Tailwind 스타일 다크 모드 테마 디자인 시스템 (#0D0E12 배경, #161820 카드, #262936 테두리)
val DarkBg = Color(0xFF0D0E12)
val CardBg = Color(0xFF161820)
val BorderColor = Color(0xFF262936)
val TeslaRed = Color(0xFFE51937)
val TextPrimary = Color(0xFFEEEEEE)
val TextSecondary = Color(0xFF9CA3AF)

@Composable
fun TeslaDashboardApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val settingsManager = remember { EncryptedSettingsManager(context) }
    var appConfig by remember { mutableStateOf(settingsManager.getConfig()) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val repository = remember(appConfig) { TeslaRepository(appConfig) }

    var vehicleState by remember { mutableStateOf(VehicleState()) }
    var dailyTrip by remember { mutableStateOf(DailyTrip()) }
    var isLoading by remember { mutableStateOf(false) }

    val loadData = {
        scope.launch {
            isLoading = true
            repository.fetchVehicleState().getOrNull()?.let { vehicleState = it }
            repository.fetchLatestDailyTrip().getOrNull()?.let { dailyTrip = it }
            isLoading = false
        }
    }

    LaunchedEffect(appConfig) {
        loadData()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 상단 헤더 바
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = vehicleState.vehicleName,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Odometer: ${vehicleState.odometerKm} km",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { loadData() },
                        colors = ButtonDefaults.buttonColors(containerColor = CardBg)
                    ) {
                        Text(if (isLoading) "Loading..." else "Refresh", color = TextPrimary)
                    }

                    Button(
                        onClick = { showSettingsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = TeslaRed)
                    ) {
                        Text("Settings", color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 대시보드 주 요약 카드 목록
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    VehicleStatusCard(vehicleState)
                }
                item {
                    DailyTripCard(dailyTrip)
                }
            }
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentConfig = appConfig,
                onDismiss = { showSettingsDialog = false },
                onSave = { newConfig ->
                    settingsManager.saveConfig(newConfig)
                    appConfig = newConfig
                    showSettingsDialog = false
                }
            )
        }
    }
}

@Composable
fun VehicleStatusCard(state: VehicleState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Vehicle Status", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusItem("Battery", "${state.batteryLevel}%")
                StatusItem("Estimated Range", "${state.estimatedRangeKm} km")
                StatusItem("Gear", state.gear)
                StatusItem("Status", if (state.isLocked) "Locked" else "Unlocked")
            }
        }
    }
}

@Composable
fun DailyTripCard(trip: DailyTrip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Today's Driving", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatusItem("Distance", "${trip.distanceKm} km")
                StatusItem("Energy Used", "${trip.energyKwh} kWh")
                StatusItem("Efficiency", "${trip.efficiencyWhKm} Wh/km")
            }
        }
    }
}

@Composable
fun StatusItem(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SettingsDialog(
    currentConfig: AppConfig,
    onDismiss: () -> Unit,
    onSave: (AppConfig) -> Unit
) {
    var url by remember { mutableStateOf(currentConfig.supabaseUrl) }
    var key by remember { mutableStateOf(currentConfig.supabaseKey) }
    var mapKey by remember { mutableStateOf(currentConfig.kakaoMapKey) }
    var interval by remember { mutableStateOf(currentConfig.refreshIntervalSec.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App Settings (Encrypted)", color = TextPrimary) },
        containerColor = CardBg,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Supabase URL", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = BorderColor
                    )
                )

                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Supabase Key", color = TextSecondary) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = BorderColor
                    )
                )

                OutlinedTextField(
                    value = mapKey,
                    onValueChange = { mapKey = it },
                    label = { Text("Kakao Map Key", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = BorderColor
                    )
                )

                OutlinedTextField(
                    value = interval,
                    onValueChange = { interval = it },
                    label = { Text("Refresh Interval (sec)", color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = BorderColor
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newInterval = interval.toIntOrNull() ?: 30
                    onSave(AppConfig(url, key, mapKey, newInterval))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TeslaRed)
            ) {
                Text("Save", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
