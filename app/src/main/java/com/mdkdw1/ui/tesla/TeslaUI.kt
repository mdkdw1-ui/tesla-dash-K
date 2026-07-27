package com.mdkdw1.ui.tesla

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// 테마 색상 정의 (index-2.html 다크모드 대응)
val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)
val AccentRed = Color(0xFFEF4444)
val AccentPurple = Color(0xFF8B5CF6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val chargingHistory by viewModel.chargingHistory.collectAsState()
    val batteryDegradation by viewModel.batteryDegradation.collectAsState()
    val consumables by viewModel.consumables.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("대시보드", "배터리&주행거리", "충전&소모품", "위치&지도", "설정")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = AccentBlue
                        )
                        Text(
                            text = "Tesla Command Hub",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "새로고침",
                            tint = AccentBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCard
                )
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 상단 탭 메인 메뉴
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = DarkCard,
                contentColor = AccentBlue,
                edgePadding = 12.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) AccentBlue else Color.Gray,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // 탭 변경에 따른 본문 콘텐츠
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBackground)
            ) {
                when (selectedTabIndex) {
                    0 -> DashboardTab(
                        vehicleState = vehicleState,
                        viewModel = viewModel
                    )
                    1 -> BatteryRangeTab(
                        vehicleState = vehicleState,
                        degradationList = batteryDegradation
                    )
                    2 -> ChargingConsumablesTab(
                        chargingList = chargingHistory,
                        consumablesList = consumables
                    )
                    3 -> LocationMapTab(
                        vehicleState = vehicleState,
                        settings = appSettings
                    )
                    4 -> SettingsTab(
                        settings = appSettings,
                        onSave = { updatedSettings ->
                            viewModel.saveSettings(updatedSettings)
                        },
                        onClear = {
                            viewModel.clearSettings()
                        }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. 대시보드 탭 (DashboardTab)
// -----------------------------------------------------------------------------
@Composable
fun DashboardTab(
    vehicleState: VehicleState,
    viewModel: TeslaViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 차량 상태 바
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = vehicleState.vehicleName,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (vehicleState.isOnline) "온라인 (연결됨)" else "오프라인 (절전 모드)",
                        color = if (vehicleState.isOnline) AccentGreen else AccentRed,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    color = if (vehicleState.isLocked) DarkBorder else AccentAmber.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, if (vehicleState.isLocked) DarkBorder else AccentAmber)
                ) {
                    Text(
                        text = if (vehicleState.isLocked) "잠김" else "잠금 해제됨",
                        color = if (vehicleState.isLocked) Color.Gray else AccentAmber,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 빠른 제어 버튼 격자
        Text(
            text = "원격 제어",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlTile(
                title = if (vehicleState.isLocked) "잠금 해제" else "도어 잠금",
                icon = if (vehicleState.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                isActive = vehicleState.isLocked,
                activeColor = AccentBlue,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleDoorLock() }
            )
            ControlTile(
                title = "공조 " + if (vehicleState.isClimateOn) "켜짐" else "꺼짐",
                icon = Icons.Default.AcUnit,
                isActive = vehicleState.isClimateOn,
                activeColor = AccentBlue,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleClimate() }
            )
            ControlTile(
                title = "센트리 " + if (vehicleState.isSentryOn) "켜짐" else "꺼짐",
                icon = Icons.Default.Security,
                isActive = vehicleState.isSentryOn,
                activeColor = AccentRed,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleSentryMode() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlTile(
                title = "트렁크 열기",
                icon = Icons.Default.DirectionsCar,
                isActive = false,
                activeColor = DarkBorder,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.openTrunk() }
            )
            ControlTile(
                title = "프렁크 열기",
                icon = Icons.Default.MinorCrash,
                isActive = false,
                activeColor = DarkBorder,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.openFrunk() }
            )
            ControlTile(
                title = "충전구 열기",
                icon = Icons.Default.EvStation,
                isActive = vehicleState.isChargePortOpen,
                activeColor = AccentGreen,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleChargePort() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlTile(
                title = "전조등 점멸",
                icon = Icons.Default.Lightbulb,
                isActive = false,
                activeColor = DarkBorder,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.flashLights() }
            )
            ControlTile(
                title = "경적 울리기",
                icon = Icons.Default.VolumeUp,
                isActive = false,
                activeColor = DarkBorder,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.honkHorn() }
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        // 주요 실시간 메트릭 카드
        Text(
            text = "차량 상태 모니터링",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "배터리 잔량",
                value = "${vehicleState.batteryLevel}%",
                subtext = if (vehicleState.isCharging) "충전 중 (${vehicleState.chargeLimit}% 제한)" else "대기 중",
                icon = Icons.Default.BatteryChargingFull,
                accentColor = AccentGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "예상 주행거리",
                value = "${vehicleState.estimatedRangeKm} km",
                subtext = "100% 환산: ${vehicleState.extrapolated100RangeKm} km",
                icon = Icons.Default.Speed,
                accentColor = AccentBlue,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "실내/실외 온도",
                value = "${vehicleState.insideTemp}°C / ${vehicleState.outsideTemp}°C",
                subtext = "목표 온도: ${vehicleState.targetTemp}°C",
                icon = Icons.Default.Thermostat,
                accentColor = AccentAmber,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "누적 주행거리",
                value = "${vehicleState.odometerKm} km",
                subtext = "속도: ${vehicleState.speedKmh} km/h",
                icon = Icons.Default.Navigation,
                accentColor = AccentPurple,
                modifier = Modifier.weight(1f)
            )
        }

        // 공조 설정 제어 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "실내 온도 설정",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.adjustTargetTemp(-0.5f) },
                        modifier = Modifier
                            .background(DarkBorder, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "온도 감축", tint = TextPrimary)
                    }

                    Text(
                        text = "${vehicleState.targetTemp} °C",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { viewModel.adjustTargetTemp(0.5f) },
                        modifier = Modifier
                            .background(DarkBorder, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "온도 증가", tint = TextPrimary)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 2. 배터리 & 주행거리 탭 (BatteryRangeTab)
// -----------------------------------------------------------------------------
@Composable
fun BatteryRangeTab(
    vehicleState: VehicleState,
    degradationList: List<BatteryDegradationData>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "배터리 건강 상태 (SoH)",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${100.0f - vehicleState.degradationRate}%",
                        color = AccentGreen,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "(열화율: ${vehicleState.degradationRate}%)",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }

        // 배터리 열화 트렌드 커스텀 차트
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "배터리 열화율 히스토리 (%)",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                DegradationChart(
                    data = degradationList,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
fun DegradationChart(
    data: List<BatteryDegradationData>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("데이터가 없습니다.", color = Color.Gray)
        }
        return
    }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val maxDeg = (data.maxOfOrNull { it.degradationPercent } ?: 5.0f).coerceAtLeast(5.0f)
        val minDeg = 0.0f

        val points = data.mapIndexed { index, item ->
            val x = (index.toFloat() / (data.size - 1).coerceAtLeast(1)) * width
            val y = height - ((item.degradationPercent - minDeg) / (maxDeg - minDeg)) * height
            Offset(x, y)
        }

        // 가이드 라인 그리기
        drawLine(
            color = DarkBorder,
            start = Offset(0f, height / 2),
            end = Offset(width, height / 2),
            strokeWidth = 1.dp.toPx()
        )

        // 라인 경로 생성
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
        }

        drawPath(
            path = path,
            color = AccentPurple,
            style = Stroke(width = 3.dp.toPx())
        )

        // 포인트 원 그리기
        points.forEach { point ->
            drawCircle(
                color = AccentAmber,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 3. 충전 & 소모품 탭 (ChargingConsumablesTab)
// -----------------------------------------------------------------------------
@Composable
fun ChargingConsumablesTab(
    chargingList: List<ChargingSession>,
    consumablesList: List<ConsumableItem>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 소모품 관리 섹션
        Text(
            text = "소모품 교체주기 관리",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        consumablesList.forEach { consumable ->
            ConsumableProgressCard(consumable)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 최근 충전 히스토리
        Text(
            text = "최근 충전 기록",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        chargingList.forEach { session ->
            ChargingSessionItem(session)
        }
    }
}

@Composable
fun ConsumableProgressCard(item: ConsumableItem) {
    val progress = (item.remainingPercent / 100.0f).coerceIn(0.0f, 1.0f)
    val progressColor = when {
        progress > 0.6f -> AccentGreen
        progress > 0.3f -> AccentAmber
        else -> AccentRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.name, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(text = "${item.remainingPercent}% 남음", color = progressColor, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = DarkBorder
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "최종 교체: ${item.lastReplacedDate} | 추천 교체 주기: ${item.replacementIntervalKm} km",
                color = Color.Gray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun ChargingSessionItem(session: ChargingSession) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = session.locationName, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(text = session.date, color = Color.Gray, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "+${session.chargedKwh} kWh", color = AccentGreen, fontWeight = FontWeight.Bold)
                Text(text = "₩${session.costAmount}", color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 4. 위치 & 지도 탭 (LocationMapTab - 카카오맵 WebView 연동)
// -----------------------------------------------------------------------------
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LocationMapTab(
    vehicleState: VehicleState,
    settings: AppSettings
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "차량 실시간 위치 (Kakao Map)",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "위도: ${vehicleState.latitude} | 경도: ${vehicleState.longitude}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (settings.kakaoKey.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    webChromeClient = WebChromeClient()

                                    // 핵심 수정: outer scope `settings` 가림 방지를 위해 `this.settings` 명시적 사용
                                    this.settings.javaScriptEnabled = true
                                    this.settings.domStorageEnabled = true
                                    this.settings.databaseEnabled = true
                                    this.settings.useWideViewPort = true
                                    this.settings.loadWithOverviewMode = true

                                    val mapHtml = """
                                        <!DOCTYPE html>
                                        <html>
                                        <head>
                                            <meta charset="utf-8"/>
                                            <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                                            <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${settings.kakaoKey}"></script>
                                            <style>
                                                html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background: #0D0E12; }
                                            </style>
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
                                                var marker = new kakao.maps.Marker({
                                                    position: markerPosition
                                                });
                                                marker.setMap(map);
                                            </script>
                                        </body>
                                        </html>
                                    """.trimIndent()

                                    loadDataWithBaseURL(
                                        "https://dapi.kakao.com",
                                        mapHtml,
                                        "text/html",
                                        "UTF-8",
                                        null
                                    )
                                }
                            },
                            update = { webView ->
                                val updatedHtml = """
                                    <!DOCTYPE html>
                                    <html>
                                    <head>
                                        <meta charset="utf-8"/>
                                        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                                        <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${settings.kakaoKey}"></script>
                                        <style>
                                            html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background: #0D0E12; }
                                        </style>
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
                                            var marker = new kakao.maps.Marker({
                                                position: markerPosition
                                            });
                                            marker.setMap(map);
                                        </script>
                                    </body>
                                    </html>
                                """.trimIndent()

                                webView.loadDataWithBaseURL(
                                    "https://dapi.kakao.com",
                                    updatedHtml,
                                    "text/html",
                                    "UTF-8",
                                    null
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(DarkBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "설정 탭에서 Kakao Map JavaScript API Key를 설정해 주세요.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 5. 설정 탭 (SettingsTab)
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(
    settings: AppSettings,
    onSave: (AppSettings) -> Unit,
    onClear: () -> Unit
) {
    var supabaseUrl by remember(settings) { mutableStateOf(settings.supabaseUrl) }
    var supabaseKey by remember(settings) { mutableStateOf(settings.supabaseKey) }
    var kakaoKey by remember(settings) { mutableStateOf(settings.kakaoKey) }
    var teslaToken by remember(settings) { mutableStateOf(settings.teslaToken) }
    var vehicleId by remember(settings) { mutableStateOf(settings.vehicleId) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "API & 암호화 서비스 설정",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Anon Key") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("Kakao Map JavaScript Key") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = teslaToken,
                    onValueChange = { teslaToken = it },
                    label = { Text("Tesla Refresh Token") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Vehicle ID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkBorder,
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = Color.Gray,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("설정 저장 (AES-256 암호화)", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = {
                            onClear()
                            supabaseUrl = ""
                            supabaseKey = ""
                            kakaoKey = ""
                            teslaToken = ""
                            vehicleId = ""
                        },
                        border = BorderStroke(1.dp, AccentRed),
                        modifier = Modifier.weight(0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("초기화", color = AccentRed)
                    }
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 공통 재사용 UI 컴포넌트
// -----------------------------------------------------------------------------
@Composable
fun ControlTile(
    title: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) activeColor.copy(alpha = 0.15f) else DarkCard
        ),
        border = BorderStroke(1.dp, if (isActive) activeColor else DarkBorder),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                color = if (isActive) activeColor else TextPrimary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = BorderStroke(1.dp, DarkBorder),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
                Text(text = title, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtext, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
