package com.mdkdw1.ui.tesla

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

// ==========================================
// 다크 모드 디자인 테마 색상 정의
// ==========================================
val DarkBackground = Color(0xFF0D0E12)
val CardDark = Color(0xFF161820)
val CardBorder = Color(0xFF262936)
val TextLight = Color(0xFFF3F4F6)
val TextGray = Color(0xFF9CA3AF)
val AccentBlue = Color(0xFF3B82F6)
val AccentRed = Color(0xFFEF4444)
val AccentGreen = Color(0xFF10B981)
val AccentAmber = Color(0xFFF59E0B)
val AccentIndigo = Color(0xFF6366F1)
val AmberBorder = Color(0xFFD97706)

// ==========================================
// 메인 Hub 스크린 컴포저블
// ==========================================
@Composable
fun MainHubScreen(
    repository: TeslaHubRepository,
    settingsManager: SecureSettingsManager
) {
    val vehicleData by repository.vehicleData.collectAsState()
    val batteryRecords by repository.batteryRecords.collectAsState()

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: 테슬라 모니터, 1: 감시 가디언
    var selectedSubTab by remember { mutableIntStateOf(0) }  // 0: 차량정보, 1: 주행지도, 2: 월간리포트, 3: 배터리그래프
    var showSettingsModal by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. 상단 타이틀 바 & 설정 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚡ Tesla Command Hub", color = TextLight, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(AccentGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("온라인", color = AccentGreen, fontSize = 12.sp)
                }
                IconButton(onClick = { showSettingsModal = true }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 메인 탭 셀렉터 (테슬라 모니터 / 🛡️ 감시 가디언)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(CardDark, RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabButton(
                    text = "테슬라 모니터",
                    isSelected = selectedMainTab == 0,
                    activeColor = AccentBlue,
                    modifier = Modifier.weight(1f)
                ) { selectedMainTab = 0 }

                Spacer(modifier = Modifier.width(4.dp))

                TabButton(
                    text = "🛡️ 감시 가디언",
                    isSelected = selectedMainTab == 1,
                    activeColor = AccentIndigo,
                    modifier = Modifier.weight(1f)
                ) { selectedMainTab = 1 }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 메인 탭 전환 패널
            if (selectedMainTab == 0) {
                // 서브 탭 (차량 정보, 주행 지도, 월간 리포트, 배터리 그래프)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val subTabs = listOf("차량 정보", "주행 지도", "월간 리포트", "배터리 그래프")
                    subTabs.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedSubTab == index,
                            onClick = { selectedSubTab = index },
                            label = { Text(title, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AccentBlue,
                                selectedLabelColor = TextLight,
                                containerColor = CardDark,
                                labelColor = TextGray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedSubTab) {
                    0 -> VehicleInfoSubTab(vehicleData, repository)
                    1 -> KakaoMapWebView(vehicleData.lat, vehicleData.lng, settingsManager.getConfig().kakaoKey)
                    2 -> MonthlyReportSubTab()
                    3 -> BatteryHealthSubTab(batteryRecords)
                }
            } else {
                GuardianTabScreen(vehicleData, repository)
            }
        }
    }

    if (showSettingsModal) {
        SettingsDialog(
            settingsManager = settingsManager,
            onDismiss = { showSettingsModal = false }
        )
    }
}

// ==========================================
// 차량 정보 서브탭
// ==========================================
@Composable
fun VehicleInfoSubTab(data: VehicleStateData, repository: TeslaHubRepository) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 차량 상태 카드
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("차량 상태", color = TextGray, fontSize = 12.sp)
                Text(data.statusText, color = TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("배터리 잔량", color = TextGray, fontSize = 11.sp)
                        Text("${data.batteryLevel}%", color = AccentGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("예상 주행거리", color = TextGray, fontSize = 11.sp)
                        Text("${data.batteryRangeKm} km", color = TextLight, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 빠른 제어 버튼 그룹
        Text("빠른 차량 제어", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { repository.toggleLock() },
                colors = ButtonDefaults.buttonColors(containerColor = if (data.isLocked) AccentBlue else AccentRed),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (data.isLocked) "도어 잠김" else "도어 해제됨")
            }

            Button(
                onClick = { repository.toggleClimate() },
                colors = ButtonDefaults.buttonColors(containerColor = if (data.isClimateOn) AccentGreen else CardDark),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (data.isClimateOn) "공조 ON (${data.insideTemp}°C)" else "공조 OFF")
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { repository.triggerFrunk() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("프렁크 열기", color = TextLight)
            }
            OutlinedButton(
                onClick = { repository.triggerTrunk() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("트렁크 열기", color = TextLight)
            }
        }
    }
}

