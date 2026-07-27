package com.mdkdw1.ui.tesla

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val degradationList by viewModel.degradationList.collectAsState()
    val chargeRecords by viewModel.chargeRecords.collectAsState()
    val consumables by viewModel.consumables.collectAsState()
    val currentTab by viewModel.currentMainTab.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = TeslaColors.DarkCard,
                contentColor = TeslaColors.TextPrimary
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setMainTab(0) },
                    icon = { Icon(Icons.Default.DirectionsCar, contentDescription = "제어") },
                    label = { Text("대시보드") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setMainTab(1) },
                    icon = { Icon(Icons.Default.BatteryChargingFull, contentDescription = "배터리/충전") },
                    label = { Text("충전/배터리") }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setMainTab(2) },
                    icon = { Icon(Icons.Default.Build, contentDescription = "소모품") },
                    label = { Text("소모품") }
                )
                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.setMainTab(3) },
                    icon = { Icon(Icons.Default.Map, contentDescription = "지도") },
                    label = { Text("카카오맵") }
                )
                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { viewModel.setMainTab(4) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "설정") },
                    label = { Text("설정") }
                )
            }
        },
        containerColor = TeslaColors.DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(vehicleState, viewModel)
                1 -> BatteryChargeScreen(vehicleState, degradationList, chargeRecords)
                2 -> ConsumablesScreen(consumables, vehicleState.odometerKm)
                3 -> KakaoMapScreen(appSettings.kakaoKey)
                4 -> SettingsScreen(appSettings) { newSettings -> viewModel.saveSettings(newSettings) }
            }
        }
    }
}

@Composable
fun DashboardScreen(state: VehicleState, viewModel: TeslaViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 차량 헤더 카피 & 상태
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TeslaColors.DarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Model Y Long Range", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TeslaColors.TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("최신 동기화: ${state.lastUpdated}", fontSize = 12.sp, color = TeslaColors.TextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusGauge("배터리", "${state.batteryLevel}%", TeslaColors.AccentGreen)
                    StatusGauge("주행 가능", "${state.batteryRangeKm} km", TeslaColors.AccentBlue)
                    StatusGauge("총 주행거리", "${state.odometerKm.toInt()} km", TeslaColors.TextPrimary)
                }
            }
        }

        // 제어 버튼 매트릭스
        Text("차량 제어", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeslaColors.TextPrimary)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ControlTile("도어 잠금", if (state.locked) "잠김" else "열림", Icons.Default.Lock, state.locked) { viewModel.toggleLock() }
            ControlTile("공조 장치", if (state.climateOn) "켜짐 (${state.insideTempC}°C)" else "꺼짐", Icons.Default.AcUnit, state.climateOn) { viewModel.toggleClimate() }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ControlTile("감시 모드", if (state.sentryMode) "활성화" else "비활성화", Icons.Default.Security, state.sentryMode) { viewModel.toggleSentry() }
            ControlTile("프렁크", if (state.frunkOpen) "열림" else "닫힘", Icons.Default.DirectionsCar, state.frunkOpen) { viewModel.toggleFrunk() }
        }
    }
}

@Composable
fun StatusGauge(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = TeslaColors.TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun RowScope.ControlTile(title: String, status: String, icon: ImageVector, isActive: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) TeslaColors.DarkBorder else TeslaColors.DarkCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = title, tint = if (isActive) TeslaColors.AccentAmber else TeslaColors.TextSecondary)
            Column {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TeslaColors.TextPrimary)
                Text(status, fontSize = 12.sp, color = TeslaColors.TextSecondary)
            }
        }
    }
}

