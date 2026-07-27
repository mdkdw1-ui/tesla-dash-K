package com.mdkdw1.ui.tesla

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// 다크 모드 UI 시스템 테마 색상
val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)
val AccentRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val chargingHistory by viewModel.chargingHistory.collectAsState()
    val batteryDegradation by viewModel.batteryDegradation.collectAsState()
    val consumables by viewModel.consumables.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tesla Dash K", color = TextPrimary, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground),
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextPrimary)
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                    }
                }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 상단 메인 탭
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkCard,
                contentColor = AccentBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("대시보드", color = TextPrimary) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("배터리 열화", color = TextPrimary) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("충전 기록", color = TextPrimary) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("소모품", color = TextPrimary) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("지도", color = TextPrimary) }
                )
            }

            when (selectedTab) {
                0 -> DashboardTab(vehicleState = vehicleState, viewModel = viewModel)
                1 -> BatteryDegradationTab(batteryDegradation = batteryDegradation)
                2 -> ChargingHistoryTab(chargingHistory = chargingHistory)
                3 -> ConsumablesTab(consumables = consumables)
                4 -> KakaoMapTab(vehicleState = vehicleState, settings = settings)
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
            },
            onClear = {
                viewModel.clearSettings()
                showSettingsDialog = false
            }
        )
    }
}

@Composable
fun DashboardTab(vehicleState: VehicleState, viewModel: TeslaViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 차량 상태 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("차량 상태", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .background(
                                if (vehicleState.isOnline) AccentGreen else AccentRed,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (vehicleState.isOnline) "온라인" else "오프라인",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatusItem("배터리", "${vehicleState.batteryLevel}%", "한도: ${vehicleState.chargeLimit}%")
                    StatusItem("주행거리", "${vehicleState.estimatedRangeKm} km", "100%: ${vehicleState.extrapolated100RangeKm} km")
                    StatusItem("속도", "${vehicleState.speedKmh} km/h", "누적: ${vehicleState.odometerKm.toInt()} km")
                }
            }
        }

        // 빠른 제어 버튼 그룹
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("빠른 제어", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ControlButton(
                        label = if (vehicleState.isLocked) "잠금 해제" else "잠금",
                        icon = if (vehicleState.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                        onClick = { viewModel.toggleDoorLock() }
                    )
                    ControlButton(
                        label = if (vehicleState.isClimateOn) "공조 끄기" else "공조 켜기",
                        icon = Icons.Default.AcUnit,
                        active = vehicleState.isClimateOn,
                        onClick = { viewModel.toggleClimate() }
                    )
                    ControlButton(
                        label = "센트리",
                        icon = Icons.Default.Security,
                        active = vehicleState.isSentryOn,
                        onClick = { viewModel.toggleSentryMode() }
                    )
                    ControlButton(
                        label = "충전구",
                        icon = Icons.Default.EvStation,
                        active = vehicleState.isChargePortOpen,
                        onClick = { viewModel.toggleChargePort() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ControlButton(
                        label = "트렁크",
                        icon = Icons.Default.ShoppingBag,
                        onClick = { viewModel.openTrunk() }
                    )
                    ControlButton(
                        label = "프렁크",
                        icon = Icons.Default.DirectionsCar,
                        onClick = { viewModel.openFrunk() }
                    )
                    ControlButton(
                        label = "전조등",
                        icon = Icons.Default.Lightbulb,
                        onClick = { viewModel.flashLights() }
                    )
                    ControlButton(
                        label = "경적",
                        icon = Icons.Default.VolumeUp,
                        onClick = { viewModel.honkHorn() }
                    )
                }
            }
        }

        // 공조기 제어 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("공조 및 온도 제어", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("실내: ${vehicleState.insideTemp}°C", color = TextPrimary)
                        Text("실외: ${vehicleState.outsideTemp}°C", color = Color.Gray, fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(onClick = { viewModel.adjustTargetTemp(-0.5f) }) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease Temp", tint = AccentBlue)
                        }
                        Text("${vehicleState.targetTemp}°C", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { viewModel.adjustTargetTemp(0.5f) }) {
                            Icon(Icons.Default.Add, contentDescription = "Increase Temp", tint = AccentAmber)
                        }
                    }
                }
            }
        }
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
fun ControlButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean = false, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .background(if (active) AccentBlue else DarkBorder, CircleShape)
        ) {
            Icon(icon, contentDescription = label, tint = TextPrimary)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextPrimary, fontSize = 11.sp)
    }
}

