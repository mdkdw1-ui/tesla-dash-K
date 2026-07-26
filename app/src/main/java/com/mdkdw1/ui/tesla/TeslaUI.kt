package com.mdkdw1.ui.tesla.ui

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.mdkdw1.ui.tesla.*
import kotlinx.coroutines.launch

val DarkBackground = Color(0xFF0D0E12)
val CardDark = Color(0xFF161820)
val BorderColor = Color(0xFF262936)
val AmberBorder = Color(0xFFD97706)
val AccentBlue = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val settingsManager = remember { EncryptedSettingsManager(context) }
    val repository = remember { TeslaRepository(settingsManager) }

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: 모니터, 1: 가디언
    var selectedSubTab by remember { mutableIntStateOf(0) }  // 0: 차량, 1: 지도, 2: 리포트, 3: 배터리
    
    var vehicleState by remember { mutableStateOf(VehicleState()) }
    var dailyTrip by remember { mutableStateOf(DailyTripRecord()) }
    var showSettingsModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scope.launch {
            vehicleState = repository.fetchVehicleState()
            dailyTrip = repository.fetchLatestDailyTrip()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFDC2626)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("⚡", fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Tesla Command Hub", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Monitor & Guardian", fontSize = 9.sp, color = Color.Gray)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            vehicleState = repository.fetchVehicleState()
                            dailyTrip = repository.fetchLatestDailyTrip()
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "갱신", tint = Color(0xFF10B981))
                    }
                    IconButton(onClick = { showSettingsModal = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "설정", tint = Color.LightGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF12141C))
            )
        },
        containerColor = DarkBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // 메인 탭 (모니터 / 가디언)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { selectedMainTab = 0 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedMainTab == 0) AccentBlue else CardDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("테슬라 모니터")
                }
                Button(
                    onClick = { selectedMainTab = 1 },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedMainTab == 1) AccentBlue else CardDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("감시 가디언")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedMainTab == 0) {
                // 서브 탭
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardDark, RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val subTabs = listOf("차량 정보", "주행 지도", "월간 리포트", "배터리")
                    subTabs.forEachIndexed { idx, title ->
                        Button(
                            onClick = { selectedSubTab = idx },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedSubTab == idx) AccentBlue else Color.Transparent,
                                contentColor = if (selectedSubTab == idx) Color.White else Color.Gray
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (selectedSubTab) {
                    0 -> VehicleInfoSubPanel(vehicleState, dailyTrip)
                    1 -> KakaoMapView(settingsManager.getConfig().kakaoMapKey)
                    2 -> Text("📊 월간 리포트 기능 제공 영역", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    3 -> Text("🔋 배터리 열화율 및 상태 그래프 영역", color = Color.Gray, modifier = Modifier.padding(16.dp))
                }
            } else {
                Text("🛡️ 감시 가디언 제어 영역", color = Color.Gray, modifier = Modifier.padding(16.dp))
            }
        }
    }

    if (showSettingsModal) {
        SettingsDialog(
            manager = settingsManager,
            onDismiss = { showSettingsModal = false }
        )
    }
}

@Composable
fun VehicleInfoSubPanel(state: VehicleState, trip: DailyTripRecord) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // 최근 차량 상태 카드
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("현재 상태: ${state.status}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("🔋 ${state.batteryLevel}% | 📍 ${state.odometer} km", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // 주차 및 감시 상태 카드
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            border = androidx.compose.foundation.BorderStroke(1.dp, AmberBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🅿️", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("주차 중", color = AmberBorder, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("마지막 갱신: ${state.lastUpdated}", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }

        // 최근 운행일 기록 카드
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("⚡ 최근 운행일 전체 기록", color = Color(0xFF818CF8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("${trip.efficiency}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF818CF8))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("km/kWh (평균)", fontSize = 11.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column { Text("총 이동거리", fontSize = 10.sp, color = Color.Gray); Text("${trip.totalDistance} km", fontSize = 12.sp, color = Color.White) }
                    Column { Text("사용 배터리", fontSize = 10.sp, color = Color.Gray); Text("${trip.usedBattery}%", fontSize = 12.sp, color = Color.White) }
                    Column { Text("운전 시간", fontSize = 10.sp, color = Color.Gray); Text("${trip.driveTimeMin} 분", fontSize = 12.sp, color = Color.White) }
                    Column { Text("사용 에너지", fontSize = 10.sp, color = Color.Gray); Text("${trip.usedKwh} kWh", fontSize = 12.sp, color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun KakaoMapView(apiKey: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl("https://m.map.kakao.com")
                }
            },
            update = { webView -> webView.loadUrl("https://m.map.kakao.com") }
        )
    }
}

@Composable
fun SettingsDialog(manager: EncryptedSettingsManager, onDismiss: () -> Unit) {
    val currentConfig = remember { manager.getConfig() }
    var supabaseUrl by remember { mutableStateOf(currentConfig.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentConfig.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(currentConfig.kakaoMapKey) }
    var githubToken by remember { mutableStateOf(currentConfig.githubToken) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("⚙️ 설정 및 API 키 (암호화 저장)") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = supabaseUrl, onValueChange = { supabaseUrl = it }, label = { Text("Supabase URL") })
                OutlinedTextField(value = supabaseKey, onValueChange = { supabaseKey = it }, label = { Text("Supabase Key") })
                OutlinedTextField(value = kakaoKey, onValueChange = { kakaoKey = it }, label = { Text("Kakao Map Key") })
                OutlinedTextField(value = githubToken, onValueChange = { githubToken = it }, label = { Text("GitHub Token") })
            }
        },
        confirmButton = {
            Button(onClick = {
                manager.saveConfig(AppConfig(supabaseUrl, supabaseKey, kakaoKey, githubToken))
                onDismiss()
            }) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}
