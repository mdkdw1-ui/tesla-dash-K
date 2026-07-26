package com.mdkdw1.ui.tesla

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch

// ==========================================
// 1. 커스텀 다크 테마 색상 (Theme Colors)
// ==========================================
val BgDark = Color(0xFF0D0E12)
val CardDark = Color(0xFF161820)
val CardBorder = Color(0xFF262936)
val AccentRed = Color(0xFFE82127)
val AccentGreen = Color(0xFF10B981)
val AccentBlue = Color(0xFF3B82F6)
val AccentIndigo = Color(0xFF818CF8)
val AccentAmber = Color(0xFFD97706)
val TextLight = Color(0xFFF3F4F6)
val TextGray = Color(0xFF9CA3AF)

// ==========================================
// 2. 메인 Hub 메인 화면 (MainHubScreen)
// ==========================================
@Composable
fun MainHubScreen(
    repository: TeslaHubRepository,
    settingsManager: SecureSettingsManager
) {
    var mainTab by remember { mutableStateOf("monitor") } // "monitor" or "guardian"
    var subTab by remember { mutableStateOf("vehicle") } // "vehicle", "driving", "monthly", "battery"
    var showSettingsModal by remember { mutableStateOf(false) }

    val vehicleData by repository.vehicleData.collectAsState()
    val config = remember { settingsManager.getConfig() }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            // 상단 헤더 & 설정 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(AccentGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tesla Model Y",
                        color = TextLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButtonButton(icon = "⚙️", text = "설정", color = TextGray) {
                    showSettingsModal = true
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 탭 (테슬라 모니터 / 감시 가디언)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(CardDark, RoundedCornerShape(12.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                TabSelectButton(
                    text = "테슬라 모니터",
                    isSelected = mainTab == "monitor",
                    activeColor = AccentBlue,
                    modifier = Modifier.weight(1f)
                ) { mainTab = "monitor" }

                Spacer(modifier = Modifier.width(4.dp))

                TabSelectButton(
                    text = "🛡️ 감시 가디언",
                    isSelected = mainTab == "guardian",
                    activeColor = AccentIndigo,
                    modifier = Modifier.weight(1f)
                ) { mainTab = "guardian" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 메인 패널 화면 전환
            Box(modifier = Modifier.weight(1f)) {
                if (mainTab == "monitor") {
                    MonitorMainPanel(
                        subTab = subTab,
                        onSubTabChange = { subTab = it },
                        data = vehicleData,
                        repository = repository,
                        kakaoKey = config.kakaoKey
                    )
                } else {
                    GuardianMainPanel(data = vehicleData, repository = repository)
                }
            }
        }
    }

    if (showSettingsModal) {
        ConfigSettingsModal(
            settingsManager = settingsManager,
            onDismiss = { showSettingsModal = false }
        )
    }
}

// ==========================================
// 3. 테슬라 모니터 패널 (MonitorMainPanel)
// ==========================================
@Composable
fun MonitorMainPanel(
    subTab: String,
    onSubTabChange: (String) -> Unit,
    data: VehicleStateData,
    repository: TeslaHubRepository,
    kakaoKey: String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // 서브 탭 Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(10.dp))
                .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SubTabItem("차량 정보", subTab == "vehicle") { onSubTabChange("vehicle") }
            SubTabItem("주행 지도", subTab == "driving") { onSubTabChange("driving") }
            SubTabItem("월간 리포트", subTab == "monthly") { onSubTabChange("monthly") }
            SubTabItem("배터리", subTab == "battery") { onSubTabChange("battery") }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(modifier = Modifier.weight(1f)) {
            when (subTab) {
                "vehicle" -> VehicleInfoSubTab(data = data, repository = repository)
                "driving" -> KakaoMapSubTab(data = data, kakaoApiKey = kakaoKey)
                "monthly" -> MonthlyReportSubTab()
                "battery" -> BatteryHealthSubTab(repository = repository)
            }
        }
    }
}

@Composable
fun VehicleInfoSubTab(data: VehicleStateData, repository: TeslaHubRepository) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 상태 메인 카드
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(data.statusText, color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("기준: ${data.lastUpdated}", color = TextGray, fontSize = 11.sp)
                }
                HorizontalDivider(color = CardBorder)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("배터리 잔량", color = TextGray, fontSize = 11.sp)
                        Text("${data.batteryLevel}%", color = AccentGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("예상 주행거리", color = TextGray, fontSize = 11.sp)
                        Text("${data.batteryRangeKm} km", color = TextLight, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 빠른 원격 제어 버튼
        Text("빠른 제어", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { repository.toggleLock() },
                colors = ButtonDefaults.buttonColors(containerColor = if (data.isLocked) AccentBlue else AccentRed),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (data.isLocked) "🔒 잠금됨" else "🔓 열림", fontSize = 12.sp)
            }
            Button(
                onClick = { repository.toggleSentry() },
                colors = ButtonDefaults.buttonColors(containerColor = if (data.isSentryOn) AccentIndigo else CardDark),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (data.isSentryOn) "👁️ 센트리 ON" else "👁️ 센트리 OFF", fontSize = 12.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    repository.triggerFrunk()
                    Toast.makeText(context, "프렁크 오픈 명령 전송", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("📦 프렁크", color = TextLight, fontSize = 12.sp)
            }
            Button(
                onClick = {
                    repository.triggerTrunk()
                    Toast.makeText(context, "트렁크 오픈 명령 전송", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("🧳 트렁크", color = TextLight, fontSize = 12.sp)
            }
            Button(
                onClick = { repository.toggleClimate() },
                colors = ButtonDefaults.buttonColors(containerColor = if (data.isClimateOn) AccentGreen else CardDark),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (data.isClimateOn) "❄️ 공조 ON" else "❄️ 공조 OFF", fontSize = 12.sp)
            }
        }
    }
}

// ==========================================
// 4. 감시 가디언 패널 (GuardianMainPanel)
// ==========================================
@Composable
fun GuardianMainPanel(data: VehicleStateData, repository: TeslaHubRepository) {
    val context = LocalContext.current
    val alertMessage = repository.checkGuardianSecurityAlert()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🛡️ 감시 가디언 보안 상태", color = TextLight, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(color = CardBorder)

                if (alertMessage != null) {
                    Text(alertMessage, color = AccentRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Text("✅ 차량 보안 시스템 정상 작동 중", color = AccentGreen, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("센트리 모드", color = TextGray, fontSize = 12.sp)
                    Text(if (data.isSentryOn) "활성화" else "비활성화", color = if (data.isSentryOn) AccentGreen else AccentRed, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("도어 잠금 상태", color = TextGray, fontSize = 12.sp)
                    Text(if (data.isLocked) "잠김" else "잠금 해제됨", color = if (data.isLocked) AccentGreen else AccentRed, fontSize = 12.sp)
                }
            }
        }

        Button(
            onClick = {
                repository.triggerFlashLights()
                Toast.makeText(context, "비상 전조등 점등 테스트 실시", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("🚨 비상등 점등 경고 테스트", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 5. 배터리 열화율 커스텀 차트 서브탭
// ==========================================
@Composable
fun BatteryHealthSubTab(repository: TeslaHubRepository) {
    val records by repository.batteryRecords.collectAsState()

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("🔋 배터리 열화율 트렌드 (%)", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = CardBorder)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(BgDark, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (records.isEmpty()) return@Canvas
                    val width = size.width
                    val height = size.height

                    val minVal = 95f
                    val maxVal = 100f

                    val path = Path()
                    records.forEachIndexed { index, record ->
                        val x = (width / (records.size - 1)) * index
                        val y = height - ((record.healthPercent - minVal) / (maxVal - minVal)) * height

                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            path.lineTo(x, y)
                        }
                    }

                    drawPath(
                        path = path,
                        color = AccentGreen,
                        style = Stroke(width = 4f)
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                records.forEach {
                    Text(it.month, color = TextGray, fontSize = 10.sp)
                }
            }
        }
    }
}

// ==========================================
// 6. 카카오맵 지도 서브탭 (Kakao Map WebView Bridge)
// ==========================================
@Composable
fun KakaoMapSubTab(data: VehicleStateData, kakaoApiKey: String) {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=$kakaoApiKey"></script>
            <style>
                html, body, #map {width:100%;height:100%;margin:0;padding:0;background:#0d0e12;}
            </style>
        </head>
        <body>
        <div id="map"></div>
        <script>
            window.onload = function() {
                var container = document.getElementById('map');
                var options = {
                    center: new kakao.maps.LatLng(${data.lat}, ${data.lng}),
                    level: 3
                };
                var map = new kakao.maps.Map(container, options);
                var marker = new kakao.maps.Marker({
                    position: new kakao.maps.LatLng(${data.lat}, ${data.lng})
                });
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
            .fillMaxSize()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
    )
}

// ==========================================
// 7. 월간 리포트 서브탭 (Monthly Report)
// ==========================================
@Composable
fun MonthlyReportSubTab() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📊 월간 주행 요약", color = TextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(color = CardBorder)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("총 주행 거리", color = TextGray, fontSize = 12.sp)
                Text("1,280 km", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("평균 전비", color = TextGray, fontSize = 12.sp)
                Text("152 Wh/km", color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// 8. 설정 모달 다이얼로그 (ConfigSettingsModal)
// ==========================================
@Composable
fun ConfigSettingsModal(
    settingsManager: SecureSettingsManager,
    onDismiss: () -> Unit
) {
    val currentConfig = settingsManager.getConfig()
    var supabaseUrl by remember { mutableStateOf(currentConfig.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentConfig.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(currentConfig.kakaoKey) }
    var teslaToken by remember { mutableStateOf(currentConfig.teslaToken) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardDark,
        title = {
            Text("⚙️ API 키 및 암호화 설정", color = TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )
                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Anon Key", color = TextGray) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )
                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("Kakao Map JavaScript Key", color = TextGray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )
                OutlinedTextField(
                    value = teslaToken,
                    onValueChange = { teslaToken = it },
                    label = { Text("Tesla Refresh Token", color = TextGray) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextLight,
                        unfocusedTextColor = TextLight
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    settingsManager.saveConfig(supabaseUrl, supabaseKey, kakaoKey, teslaToken)
                    Toast.makeText(context, "AES-256 암호화 저장 완료", Toast.LENGTH_SHORT).show()
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
// 9. 헬퍼 UI 컴포넌트
// ==========================================
@Composable
fun IconButtonButton(icon: String, text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.2f)),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("$icon $text", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TabSelectButton(text: String, isSelected: Boolean, activeColor: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isSelected) activeColor else CardDark, RoundedCornerShape(10.dp))
            .border(1.dp, if (isSelected) activeColor else CardBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) TextLight else TextGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RowScope.SubTabItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .padding(2.dp)
            .background(if (isSelected) AccentBlue else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (isSelected) TextLight else TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