@Composable
fun BatteryDegradationTab(batteryDegradation: List<BatteryDegradationData>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("배터리 열화율 추이", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (batteryDegradation.isEmpty()) {
                Text("데이터가 없습니다.", color = Color.Gray)
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(DarkBackground, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    val maxDegradation = (batteryDegradation.maxOfOrNull { item -> item.degradationRate } ?: 5f).coerceAtLeast(1f)

                    val width = size.width
                    val height = size.height

                    val points = batteryDegradation.mapIndexed { index, item ->
                        val x = if (batteryDegradation.size > 1) {
                            (index.toFloat() / (batteryDegradation.size - 1)) * width
                        } else width / 2f

                        val y = height - ((item.degradationRate / maxDegradation) * height)
                        Offset(x, y)
                    }

                    if (points.size >= 2) {
                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                        drawPath(path, color = AccentAmber, style = Stroke(width = 4f))
                    }

                    points.forEach { point ->
                        drawCircle(color = AccentBlue, radius = 6f, center = point)
                    }
                }

                batteryDegradation.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${item.date} (${item.mileage}km)", color = TextPrimary, fontSize = 14.sp)
                        Text("열화율: ${item.degradationRate}%", color = AccentAmber, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ChargingHistoryTab(chargingHistory: List<ChargingSession>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("충전 기록", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (chargingHistory.isEmpty()) {
                Text("충전 기록이 없습니다.", color = Color.Gray)
            } else {
                chargingHistory.forEach { session ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBackground, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(session.location, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text(session.date, color = Color.Gray, fontSize = 12.sp)
                        }
                        Text("충전량: ${session.startPercent}% → ${session.endPercent}% (${session.energyAddedKwh} kWh)", color = TextPrimary, fontSize = 13.sp)
                        Text("비용: ${session.cost} 원", color = AccentGreen, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ConsumablesTab(consumables: List<ConsumableItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("소모품 관리", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            if (consumables.isEmpty()) {
                Text("소모품 데이터가 없습니다.", color = Color.Gray)
            } else {
                consumables.forEach { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(item.name, color = TextPrimary)
                            Text("남은 수명: ${item.remainingKm} km (${item.remainingPercent.toInt()}%)", color = Color.Gray, fontSize = 12.sp)
                        }
                        LinearProgressIndicator(
                            progress = (item.remainingPercent / 100f).coerceIn(0f, 1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = if (item.remainingPercent > 20f) AccentGreen else AccentRed,
                            trackColor = DarkBorder
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KakaoMapTab(vehicleState: VehicleState, settings: AppSettings) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("위치 및 카카오맵", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground, RoundedCornerShape(8.dp)),
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        val htmlData = """
                            <!DOCTYPE html>
                            <html>
                            <head>
                                <meta charset="utf-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${settings.kakaoKey}"></script>
                                <style>html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background-color: #0D0E12; }</style>
                            </head>
                            <body>
                                <div id="map"></div>
                                <script>
                                    var container = document.getElementById('map');
                                    var options = {
                                        center: new kakao.maps.LatLng(${vehicleState.latitude}, ${vehicleState.longitude}),
                                        level: 3
                                    };
                                    var map = new kakao.maps.Map(container, options);
                                    var markerPosition = new kakao.maps.LatLng(${vehicleState.latitude}, ${vehicleState.longitude});
                                    var marker = new kakao.maps.Marker({ position: markerPosition });
                                    marker.setMap(map);
                                </script>
                            </body>
                            </html>
                        """.trimIndent()
                        loadDataWithBaseURL("https://dapi.kakao.com", htmlData, "text/html", "UTF-8", null)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsDialog(
    currentSettings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit,
    onClear: () -> Unit
) {
    var supabaseUrl by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(currentSettings.kakaoKey) }
    var teslaToken by remember { mutableStateOf(currentSettings.teslaToken) }
    var vehicleId by remember { mutableStateOf(currentSettings.vehicleId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("보안 설정 (AES-256 암호화)", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
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
                    label = { Text("Kakao Map Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = teslaToken,
                    onValueChange = { teslaToken = it },
                    label = { Text("Tesla Token") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Vehicle ID") },
                    singleLine = true
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
                            vehicleId = vehicleId
                        )
                    )
                }
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onClear) {
                    Text("초기화", color = AccentRed)
                }
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            }
        },
        containerColor = DarkCard,
        titleContentColor = TextPrimary
    )
}
