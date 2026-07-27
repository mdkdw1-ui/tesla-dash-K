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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 테슬라 다크 테마 색상 정의
val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF9CA3AF)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)
val AccentRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val mainTab by viewModel.currentMainTab.collectAsState()
    val subTab by viewModel.currentSubTab.collectAsState()
    val vehicleState by viewModel.vehicleState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            // 상단 탑바 (타이틀 및 우측 상단 설정/새로고침 버튼)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "Tesla Logo",
                        tint = AccentAmber,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "TESLA COMMAND HUB",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 우측 상단 설정 및 새로고침 아이콘 배치
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextSecondary
                        )
                    }
                    IconButton(onClick = { viewModel.openSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = AccentAmber
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. 대분류 메인 탭 (테슬라 모니터 vs 감시 가디언)
            TabRow(
                selectedTabIndex = mainTab,
                containerColor = DarkCard,
                contentColor = TextPrimary,
                divider = { HorizontalDivider(color = DarkBorder) }
            ) {
                Tab(
                    selected = mainTab == 0,
                    onClick = { viewModel.setMainTab(0) },
                    text = {
                        Text(
                            "테슬라 모니터",
                            fontWeight = if (mainTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Monitor, contentDescription = null) }
                )
                Tab(
                    selected = mainTab == 1,
                    onClick = { viewModel.setMainTab(1) },
                    text = {
                        Text(
                            "감시 가디언",
                            fontWeight = if (mainTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    icon = { Icon(Icons.Default.Security, contentDescription = null) }
                )
            }

            // 2. 화면 본문 구성
            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = AccentAmber,
                    trackColor = DarkCard
                )
            }

            if (mainTab == 0) {
                // ----------------------------------------------------
                // [큰 축 1] 테슬라 모니터 (4개 서브 탭 지원)
                // ----------------------------------------------------
                ScrollableTabRow(
                    selectedTabIndex = subTab,
                    containerColor = DarkBackground,
                    contentColor = AccentAmber,
                    edgePadding = 8.dp,
                    divider = { HorizontalDivider(color = DarkBorder) }
                ) {
                    val subTabs = listOf("주행 기록", "배터리 & 충전", "소모품 관리", "내 차량 정보")
                    subTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = subTab == index,
                            onClick = { viewModel.setSubTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    color = if (subTab == index) AccentAmber else TextSecondary,
                                    fontWeight = if (subTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (subTab) {
                        0 -> DriveLogSubScreen(viewModel)
                        1 -> BatteryChargingSubScreen(viewModel)
                        2 -> ConsumablesSubScreen()
                        3 -> VehicleInfoSubScreen(vehicleState, viewModel)
                    }
                }
            } else {
                // ----------------------------------------------------
                // [큰 축 2] 감시 가디언 화면
                // ----------------------------------------------------
                SentryGuardianScreen(viewModel)
            }
        }
    }

    // 설정 다이얼로그 (우측 상단 클릭 시)
    if (showSettingsDialog) {
        SettingsDialog(
            currentSettings = settings,
            onDismiss = { viewModel.closeSettings() },
            onSave = { updatedSettings -> viewModel.saveSettings(updatedSettings) }
        )
    }
}

// --------------------------------------------------------------------
// [서브 탭 4] 내 차량 정보 화면 (첨부 이미지 디자인 완벽 적용)
// --------------------------------------------------------------------
@Composable
fun VehicleInfoSubScreen(state: VehicleState, viewModel: TeslaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 차량 주요 정보 헤더 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = state.vehicleName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "VIN: ${state.vin}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AccentGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = state.statusText,
                            color = AccentGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoItem("소프트웨어", state.carSoftwareVersion)
                    InfoItem("총 주행거리", "${String.format("%.1f", state.odometerKm)} km")
                    InfoItem("예상 주행거리", "${state.estimatedRangeKm} km")
                }
            }
        }

        // 2. 실내외 온도 & 센트리 모드 제어 카드
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Thermostat, contentDescription = null, tint = AccentAmber)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("실내 / 실외 온도", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${state.insideTemp}°C / ${state.outsideTemp}°C",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = AccentBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("감시 모드 (Sentry)", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Switch(
                        checked = state.sentryModeOn,
                        onCheckedChange = { viewModel.toggleSentryMode(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentAmber)
                    )
                }
            }
        }

        // 3. 타이어 공기압 (FL, FR, RL, RR) 4륜 시스템 시각적 배치
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.TireRepair, contentDescription = null, tint = AccentAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "타이어 공기압 현황 (PSI)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 차 형태의 4륜 배치
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 전륜
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TirePressureCard("전좌 (FL)", state.flTire)
                        TirePressureCard("전우 (FR)", state.frTire)
                    }

                    // 후륜
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TirePressureCard("후좌 (RL)", state.rlTire)
                        TirePressureCard("후우 (RR)", state.rrTire)
                    }
                }
            }
        }
    }
}

