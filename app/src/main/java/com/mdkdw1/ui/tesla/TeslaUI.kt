package com.mdkdw1/ui/tesla

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// 테마 색상 정의 (Dark Theme)
val BgDark = Color(0xFF0D0E12)
val CardDark = Color(0xFF161820)
val CardBorder = Color(0xFF262936)
val AccentRed = Color(0xFFE82127)
val AccentGreen = Color(0xFF10B981)
val AccentBlue = Color(0xFF3B82F6)
val AccentAmber = Color(0xFFD97706)
val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF9CA3AF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val appSettings by viewModel.settings.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val batteryDegradation by viewModel.batteryDegradation.collectAsState()
    val chargingHistory by viewModel.chargingHistory.collectAsState()
    val consumables by viewModel.consumables.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var selectedSubTab by remember { mutableStateOf(0) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = vehicleState.vehicleName,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "최신 갱신: ${vehicleState.lastUpdated}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.refreshAllData()
                            Toast.makeText(context, "데이터 동기화 시작", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "동기화",
                            tint = if (isRefreshing) AccentAmber else TextPrimary
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "설정",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgDark)
            )
        },
        containerColor = BgDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 메인 탭
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardDark,
                contentColor = TextPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("대시보드", fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("감시 가디언", fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("소모품 관리", fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("카카오맵", fontSize = 13.sp) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (selectedTab) {
                0 -> DashboardTabContent(
                    vehicleState = vehicleState,
                    selectedSubTab = selectedSubTab,
                    onSubTabSelected = { selectedSubTab = it },
                    batteryDegradation = batteryDegradation,
                    chargingHistory = chargingHistory,
                    onToggleLock = { viewModel.toggleDoorLock() },
                    onToggleClimate = { viewModel.toggleClimate() },
                    onToggleSentry = { viewModel.toggleSentry() },
                    onToggleTrunk = { viewModel.toggleTrunk() },
                    onToggleFrunk = { viewModel.toggleFrunk() }
                )
                1 -> SentryGuardianContent(
                    vehicleState = vehicleState,
                    onToggleSentry = { viewModel.toggleSentry() }
                )
                2 -> ConsumablesTabContent(
                    consumables = consumables,
                    onItemReplaced = { item ->
                        viewModel.updateConsumable(
                            item.copy(
                                lastReplacedKm = vehicleState.totalMileageKm,
                                lastReplacedOdoKm = vehicleState.totalMileageKm,
                                lastReplacedDate = "방금 전"
                            )
                        )
                        Toast.makeText(context, "${item.name} 교체 완료 기록", Toast.LENGTH_SHORT).show()
                    }
                )
                3 -> KakaoMapTabContent(kakaoKey = appSettings.kakaoKey)
            }
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = appSettings,
                onDismiss = { showSettingsDialog = false },
                onSave = { newSettings ->
                    viewModel.saveSettings(newSettings)
                    showSettingsDialog = false
                    Toast.makeText(context, "설정 저장 완료", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun DashboardTabContent(
    vehicleState: VehicleState,
    selectedSubTab: Int,
    onSubTabSelected: (Int) -> Unit,
    batteryDegradation: List<DegradationRecord>,
    chargingHistory: List<ChargeRecord>,
    onToggleLock: () -> Unit,
    onToggleClimate: () -> Unit,
    onToggleSentry: () -> Unit,
    onToggleTrunk: () -> Unit,
    onToggleFrunk: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 주요 상태 카드 (배터리, 주행거리, 온도)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusCard(
                modifier = Modifier.weight(1f),
                title = "배터리",
                value = "${vehicleState.batteryLevel}%",
                subtext = if (vehicleState.isCharging) "${vehicleState.chargingPowerKw} kW 충전 중" else "대기 중",
                color = if (vehicleState.batteryLevel > 20) AccentGreen else AccentRed
            )
            StatusCard(
                modifier = Modifier.weight(1f),
                title = "주행가능거리",
                value = "${vehicleState.estimatedRangeKm} km",
                subtext = "총 ${vehicleState.totalMileageKm} km",
                color = AccentBlue
            )
            StatusCard(
                modifier = Modifier.weight(1f),
                title = "실내/외 온도",
                value = "${vehicleState.insideTempC}°C",
                subtext = "실외 ${vehicleState.outsideTempC}°C",
                color = AccentAmber
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 빠른 제어 버튼
        Text("빠른 제어", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ControlButton(
                icon = if (vehicleState.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                label = if (vehicleState.isLocked) "잠금 해제" else "도어 잠금",
                isActive = vehicleState.isLocked,
                onClick = onToggleLock
            )
            ControlButton(
                icon = Icons.Default.AcUnit,
                label = "공조기",
                isActive = vehicleState.climateOn,
                onClick = onToggleClimate
            )
            ControlButton(
                icon = Icons.Default.Shield,
                label = "감시 모드",
                isActive = vehicleState.sentryMode,
                onClick = onToggleSentry
            )
            ControlButton(
                icon = Icons.Default.DirectionsCar,
                label = "프렁크",
                isActive = vehicleState.frunkOpen,
                onClick = onToggleFrunk
            )
            ControlButton(
                icon = Icons.Default.TimeToLeave,
                label = "트렁크",
                isActive = vehicleState.trunkOpen,
                onClick = onToggleTrunk
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 서브 탭 (차량 정보 / 주행 지도 / 월간 리포트 / 배터리 열화율)
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CardDark,
            contentColor = TextPrimary,
            edgePadding = 0.dp
        ) {
            Tab(selected = selectedSubTab == 0, onClick = { onSubTabSelected(0) }, text = { Text("차량 정보") })
            Tab(selected = selectedSubTab == 1, onClick = { onSubTabSelected(1) }, text = { Text("충전 기록") })
            Tab(selected = selectedSubTab == 2, onClick = { onSubTabSelected(2) }, text = { Text("배터리 열화율") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedSubTab) {
            0 -> VehicleInfoSubContent(vehicleState)
            1 -> ChargeHistorySubContent(chargingHistory)
            2 -> BatteryHealthSubContent(batteryDegradation)
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtext: String,
    color: Color
) {
    Card(
        modifier = modifier.border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, color = TextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtext, color = TextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(if (isActive) AccentBlue else CardDark, CircleShape)
                .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextPrimary, fontSize = 11.sp)
    }
}

@Composable
fun VehicleInfoSubContent(vehicleState: VehicleState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("차량 세부 제원", color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("차종: ${vehicleState.vehicleName}", color = TextSecondary, fontSize = 13.sp)
            Text("총 누적 주행거리: ${vehicleState.totalMileageKm} km", color = TextSecondary, fontSize = 13.sp)
            Text("도어 상태: ${if (vehicleState.isLocked) "잠김" else "열림"}", color = TextSecondary, fontSize = 13.sp)
            Text("공조 시스템: ${if (vehicleState.climateOn) "작동 중" else "꺼짐"}", color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
fun ChargeHistorySubContent(chargingHistory: List<ChargeRecord>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        chargingHistory.forEach { record ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(record.location, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${record.date} • ${record.durationMinutes}분 충전", color = TextSecondary, fontSize = 11.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("+${record.addedKwh} kWh", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${record.costKrw} 원 (${record.startSoc}% -> ${record.endSoc}%)", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryHealthSubContent(batteryDegradation: List<DegradationRecord>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("배터리 수명 (SOH) 추이", color = TextPrimary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (batteryDegradation.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    val maxHealth = 100f
                    val minHealth = 90f
                    val points = batteryDegradation.mapIndexed { index, record ->
                        val x = size.width * (index.toFloat() / (batteryDegradation.size - 1).coerceAtLeast(1))
                        val y = size.height * (1f - (record.healthPercent - minHealth) / (maxHealth - minHealth))
                        androidx.compose.ui.geometry.Offset(x, y)
                    }

                    val path = Path().apply {
                        points.forEachIndexed { i, offset ->
                            if (i == 0) moveTo(offset.x, offset.y) else lineTo(offset.x, offset.y)
                        }
                    }

                    drawPath(path = path, color = AccentGreen, style = Stroke(width = 4f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            batteryDegradation.forEach { record ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${record.date} (${record.mileageKm} km)", color = TextSecondary, fontSize = 12.sp)
                    Text("${record.healthPercent}%", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SentryGuardianContent(vehicleState: VehicleState, onToggleSentry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = CardDark)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (vehicleState.sentryMode) AccentRed else TextSecondary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (vehicleState.sentryMode) "감시 가디언 활성화됨" else "감시 가디언 비활성화됨",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "차량 주변 충격 및 움직임을 24시간 실시간 감지 중입니다.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onToggleSentry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (vehicleState.sentryMode) AccentRed else AccentBlue
                    )
                ) {
                    Text(if (vehicleState.sentryMode) "가디언 정지" else "가디언 가동")
                }
            }
        }
    }
}

@Composable
fun ConsumablesTabContent(
    consumables: List<ConsumableItem>,
    onItemReplaced: (ConsumableItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("소모품 교체 및 정비 관리", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)

        consumables.forEach { item ->
            val usedKm = item.currentMileageKm - item.lastReplacedKm
            val progress = (usedKm.toFloat() / item.replacementIntervalKm.toFloat()).coerceIn(0f, 1f)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Button(
                            onClick = { onItemReplaced(item) },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("교체 완료", fontSize = 11.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = if (progress > 0.85f) AccentRed else AccentGreen,
                        trackColor = CardBorder
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("사용: $usedKm km / 주과: ${item.replacementIntervalKm} km", color = TextSecondary, fontSize = 11.sp)
                        Text("마지막 교체일: ${item.lastReplacedDate}", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun KakaoMapTabContent(kakaoKey: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    this.settings.javaScriptEnabled = true
                    this.settings.domStorageEnabled = true
                    loadUrl("https://m.map.kakao.com/")
                }
            },
            modifier = Modifier.fillMaxSize()
        )
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
    var kakaoKey by remember { mutableStateOf(currentSettings.kakaoKey) }
    var teslaToken by remember { mutableStateOf(currentSettings.teslaToken) }
    var vehicleId by remember { mutableStateOf(currentSettings.vehicleId) }
    var githubKey by remember { mutableStateOf(currentSettings.githubKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("시스템 및 API 설정", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
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
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("카카오맵 API Key") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = teslaToken,
                    onValueChange = { teslaToken = it },
                    label = { Text("Tesla Access Token") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Tesla Vehicle ID") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = githubKey,
                    onValueChange = { githubKey = it },
                    label = { Text("GitHub API Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AppSettings(
                            supabaseUrl = supabaseUrl,
                            supabaseKey = supabaseKey,
                            kakaoKey = kakaoKey,
                            teslaToken = teslaToken,
                            vehicleId = vehicleId,
                            githubKey = githubKey
                        )
                    )
                }
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextSecondary)
            }
        },
        containerColor = CardDark,
        titleContentColor = TextPrimary
    )
}
