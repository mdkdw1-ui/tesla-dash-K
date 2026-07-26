package com.mdkdw1.ui.tesla

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val vehicleState = viewModel.vehicleState
    val settings = viewModel.settings
    val scrollState = rememberScrollState()

    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tesla Command Hub", color = TextPrimary, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkCard)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 상태 요약 카드
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("차량 상태 대시보드", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatusItem("배터리", "${vehicleState.batteryLevel}%", "${vehicleState.range} km")
                        StatusItem("주행거리", "${vehicleState.odometer} km", "총 주행")
                        StatusItem("실내 온도", "${vehicleState.cabinTemp}°C", "공조 ${if (vehicleState.climateOn) "켜짐" else "꺼짐"}")
                    }
                }
            }

            // 제어 버튼 패널
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("원격 제어", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.toggleLock() },
                            colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.isLocked) AccentBlue else Color.DarkGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (vehicleState.isLocked) "잠김 해제" else "잠금")
                        }
                        Button(
                            onClick = { viewModel.toggleClimate() },
                            colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.climateOn) AccentAmber else Color.DarkGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (vehicleState.climateOn) "공조 끄기" else "공조 켜기")
                        }
                        Button(
                            onClick = { viewModel.toggleCharging() },
                            colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.isCharging) Color.Red else Color.DarkGray),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (vehicleState.isCharging) "충전 중지" else "충전 시작")
                        }
                    }
                }
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentSettings = settings,
            onDismiss = { showSettingsDialog = false },
            onSave = { newSettings ->
                viewModel.saveSettings(newSettings)
                showSettingsDialog = false
            }
        )
    }
}

@Composable
fun StatusItem(title: String, value: String, subtext: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(subtext, color = Color.LightGray, fontSize = 10.sp)
    }
}

@Composable
fun SettingsDialog(
    currentSettings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit
) {
    var supabaseUrl by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoMapKey by remember { mutableStateOf(currentSettings.kakaoMapKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("설정 (AES-256 암호화 저장)", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = kakaoMapKey,
                    onValueChange = { kakaoMapKey = it },
                    label = { Text("Kakao Map Key") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(AppSettings(supabaseUrl = supabaseUrl, supabaseKey = supabaseKey, kakaoMapKey = kakaoMapKey))
            }) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.Gray)
            }
        }
    )
}