@Composable
fun TirePressureCard(label: String, psi: Double) {
    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBackground),
        border = BorderStroke(1.dp, if (psi >= 40.0) DarkBorder else AccentRed)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "$psi PSI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (psi >= 40.0) AccentGreen else AccentRed
            )
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}

// --------------------------------------------------------------------
// [서브 탭 1] 주행 기록 화면
// --------------------------------------------------------------------
@Composable
fun DriveLogSubScreen(viewModel: TeslaViewModel) {
    val logs by viewModel.driveLogs.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(logs) { log ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(log.date, fontSize = 12.sp, color = AccentAmber)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "${log.startLocation} ➔ ${log.endLocation}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "전비: ${log.efficiencyWhKm} Wh/km | 사용량: ${log.energyUsedKwh} kWh",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Text(
                        "${log.distanceKm} km",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// [서브 탭 2] 배터리 & 충전 화면
// --------------------------------------------------------------------
@Composable
fun BatteryChargingSubScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val batteryData by viewModel.batteryDegradation.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("배터리 잔량 현황", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${vehicleState.batteryPercent}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentGreen
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "예상 주행거리: ${vehicleState.estimatedRangeKm} km",
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("배터리 열화 트렌드", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                batteryData.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.date, fontSize = 13.sp, color = TextSecondary)
                        Text("건강도: ${item.degradationPercent}% (${item.fullRangeKm} km)", fontSize = 13.sp, color = TextPrimary)
                    }
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// [서브 탭 3] 소모품 관리 화면
// --------------------------------------------------------------------
@Composable
fun ConsumablesSubScreen() {
    val items = remember {
        listOf(
            ConsumableItem("1", "에어컨 캐빈 필터", 24000.0, 20000.0, 45),
            ConsumableItem("2", "와이퍼 블레이드", 12000.0, 15000.0, 80),
            ConsumableItem("3", "브레이크 오일 (DOT4)", 34000.0, 40000.0, 90),
            ConsumableItem("4", "타이어 위치 교환", 15000.0, 10000.0, 30)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            "${item.statusPercent}% 남음",
                            color = if (item.statusPercent < 40) AccentRed else AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { item.statusPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (item.statusPercent < 40) AccentRed else AccentGreen,
                        trackColor = DarkBackground,
                    )
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// [큰 축 2] 감시 가디언 화면
// --------------------------------------------------------------------
@Composable
fun SentryGuardianScreen(viewModel: TeslaViewModel) {
    val events by viewModel.sentryEvents.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, AccentAmber)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("감시 가디언 실시간 모니터링", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("주차 중 차량 주위 감시 로그 및 이벤트를 모니터링합니다.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        items(events) { event ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = BorderStroke(1.dp, DarkBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(event.eventType, color = AccentAmber, fontWeight = FontWeight.Bold)
                        Text(event.timestamp, fontSize = 12.sp, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(event.location, fontSize = 14.sp, color = TextPrimary)
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// 설정 다이얼로그 (우측 상단 클릭 / 중복 필드 정리 및 테슬라 ID/PW 제거)
// --------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentSettings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit
) {
    var supabaseUrl by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoMapKey by remember { mutableStateOf(currentSettings.kakaoMapKey) }
    var githubToken by remember { mutableStateOf(currentSettings.githubToken) }
    var isAutoSync by remember { mutableStateOf(currentSettings.isAutoSync) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = {
            Text(
                "시스템 API 설정",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentAmber)
                )

                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key (Anon Key)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentAmber)
                )

                OutlinedTextField(
                    value = kakaoMapKey,
                    onValueChange = { kakaoMapKey = it },
                    label = { Text("카카오맵 API Key") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentAmber)
                )

                // 중복되던 Key/Token을 단일 GitHub Token으로 통합
                OutlinedTextField(
                    value = githubToken,
                    onValueChange = { githubToken = it },
                    label = { Text("GitHub Token") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentAmber)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("실시간 자동 동기화", color = TextPrimary)
                    Switch(
                        checked = isAutoSync,
                        onCheckedChange = { isAutoSync = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AccentAmber)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AppSettings(
                            supabaseUrl = supabaseUrl,
                            supabaseKey = supabaseKey,
                            kakaoMapKey = kakaoMapKey,
                            githubToken = githubToken,
                            isAutoSync = isAutoSync
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
            ) {
                Text("저장", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextSecondary)
            }
        }
    )
}
