package com.mdkdw1.ui.tesla

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

// 테슬라 메인 디자인 시스템 색상
private val DarkBackground = Color(0xFF0D0E12)
private val DarkCardBg = Color(0xFF161820)
private val DarkBorder = Color(0xFF262936)
private val TeslaRed = Color(0xFFE82127)
private val TeslaGreen = Color(0xFF10B981)
private val TeslaBlue = Color(0xFF3B82F6)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF9CA3AF)

@Composable
fun TeslaHubCore(
    repository: TeslaRepository,
    settingsManager: EncryptedSettingsManager,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // State Delegate 정상 동작을 위한 getValue/setValue 임포트 완료
    var vehicleState by remember { mutableStateOf<VehicleState?>(null) }
    var batteryRecords by remember { mutableStateOf<List<BatteryDegradation>>(emptyList()) }
    var chargeRecords by remember { mutableStateOf<List<ChargeRecord>>(emptyList()) }
    var consumableItems by remember { mutableStateOf<List<ConsumableItem>>(emptyList()) }
    var dailyTrips by remember { mutableStateOf<List<DailyTrip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // 데이터 동기화 함수
    fun refreshData() {
        scope.launch {
            isLoading = true
            try {
                vehicleState = repository.fetchVehicleState()
                batteryRecords = repository.fetchBatteryDegradation()
                chargeRecords = repository.fetchChargeRecords()
                consumableItems = repository.fetchConsumableItems()
                dailyTrips = repository.fetchDailyTrips()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    // 22번 줄 / 27번 줄 에러 해결 영역: List 타입 추론 정상화로 getOrNull 및 it 참조 안전 동작
    val latestBattery = batteryRecords.getOrNull(0)
    val latestBatteryText = latestBattery?.let {
        "열화율: ${it.degradationPercent}% (${it.maxRangeKm} km)"
    } ?: "배터리 데이터 없음"

    val latestCharge = chargeRecords.getOrNull(0)
    val latestChargeText = latestCharge?.let {
        "최근 충전: ${it.addedKwh} kWh (${it.cost}원)"
    } ?: "충전 기록 없음"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 상단 헤더
            TopHeaderBar(
                vehicleName = vehicleState?.vehicleName ?: "Tesla Model Y",
                isOnline = vehicleState?.state == "online",
                isLoading = isLoading,
                onRefresh = { refreshData() },
                onOpenSettings = { showSettingsDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 탭 네비게이션
            TabNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 탭 컨텐츠
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> DashboardView(
                        vehicleState = vehicleState,
                        latestBatteryText = latestBatteryText,
                        latestChargeText = latestChargeText,
                        onControlClick = { action ->
                            scope.launch {
                                repository.sendVehicleCommand(action)
                                refreshData()
                            }
                        }
                    )
                    1 -> BatteryAnalysisView(batteryRecords = batteryRecords)
                    2 -> ChargingHistoryView(chargeRecords = chargeRecords)
                    3 -> MaintenanceView(consumables = consumableItems)
                    4 -> TripLogView(trips = dailyTrips)
                }
            }
        }

        // 설정 다이얼로그 (Supabase URL, API Key, 카카오맵 Key 암호화 저장)
        if (showSettingsDialog) {
            SettingsDialog(
                settingsManager = settingsManager,
                onDismiss = { showSettingsDialog = false },
                onSave = {
                    showSettingsDialog = false
                    refreshData()
                }
            )
        }
    }
}

@Composable
private fun TopHeaderBar(
    vehicleName: String,
    isOnline: Boolean,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = vehicleName,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) TeslaGreen else Color.Gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isOnline) "온라인" else "절전 모드",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardBg)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = TeslaRed,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "새로고침",
                        tint = TextPrimary
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardBg)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun TabNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("대시보드", "배터리 분석", "충전 내역", "소모품 관리", "주행 기록")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCardBg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) TeslaRed else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isSelected) TextPrimary else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun DashboardView(
    vehicleState: VehicleState?,
    latestBatteryText: String,
    latestChargeText: String,
    onControlClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 배터리 상태 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCardBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("배터리 잔량", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        "${vehicleState?.batteryLevel ?: 0}%",
                        color = TeslaGreen,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { ((vehicleState?.batteryLevel ?: 0) / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = TeslaGreen,
                    trackColor = DarkBorder,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(latestBatteryText, color = TextPrimary, fontSize = 13.sp)
                    Text(latestChargeText, color = TextSecondary, fontSize = 13.sp)
                }
            }
        }

        // 제어 기능 그리드
        Text("빠른 제어", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlCard(
                modifier = Modifier.weight(1f),
                title = if (vehicleState?.isLocked == true) "잠금 해제" else "차량 잠금",
                icon = if (vehicleState?.isLocked == true) Icons.Default.Lock else Icons.Default.LockOpen,
                isActive = vehicleState?.isLocked == true,
                onClick = { onControlClick("lock_toggle") }
            )
            ControlCard(
                modifier = Modifier.weight(1f),
                title = if (vehicleState?.climateOn == true) "공조 끄기" else "공조 켜기",
                icon = Icons.Default.AcUnit,
                isActive = vehicleState?.climateOn == true,
                onClick = { onControlClick("climate_toggle") }
            )
            ControlCard(
                modifier = Modifier.weight(1f),
                title = "충전구 열기",
                icon = Icons.Default.EvStation,
                isActive = false,
                onClick = { onControlClick("open_charge_port") }
            )
        }

        // 상태 정보 요약
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                title = "실내 온도",
                value = "${vehicleState?.insideTemp ?: 0.0} ℃"
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                title = "실외 온도",
                value = "${vehicleState?.outsideTemp ?: 0.0} ℃"
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                title = "총 주행거리",
                value = "${vehicleState?.odometer ?: 0} km"
            )
        }
    }
}

