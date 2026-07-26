package com.mdkdw1.ui.tesla

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

// 테슬라 다크모드 전용 칼라 파렛트
private val DarkBg = Color(0xFF0D0E12)
private val CardBg = Color(0xFF161820)
private val BorderColor = Color(0xFF262936)
private val TeslaRed = Color(0xFFE82127)
private val ElectricBlue = Color(0xFF3B82F6)
private val TextWhite = Color(0xFFF3F4F6)
private val TextMuted = Color(0xFF9CA3AF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaDashboardScreen(
    viewModel: TeslaViewModel = viewModel()
) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val consumables by viewModel.consumables.collectAsState()
    val dailyTrips by viewModel.dailyTrips.collectAsState()
    val chargeRecords by viewModel.chargeRecords.collectAsState()
    val degradationData by viewModel.degradationData.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val logs by viewModel.commandLog.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("대시보드", "배터리/차트", "소모품 관리", "주행 일지", "카카오맵", "보안 설정")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape = CircleShape,
                            color = Color(0xFF10B981)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = vehicleState.vehicleName,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshAllData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "새로고침",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        containerColor = DarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 탭 네비게이션
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkBg,
                contentColor = TeslaRed,
                edgePadding = 12.dp,
                divider = { HorizontalDivider(color = BorderColor) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) TeslaRed else TextMuted,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = TeslaRed,
                    trackColor = CardBg
                )
            }

            Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                when (selectedTab) {
                    0 -> DashboardTab(vehicleState, logs, onCommand = { viewModel.sendCommand(it) })
                    1 -> BatteryTab(vehicleState, degradationData, chargeRecords)
                    2 -> ConsumablesTab(consumables, onReset = { viewModel.resetConsumable(it) })
                    3 -> DailyTripsTab(dailyTrips)
                    4 -> KakaoMapTab(settings.kakaoMapKey)
                    5 -> SettingsTab(settings, onSave = { u, k, m, t -> viewModel.saveSettings(u, k, m, t) })
                }
            }
        }
    }
}

