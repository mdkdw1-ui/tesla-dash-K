package com.mdkdw1.ui.tesla

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// 다크 모드 디자인 색상 시스템
val DarkBg = Color(0xFF0D0E12)
val DarkCardBg = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val PrimaryAccent = Color(0xFF3B82F6)
val TextPrimary = Color(0xFFEEEEEE)
val TextSecondary = Color(0xFF9CA3AF)

@Composable
fun TeslaHubScreen() {
    val context = LocalContext.current
    val repository = remember { TeslaHubRepository(context) }
    val settingsManager = remember { SecureSettingsManager(context) }
    val scope = rememberCoroutineScope()

    var currentTab by remember { mutableIntStateOf(0) }
    var vehicleState by remember { mutableStateOf<VehicleStateData?>(null) }
    var dailyTrip by remember { mutableStateOf<DailyTrip?>(null) }
    var batteryRecords by remember { mutableStateOf<List<BatteryRecord>>(emptyList()) }
    var chargeRecords by remember { mutableStateOf<List<ChargeRecord>>(emptyList()) }
    var consumables by remember { mutableStateOf<List<ConsumableItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            vehicleState = repository.fetchVehicleState()
            dailyTrip = repository.fetchLatestDailyTrip()
            batteryRecords = repository.fetchBatteryDegradation()
            chargeRecords = repository.fetchChargeRecords()
            consumables = repository.fetchConsumables()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // 상단 헤더
        Text(
            text = "TESLA DASHBOARD",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 탭 네비게이션
        TabRow(
            selectedTabIndex = currentTab,
            containerColor = DarkCardBg,
            contentColor = PrimaryAccent,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
        ) {
            val tabs = listOf("대시보드", "배터리열화", "충전기록", "소모품", "설정")
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index },
                    text = {
                        Text(
                            text = title,
                            color = if (currentTab == index) PrimaryAccent else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 탭 콘텐츠
        Box(modifier = Modifier.weight(1f)) {
            when (currentTab) {
                0 -> DashboardTabContent(vehicleState = vehicleState, dailyTrip = dailyTrip)
                1 -> BatteryTabContent(batteryRecords = batteryRecords)
                2 -> ChargeRecordsTabContent(chargeRecords = chargeRecords)
                3 -> ConsumablesTabContent(consumables = consumables)
                4 -> SettingsTabContent(settingsManager = settingsManager)
            }
        }
    }
}

@Composable
fun DashboardTabContent(vehicleState: VehicleStateData?, dailyTrip: DailyTrip?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 차량 기본 정보 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = vehicleState?.vehicleName ?: "Tesla Vehicle",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "배터리 잔량", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "${vehicleState?.batteryLevel ?: 0}%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryAccent
                        )
                    }
                    Column {
                        Text(text = "주행 가능 거리", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = "${vehicleState?.batteryRangeKm ?: 0.0} km",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Column {
                        Text(text = "잠금 상태", fontSize = 12.sp, color = TextSecondary)
                        Text(
                            text = if (vehicleState?.isLocked == true) "잠김" else "열림",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // 주행 및 온도 상태
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "차량 상태",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "누적 주행거리:", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        text = "${vehicleState?.odometerKm ?: 0.0} km",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "실내/실외 온도:", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        text = "${vehicleState?.insideTemp ?: 0.0}°C / ${vehicleState?.outsideTemp ?: 0.0}°C",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // 최근 주행 트립
        dailyTrip?.let { trip ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "최근 일일 트립 (${trip.date})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "주행 거리: ${trip.distanceKm} km", color = TextSecondary, fontSize = 14.sp)
                        Text(text = "전비: ${trip.efficiencyWhPerKm} Wh/km", color = PrimaryAccent, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryTabContent(batteryRecords: List<BatteryRecord>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "배터리 열화율 및 용량 이력",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(batteryRecords) { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = record.date, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(text = "용량: ${record.capacityKwh} kWh", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text(
                        text = "건강도: ${record.healthPercentage}%",
                        fontSize = 14.sp,
                        color = PrimaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ChargeRecordsTabContent(chargeRecords: List<ChargeRecord>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "충전 이력 목록",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(chargeRecords) { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = record.location, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(text = record.date, fontSize = 12.sp, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "충전량: ${record.addedKwh} kWh", fontSize = 13.sp, color = TextSecondary)
                        Text(text = "비용: ${record.cost} 원", fontSize = 13.sp, color = PrimaryAccent)
                    }
                }
            }
        }
    }
}

@Composable
fun ConsumablesTabContent(consumables: List<ConsumableItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "소모품 교체 주기 관리",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(consumables) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = item.name, fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text(
                            text = "마지막 교체: ${item.lastReplacedKm} km",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        text = "주기: ${item.cycleKm} km",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsTabContent(settingsManager: SecureSettingsManager) {
    val initialConfig = remember { settingsManager.getConfig() }

    var supabaseUrl by remember { mutableStateOf(initialConfig.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(initialConfig.supabaseKey) }
    var kakaoMapKey by remember { mutableStateOf(initialConfig.kakaoMapKey) }
    var vehicleId by remember { mutableStateOf(initialConfig.vehicleId) }

    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "암호화 설정 관리",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        OutlinedTextField(
            value = supabaseUrl,
            onValueChange = { supabaseUrl = it },
            label = { Text("Supabase URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = DarkBorder,
                focusedLabelColor = PrimaryAccent,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        OutlinedTextField(
            value = supabaseKey,
            onValueChange = { supabaseKey = it },
            label = { Text("Supabase Key") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = DarkBorder,
                focusedLabelColor = PrimaryAccent,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        OutlinedTextField(
            value = kakaoMapKey,
            onValueChange = { kakaoMapKey = it },
            label = { Text("KakaoMap API Key") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = DarkBorder,
                focusedLabelColor = PrimaryAccent,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        OutlinedTextField(
            value = vehicleId,
            onValueChange = { vehicleId = it },
            label = { Text("Vehicle ID") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = DarkBorder,
                focusedLabelColor = PrimaryAccent,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )

        Button(
            onClick = {
                val newConfig = AppConfig(
                    supabaseUrl = supabaseUrl,
                    supabaseKey = supabaseKey,
                    kakaoMapKey = kakaoMapKey,
                    vehicleId = vehicleId
                )
                settingsManager.saveConfig(newConfig)
                isSaved = true
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
        ) {
            Text(text = "설정 보안 저장 (AES-256)", color = Color.White)
        }

        if (isSaved) {
            Text(
                text = "설정이 성공적으로 저장되었습니다.",
                color = Color(0xFF10B981),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