@Composable
private fun ControlCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(
                1.dp,
                if (isActive) TeslaRed else DarkBorder,
                RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) TeslaRed.copy(alpha = 0.15f) else DarkCardBg
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) TeslaRed else TextPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun InfoCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier.border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BatteryAnalysisView(batteryRecords: List<BatteryDegradation>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("배터리 열화율 히스토리", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        items(batteryRecords) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(record.date, color = TextPrimary, fontSize = 14.sp)
                        Text("${record.maxRangeKm} km (최대 주행거리)", color = TextSecondary, fontSize = 12.sp)
                    }
                    Text(
                        "${record.degradationPercent}% 열화",
                        color = TeslaRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ChargingHistoryView(chargeRecords: List<ChargeRecord>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("충전 기록 및 비용 관리", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        items(chargeRecords) { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(record.location, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${record.date} • ${if (record.fastCharge) "급속 충전" else "완속 충전"}", color = TextSecondary, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("+${record.addedKwh} kWh", color = TeslaGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${record.cost} 원", color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MaintenanceView(consumables: List<ConsumableItem>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("소모품 교환주기 관리", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        items(consumables) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(item.status, color = TeslaBlue, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "마지막 교체: ${item.lastChangedKm} km (교체 주기: ${item.intervalKm} km)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TripLogView(trips: List<DailyTrip>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("일별 주행 전비 기록", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        items(trips) { trip ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(trip.date, color = TextPrimary, fontSize = 14.sp)
                        Text("${trip.distanceKm} km 주행", color = TextSecondary, fontSize = 12.sp)
                    }
                    Text(
                        "${trip.efficiencyWhPerKm} Wh/km",
                        color = TeslaGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsDialog(
    settingsManager: EncryptedSettingsManager,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val config = settingsManager.loadConfig()
    var url by remember { mutableStateOf(config.supabaseUrl) }
    var key by remember { mutableStateOf(config.supabaseKey) }
    var mapKey by remember { mutableStateOf(config.kakaoMapKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCardBg,
        title = { Text("암호화 보안 설정", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Supabase URL", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Supabase Anon Key", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
                OutlinedTextField(
                    value = mapKey,
                    onValueChange = { mapKey = it },
                    label = { Text("카카오맵 App Key", color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TeslaRed,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    settingsManager.saveConfig(AppConfig(url, key, mapKey))
                    onSave()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TeslaRed)
            ) {
                Text("암호화 저장", color = TextPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextSecondary)
            }
        }
    )
}