// ==========================================
// Tab 0: 대시보드 화면
// ==========================================
@Composable
private fun DashboardTab(
    state: VehicleState,
    logs: List<String>,
    onCommand: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("배터리 잔량", color = TextMuted, fontSize = 13.sp)
                            Text("${state.batteryLevel}%", color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("주행 가능 거리", color = TextMuted, fontSize = 13.sp)
                            Text("${state.estimatedRangeKm} km", color = ElectricBlue, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { state.batteryLevel / 100f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (state.batteryLevel > 20) Color(0xFF10B981) else TeslaRed,
                        trackColor = DarkBg,
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoTile(modifier = Modifier.weight(1f), title = "총 누적 거리", value = "${state.odometerKm} km")
                InfoTile(modifier = Modifier.weight(1f), title = "실내 / 실외 온도", value = "${state.insideTempC}℃ / ${state.outsideTempC}℃")
            }
        }

        item {
            Text("차량 수동 제어", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ControlButton(
                    modifier = Modifier.weight(1f),
                    label = if (state.isLocked) "잠금 해제" else "잠금 설정",
                    icon = if (state.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                    isActive = state.isLocked,
                    onClick = { onCommand(if (state.isLocked) "UNLOCK" else "LOCK") }
                )
                ControlButton(
                    modifier = Modifier.weight(1f),
                    label = if (state.isClimateOn) "공조 끄기" else "공조 켜기",
                    icon = Icons.Default.AcUnit,
                    isActive = state.isClimateOn,
                    onClick = { onCommand("CLIMATE_TOGGLE") }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ControlButton(
                    modifier = Modifier.weight(1f),
                    label = if (state.isTrunkOpen) "트렁크 닫기" else "트렁크 열기",
                    icon = Icons.Default.DirectionsCar,
                    isActive = state.isTrunkOpen,
                    onClick = { onCommand("TRUNK_TOGGLE") }
                )
                ControlButton(
                    modifier = Modifier.weight(1f),
                    label = if (state.isSentryModeOn) "감시모드 해제" else "감시모드 켜기",
                    icon = Icons.Default.Security,
                    isActive = state.isSentryModeOn,
                    onClick = { onCommand("SENTRY_TOGGLE") }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text("제어 및 상태 커맨드 로그", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(140.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                LazyColumn(modifier = Modifier.padding(12.dp)) {
                    items(logs) { log ->
                        Text(text = log, color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 1: 배터리 열화율 및 충전 기록
// ==========================================
@Composable
private fun BatteryTab(
    state: VehicleState,
    degradation: List<BatteryDegradationPoint>,
    records: List<ChargeRecord>
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("배터리 열화 추이 차트", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp).border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (degradation.isNotEmpty()) {
                            val path = Path()
                            val width = size.width
                            val height = size.height

                            degradation.forEachIndexed { i, pt ->
                                val x = (i.toFloat() / (degradation.size - 1)) * width
                                val y = height - ((pt.degradationPct.toFloat() / 5.0f) * height)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                drawCircle(color = TeslaRed, radius = 4.dp.toPx(), center = Offset(x, y))
                            }
                            drawPath(path = path, color = ElectricBlue, style = Stroke(width = 3.dp.toPx()))
                        }
                    }
                }
            }
        }

        item {
            Text("최근 충전 이력", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        items(records) { record ->
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(record.location, color = TextWhite, fontWeight = FontWeight.Bold)
                        Text(record.date, color = TextMuted, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${record.kWh} kWh", color = ElectricBlue, fontWeight = FontWeight.Bold)
                        Text("${record.cost} 원", color = TextWhite, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 2: 소모품 관리
// ==========================================
@Composable
private fun ConsumablesTab(
    items: List<ConsumableItem>,
    onReset: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(items) { item ->
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        TextButton(onClick = { onReset(item.name) }) {
                            Text("리셋", color = TeslaRed, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("사용: ${item.currentKm} km / 최대: ${item.maxKm} km (교체일: ${item.lastReplacedDate})", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { item.progressRatio },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = if (item.progressRatio > 0.8f) TeslaRed else ElectricBlue,
                        trackColor = DarkBg,
                    )
                }
            }
        }
    }
}

// ==========================================
// Tab 3: 주행 일지 및 전비 정보
// ==========================================
@Composable
private fun DailyTripsTab(trips: List<DailyTrip>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(trips) { trip ->
            Card(
                modifier = Modifier.fillMaxWidth().border(1.dp, BorderColor, RoundedCornerShape(8.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(trip.date, color = TextWhite, fontWeight = FontWeight.Bold)
                        Text("주행 거리: ${trip.distanceKm} km", color = TextMuted, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${trip.efficiencyWhPerKm} Wh/km", color = ElectricBlue, fontWeight = FontWeight.Bold)
                        Text("사용 에너지: ${trip.energyUsedKwh} kWh", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// Tab 4: 카카오맵 WebView 연동
// ==========================================
@Composable
private fun KakaoMapTab(kakaoKey: String) {
    Card(
        modifier = Modifier.fillMaxSize().border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    val html = """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="utf-8"/>
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=$kakaoKey"></script>
                            <style>
                                html, body { width:100%; height:100%; margin:0; padding:0; background-color:#0D0E12; }
                                #map { width:100%; height:100%; }
                            </style>
                        </head>
                        <body>
                            <div id="map"></div>
                            <script>
                                var container = document.getElementById('map');
                                var options = {
                                    center: new kakao.maps.LatLng(37.5665, 126.9780),
                                    level: 4
                                };
                                var map = new kakao.maps.Map(container, options);
                            </script>
                        </body>
                        </html>
                    """.trimIndent()
                    loadDataWithBaseURL("https://dapi.kakao.com", html, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ==========================================
// Tab 5: 보안 설정 화면 (AES 암호화 저장소)
// ==========================================
@Composable
private fun SettingsTab(
    currentConfig: AppConfig,
    onSave: (String, String, String, String) -> Unit
) {
    var supabaseUrl by remember(currentConfig) { mutableStateOf(currentConfig.supabaseUrl) }
    var supabaseKey by remember(currentConfig) { mutableStateOf(currentConfig.supabaseKey) }
    var kakaoMapKey by remember(currentConfig) { mutableStateOf(currentConfig.kakaoMapKey) }
    var teslaAccessToken by remember(currentConfig) { mutableStateOf(currentConfig.teslaAccessToken) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("AES-256 보안 암호화 설정 저장소", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("입력된 인증 키와 URL은 하드웨어 KeyStore 기반으로 암호화 저장됩니다.", color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            OutlinedTextField(
                value = supabaseUrl,
                onValueChange = { supabaseUrl = it },
                label = { Text("Supabase URL", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = supabaseKey,
                onValueChange = { supabaseKey = it },
                label = { Text("Supabase Anon Key", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = textFieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = kakaoMapKey,
                onValueChange = { kakaoMapKey = it },
                label = { Text("카카오맵 JS API Key", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors()
            )
        }

        item {
            OutlinedTextField(
                value = teslaAccessToken,
                onValueChange = { teslaAccessToken = it },
                label = { Text("Tesla API Access Token", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = textFieldColors()
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { onSave(supabaseUrl, supabaseKey, kakaoMapKey, teslaAccessToken) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TeslaRed),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("암호화 저장 및 적용", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// Sub Components & Helpers
// ==========================================
@Composable
private fun InfoTile(modifier: Modifier = Modifier, title: String, value: String) {
    Card(
        modifier = modifier.border(1.dp, BorderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ControlButton(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp).border(1.dp, if (isActive) TeslaRed else BorderColor, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isActive) TeslaRed.copy(alpha = 0.2f) else CardBg
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = label, tint = if (isActive) TeslaRed else TextWhite, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CardBg,
    unfocusedContainerColor = CardBg,
    focusedBorderColor = TeslaRed,
    unfocusedBorderColor = BorderColor,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite
)