@Composable
fun BatteryChargeScreen(
    state: VehicleState,
    degradationList: List<DegradationRecord>,
    chargeRecords: List<ChargeRecord>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("배터리 열화 및 충전 기록", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TeslaColors.TextPrimary)

        // 열화 기록 카트
        Card(colors = CardDefaults.cardColors(containerColor = TeslaColors.DarkCard), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("배터리 열화율 측정 이력", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeslaColors.AccentAmber)
                Spacer(modifier = Modifier.height(8.dp))
                degradationList.forEach { record ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(record.date, fontSize = 13.sp, color = TeslaColors.TextSecondary)
                        Text("${record.odometerKm.toInt()} km", fontSize = 13.sp, color = TeslaColors.TextPrimary)
                        Text("${record.degradationPercent}% 열화", fontSize = 13.sp, color = TeslaColors.AccentRed)
                    }
                }
            }
        }

        // 충전 히스토리
        Card(colors = CardDefaults.cardColors(containerColor = TeslaColors.DarkCard), shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("최근 충전 히스토리", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeslaColors.AccentBlue)
                Spacer(modifier = Modifier.height(8.dp))
                chargeRecords.forEach { record ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(record.location, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TeslaColors.TextPrimary)
                            Text("${record.costKrw}원", fontSize = 14.sp, color = TeslaColors.AccentGreen)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${record.date} (${record.chargeType})", fontSize = 12.sp, color = TeslaColors.TextSecondary)
                            Text("+${record.addedKwh} kWh (${record.startSoc}% → ${record.endSoc}%)", fontSize = 12.sp, color = TeslaColors.TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsumablesScreen(items: List<ConsumableItem>, currentOdoKm: Double) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("소모품 관리", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TeslaColors.TextPrimary)
        items.forEach { item ->
            val usedKm = currentOdoKm - item.lastReplacedOdoKm
            val progress = (usedKm / item.replacementIntervalKm).toFloat().coerceIn(0f, 1f)

            Card(
                colors = CardDefaults.cardColors(containerColor = TeslaColors.DarkCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TeslaColors.TextPrimary)
                        Text("교체주기: ${item.replacementIntervalKm} km", fontSize = 12.sp, color = TeslaColors.TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (progress > 0.8f) TeslaColors.AccentRed else TeslaColors.AccentBlue,
                        trackColor = TeslaColors.DarkBorder
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("마지막 교체: ${item.lastReplacedDate} (${item.lastReplacedOdoKm.toInt()} km)", fontSize = 12.sp, color = TeslaColors.TextSecondary)
                }
            }
        }
    }
}

@Composable
fun KakaoMapScreen(kakaoKey: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    val webSettings = this.settings
                    webSettings.javaScriptEnabled = true
                    webSettings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL(
                        "https://dapi.kakao.com",
                        """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="utf-8"/>
                            <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=$kakaoKey"></script>
                            <style>html, body, #map {width:100%;height:100%;margin:0;padding:0;background:#0D0E12;}</style>
                        </head>
                        <body>
                        <div id="map"></div>
                        <script>
                            var container = document.getElementById('map');
                            var options = { center: new kakao.maps.LatLng(37.5665, 126.9780), level: 3 };
                            var map = new kakao.maps.Map(container, options);
                        </script>
                        </body>
                        </html>
                        """.trimIndent(),
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SettingsScreen(currentSettings: AppSettings, onSave: (AppSettings) -> Unit) {
    var supabaseUrl by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(currentSettings.kakaoKey) }
    var teslaToken by remember { mutableStateOf(currentSettings.teslaToken) }
    var vehicleId by remember { mutableStateOf(currentSettings.vehicleId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("보안 설정 (AES-256 암호화 저장)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TeslaColors.AccentAmber)

        OutlinedTextField(
            value = supabaseUrl,
            onValueChange = { supabaseUrl = it },
            label = { Text("Supabase URL") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = supabaseKey,
            onValueChange = { supabaseKey = it },
            label = { Text("Supabase Key") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = kakaoKey,
            onValueChange = { kakaoKey = it },
            label = { Text("Kakao Map JavaScript Key") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = teslaToken,
            onValueChange = { teslaToken = it },
            label = { Text("Tesla Refresh Token") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = vehicleId,
            onValueChange = { vehicleId = it },
            label = { Text("Tesla Vehicle ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = TeslaColors.AccentAmber)
        ) {
            Text("설정 저장 및 암호화", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