// ==========================================
// 감시 가디언 탭
// ==========================================
@Composable
fun GuardianTabScreen(data: VehicleStateData, repository: TeslaHubRepository) {
    val context = LocalContext.current
    val alertMessage = repository.checkGuardianSecurityAlert()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (alertMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF3F1D1D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AccentRed, RoundedCornerShape(12.dp))
            ) {
                Text(
                    text = alertMessage,
                    color = AccentRed,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AmberBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🛡️ 감시 가디언 센트리 모드", color = AccentAmber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("센트리 모드 상태", color = TextGray, fontSize = 12.sp)
                    Text(if (data.isSentryOn) "가동 중" else "중지됨", color = if (data.isSentryOn) AccentGreen else AccentRed, fontSize = 12.sp)
                }
            }
        }

        Button(
            onClick = {
                repository.triggerFlashLights()
                Toast.makeText(context, "비상 전조등 점등 명령 전송", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("🚨 경고 비상등 점등 테스트", color = DarkBackground, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 배터리 열화율 차트 서브탭 (Canvas Line Chart)
// ==========================================
@Composable
fun BatteryHealthSubTab(records: List<BatteryRecord>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("배터리 수명 및 열화율 추이 (%)", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (records.isNotEmpty()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    val maxVal = 100f
                    val minVal = 95f

                    val points = records.mapIndexed { index, record ->
                        val x = index * (width / (records.size - 1))
                        val y = height - ((record.healthPercent - minVal) / (maxVal - minVal) * height)
                        Offset(x, y)
                    }

                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                    }

                    drawPath(
                        path = path,
                        color = AccentBlue,
                        style = Stroke(width = 4f)
                    )

                    for (p in points) {
                        drawCircle(color = AccentGreen, radius = 6f, center = p)
                    }
                }
            }
        }
    }
}

// ==========================================
// 월간 리포트 서브탭
// ==========================================
@Composable
fun MonthlyReportSubTab() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📊 이번 달 주행 및 충전 리포트", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("총 주행 거리", color = TextGray, fontSize = 12.sp)
                Text("1,248 km", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("평균 전비", color = TextGray, fontSize = 12.sp)
                Text("152 Wh/km", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 카카오맵 WebView 연동
// ==========================================
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KakaoMapWebView(lat: Double, lng: Double, kakaoApiKey: String) {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=$kakaoApiKey"></script>
            <style>
                html, body { margin: 0; padding: 0; width: 100%; height: 100%; background-color: #0D0E12; }
                #map { width: 100%; height: 100%; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                window.onload = function() {
                    var container = document.getElementById('map');
                    var options = { center: new kakao.maps.LatLng($lat, $lng), level: 3 };
                    var map = new kakao.maps.Map(container, options);
                    var marker = new kakao.maps.Marker({ position: new kakao.maps.LatLng($lat, $lng) });
                    marker.setMap(map);
                };
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadDataWithBaseURL("https://dapi.kakao.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
    )
}

// ==========================================
// 설정 다이얼로그 (AES-256 암호화 저장)
// ==========================================
@Composable
fun SettingsDialog(
    settingsManager: SecureSettingsManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val config = remember { settingsManager.getConfig() }

    var supabaseUrl by remember { mutableStateOf(config.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(config.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(config.kakaoKey) }
    var teslaToken by remember { mutableStateOf(config.teslaToken) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = { Text("⚙️ API 및 보안 설정 (AES-256 암호화)", color = TextLight, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL", color = TextGray) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key", color = TextGray) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("Kakao Map App Key", color = TextGray) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = teslaToken,
                    onValueChange = { teslaToken = it },
                    label = { Text("Tesla Refresh Token", color = TextGray) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    settingsManager.saveConfig(supabaseUrl, supabaseKey, kakaoKey, teslaToken)
                    Toast.makeText(context, "AES-256 보안 저장소에 암호화 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextGray)
            }
        }
    )
}

// ==========================================
// UI 공통 버튼 컴포넌트
// ==========================================
@Composable
fun TabButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) TextLight else TextGray,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
