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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val vehicleState by viewModel.vehicleState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val driveLogs by viewModel.driveLogs.collectAsState()
    val monthlyReports by viewModel.monthlyReports.collectAsState()
    val batteryDegradationList by viewModel.batteryDegradationList.collectAsState()
    val sentryEvents by viewModel.sentryEvents.collectAsState()
    val consumables by viewModel.consumables.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = vehicleState.vehicleName,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "상태: ${vehicleState.statusText} (${vehicleState.lastUpdatedTimestamp})",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "새로고침", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkCard) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "대시보드") },
                    label = { Text("대시보드") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Timeline, contentDescription = "주행/전비") },
                    label = { Text("주행/전비") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = "배터리/감시") },
                    label = { Text("배터리/감시") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "설정") },
                    label = { Text("설정") }
                )
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(vehicleState, viewModel)
                1 -> DriveLogScreen(driveLogs, monthlyReports)
                2 -> BatterySentryScreen(batteryDegradationList, sentryEvents, consumables)
                3 -> SettingsScreen(settings, onSaveSettings = { viewModel.saveSettings(it) })
            }
        }
    }
}

@Composable
fun DashboardScreen(vehicleState: VehicleState, viewModel: TeslaViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 제어 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlButton(
                modifier = Modifier.weight(1f),
                title = if (vehicleState.isLocked) "잠금 해제" else "잠금",
                icon = if (vehicleState.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                isActive = vehicleState.isLocked,
                onClick = { viewModel.toggleLock() }
            )
            ControlButton(
                modifier = Modifier.weight(1f),
                title = if (vehicleState.climateOn) "공조 끄기" else "공조 켜기",
                icon = Icons.Default.AcUnit,
                isActive = vehicleState.climateOn,
                onClick = { viewModel.toggleClimate() }
            )
            ControlButton(
                modifier = Modifier.weight(1f),
                title = if (vehicleState.isCharging) "충전 중지" else "충전 시작",
                icon = Icons.Default.Bolt,
                isActive = vehicleState.isCharging,
                onClick = { viewModel.toggleCharging() }
            )
            ControlButton(
                modifier = Modifier.weight(1f),
                title = if (vehicleState.sentryModeOn) "감시 ON" else "감시 OFF",
                icon = Icons.Default.Security,
                isActive = vehicleState.sentryModeOn,
                onClick = { viewModel.toggleSentryMode() }
            )
        }

        // 배터리 및 주행거리 카드
        DarkCardView {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("배터리 및 예상 주행거리", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${vehicleState.batteryLevel}%",
                        color = TextPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${vehicleState.estimatedRange} km / ${vehicleState.maxRange} km",
                        color = AccentAmber,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { vehicleState.batteryLevel / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = AccentAmber,
                    trackColor = DarkBorder
                )
            }
        }

        // 실내/외 온도 카드
        DarkCardView {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatusItem("실내 온도", "${vehicleState.insideTemp} °C", "목표 ${vehicleState.targetTemp} °C")
                StatusItem("실외 온도", "${vehicleState.outsideTemp} °C", "외기 온습도")
            }
        }

        // 타이어 공기압 카드
        DarkCardView {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("타이어 공기압 (PSI)", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("전륜 좌: ${vehicleState.flTire}", color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("후륜 좌: ${vehicleState.rlTire}", color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("전륜 우: ${vehicleState.frTire}", color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("후륜 우: ${vehicleState.rrTire}", color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun DriveLogScreen(driveLogs: List<DriveLogItem>, monthlyReports: List<MonthlyReport>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("월간 요약 리포트", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        for (report in monthlyReports) {
            DarkCardView {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(report.month, color = AccentAmber, fontWeight = FontWeight.Bold)
                        Text("주행: ${report.totalDistanceKm} km", color = TextPrimary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("전비: ${report.avgEfficiency} Wh/km", color = AccentBlue)
                        Text("비용: ₩${report.totalCostKrw}", color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("최근 주행 기록", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        for (log in driveLogs) {
            DarkCardView {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.date, color = Color.Gray, fontSize = 12.sp)
                        Text("${log.distanceKm} km (${log.durationMinutes}분)", color = TextPrimary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${log.startLocation} ➔ ${log.endLocation}", color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("평균 전비: ${log.avgEfficiencyWhPerKm} Wh/km | 소모: ${log.energyUsedKwh} kWh", color = AccentBlue, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BatterySentryScreen(
    batteryList: List<BatteryDegradationItem>,
    sentryEvents: List<SentryEventItem>,
    consumables: List<ConsumableItem>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("배터리 열화율 이력", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        for (b in batteryList) {
            DarkCardView {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(b.date, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("주행거리: ${b.odometerKm} km", color = Color.Gray, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("열화율: ${b.degradationPercent}%", color = AccentAmber, fontWeight = FontWeight.Bold)
                        Text("100% 예상: ${b.estimated100PercentRange} km", color = AccentBlue, fontSize = 12.sp)
                    }
                }
            }
        }

        Text("감시 모드(Sentry) 이벤트", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        for (s in sentryEvents) {
            DarkCardView {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(s.timestamp, color = AccentAmber, fontSize = 12.sp)
                    Text("${s.location} (${s.cameraAngle})", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("소모품 관리", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        for (c in consumables) {
            DarkCardView {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(c.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("사용률: ${c.currentUsagePercent}%", color = AccentAmber)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("마지막 교체: ${c.lastReplacedDate} (${c.lastReplacedKm} km)", color = Color.Gray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    onSaveSettings: (AppSettings) -> Unit
) {
    var supabaseUrl by remember(currentSettings) { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember(currentSettings) { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoMapKey by remember(currentSettings) { mutableStateOf(currentSettings.kakaoMapKey) }
    var teslaClientId by remember(currentSettings) { mutableStateOf(currentSettings.teslaClientId) }
    var teslaClientSecret by remember(currentSettings) { mutableStateOf(currentSettings.teslaClientSecret) }
    var githubKey by remember(currentSettings) { mutableStateOf(currentSettings.githubKey) }
    var githubToken by remember(currentSettings) { mutableStateOf(currentSettings.githubToken) }
    var isAutoSync by remember(currentSettings) { mutableStateOf(currentSettings.isAutoSync) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("앱 암호화 보안 설정", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        DarkOutlinedTextField(
            value = supabaseUrl,
            onValueChange = { supabaseUrl = it },
            label = "Supabase URL"
        )
        DarkOutlinedTextField(
            value = supabaseKey,
            onValueChange = { supabaseKey = it },
            label = "Supabase Key",
            isPassword = true
        )
        DarkOutlinedTextField(
            value = kakaoMapKey,
            onValueChange = { kakaoMapKey = it },
            label = "카카오맵 API Key",
            isPassword = true
        )
        DarkOutlinedTextField(
            value = teslaClientId,
            onValueChange = { teslaClientId = it },
            label = "Tesla Client ID"
        )
        DarkOutlinedTextField(
            value = teslaClientSecret,
            onValueChange = { teslaClientSecret = it },
            label = "Tesla Client Secret",
            isPassword = true
        )
        DarkOutlinedTextField(
            value = githubKey,
            onValueChange = { githubKey = it },
            label = "GitHub Key"
        )
        DarkOutlinedTextField(
            value = githubToken,
            onValueChange = { githubToken = it },
            label = "GitHub Token",
            isPassword = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("자동 백업/동기화", color = TextPrimary)
            Switch(
                checked = isAutoSync,
                onCheckedChange = { isAutoSync = it }
            )
        }

        Button(
            onClick = {
                onSaveSettings(
                    AppSettings(
                        supabaseUrl = supabaseUrl,
                        supabaseKey = supabaseKey,
                        kakaoMapKey = kakaoMapKey,
                        teslaClientId = teslaClientId,
                        teslaClientSecret = teslaClientSecret,
                        githubKey = githubKey,
                        githubToken = githubToken,
                        isAutoSync = isAutoSync
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
        ) {
            Text("설정 저장 (AES-256 암호화)", color = Color.White)
        }
    }
}

@Composable
fun DarkCardView(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard, RoundedCornerShape(12.dp))
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
    ) {
        content()
    }
}

@Composable
fun ControlButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) AccentAmber else DarkCard
        ),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = title, tint = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(title, color = TextPrimary, fontSize = 10.sp)
        }
    }
}

@Composable
fun StatusItem(title: String, value: String, subtext: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(subtext, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
fun DarkOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AccentBlue,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
        )
    )
}
