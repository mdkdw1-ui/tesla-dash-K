package com.example.tesladashk

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// Visual Palette matching HTML Design
val DarkBg = Color(0xFF0D0E12)
val CardDark = Color(0xFF161820)
val CardDarker = Color(0xFF0F1015)
val HeaderBg = Color(0xFF12141C)
val BorderGray = Color(0xFF262936)
val AccentBlue = Color(0xFF3B82F6)
val AccentEmerald = Color(0xFF10B981)
val AccentAmber = Color(0xFFD97706)
val AccentRed = Color(0xFFEF4444)
val AccentIndigo = Color(0xFF6366F1)
val AccentTeal = Color(0xFF14B8A6)
val TextWhite = Color(0xFFF3F4F6)
val TextGray = Color(0xFF9CA3AF)
val LogGreen = Color(0xFF34D399)

data class VehicleState(
    val statusText: String = "현재: 주차 중",
    val batteryLevel: Int = 85,
    val odometer: Int = 45210,
    val outsideTemp: Float = 24.0f,
    val parkDurationStr: String = "주차 중 2시간 15분",
    val tpmsFl: Int = 42,
    val tpmsFr: Int = 42,
    val tpmsRl: Int = 41,
    val tpmsRr: Int = 41
)

data class TripItem(
    val id: String,
    val timeStr: String,
    val startDong: String,
    val endDong: String,
    val moveKm: Double,
    val durationMin: Int,
    val useBattery: Double,
    val startBat: Int,
    val endBat: Int,
    val odometer: Int
)

data class ConfigState(
    val kakaoKey: String = "159c5d7588efc5939d431f005912f9f3",
    val supabaseUrl: String = "https://xxx.supabase.co",
    val supabaseKey: String = "",
    val ghToken: String = "",
    val vehicleId: String = "3744141651867089",
    val ntfyTopic: String = "MJYAz6ZyjXiujaTDpJ"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeslaDashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBg
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun TeslaDashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = DarkBg,
            surface = CardDark,
            primary = AccentBlue,
            secondary = AccentEmerald
        ),
        content = content
    )
}

@Composable
fun MainScreen() {
    var mainTab by remember { mutableStateOf("monitor") }
    var subTab by remember { mutableStateOf("vehicle") }
    var showConfigModal by remember { mutableStateOf(false) }
    
    val vehicleState by remember { mutableStateOf(VehicleState()) }
    var guardianActive by remember { mutableStateOf(false) }
    val accessToken by remember { mutableStateOf("eyJhbGciOiJSUzI1NiIs...") }
    var logs by remember { mutableStateOf(listOf("[시스템] 테슬라 커맨드 허브 준비 완료")) }
    var config by remember { mutableStateOf(ConfigState()) }

    val sampleTrips = remember {
        listOf(
            TripItem("1", "14:20", "서울 강남구 대치동", "서울 성동구 성수동", 8.4, 22, 3.2, 88, 85, 45210),
            TripItem("2", "09:15", "서울 성동구 성수동", "경기 성남시 분당구", 18.2, 35, 6.5, 95, 88, 45202)
        )
    }

    Scaffold(
        topBar = {
            HeaderBar(
                statusText = "🅿️ 대기",
                onRefresh = { logs = logs + "[시스템] 데이터 수동 갱신 완료" },
                onSync = { logs = logs + "[시스템] GitHub Actions Sync 실행 신호 전송" },
                onOpenSettings = { showConfigModal = true }
            )
        },
        containerColor = DarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MainTabBar(
                selectedTab = mainTab,
                onTabSelected = { mainTab = it }
            )

            if (mainTab == "monitor") {
                SubTabBar(
                    selectedSubTab = subTab,
                    onSubTabSelected = { subTab = it }
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when (subTab) {
                        "vehicle" -> VehicleInfoScreen(vehicleState, sampleTrips)
                        "driving" -> DrivingMapPlaceholder()
                        "monthly" -> MonthlyReportScreen()
                        "battery" -> BatteryScreen()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    GuardianScreen(
                        isGuardianActive = guardianActive,
                        onToggleGuardian = {
                            guardianActive = it
                            logs = logs + if (it) "✅ 감시 모드 가디언 가동 시작" else "🛑 감시 모드 가디언 중지됨"
                        },
                        accessToken = accessToken,
                        logs = logs,
                        onClearLogs = { logs = listOf("[시스템] 로그 초기화됨") },
                        onTestFlash = { logs = logs + "[테스트] ⚡ 전조등 점멸 신호 전송 완료" },
                        onTestNtfy = { logs = logs + "[테스트] 📱 ntfy 푸시 알림 발송 완료" }
                    )
                }
            }
        }

        if (showConfigModal) {
            ConfigModal(
                config = config,
                onSave = { newConfig ->
                    config = newConfig
                    showConfigModal = false
                },
                onDismiss = { showConfigModal = false }
            )
        }
    }
}

@Composable
fun HeaderBar(
    statusText: String,
    onRefresh: () -> Unit,
    onSync: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Surface(
        color = HeaderBg,
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 18.sp)
                }
                Column {
                    Text(
                        "Tesla Command Hub",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        "Monitor & Guardian",
                        color = TextGray,
                        fontSize = 10.sp
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CardDarker)
                        .border(1.dp, BorderGray, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(statusText, color = TextGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = onRefresh,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentEmerald.copy(alpha = 0.2f),
                        contentColor = AccentEmerald
                    ),
                    border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("🔄 갱신", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSync,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentBlue.copy(alpha = 0.2f),
                        contentColor = AccentBlue
                    ),
                    border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("⚡ Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardDarker)
                        .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "설정", tint = TextGray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun MainTabBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    Surface(
        color = HeaderBg,
        border = BorderStroke(0.5.dp, BorderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onTabSelected("monitor") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "monitor") AccentBlue else CardDark,
                    contentColor = if (selectedTab == "monitor") Color.White else TextGray
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🚗")
                    Text("테슬라 모니터", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Button(
                onClick = { onTabSelected("guardian") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedTab == "guardian") AccentRed else CardDark,
                    contentColor = if (selectedTab == "guardian") Color.White else TextGray
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🛡️")
                    Text("감시 가디언", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun SubTabBar(selectedSubTab: String, onSubTabSelected: (String) -> Unit) {
    Surface(
        color = CardDark,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                "vehicle" to "차량 정보",
                "driving" to "주행 지도",
                "monthly" to "월간 리포트",
                "battery" to "배터리"
            )
            tabs.forEach { (key, label) ->
                val isSelected = selectedSubTab == key
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AccentBlue else Color.Transparent)
                        .clickable { onSubTabSelected(key) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        label,
                        color = if (isSelected) Color.White else TextGray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleInfoScreen(state: VehicleState, trips: List<TripItem>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AccentAmber)
                )
                Text(state.statusText, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🔋 ${state.batteryLevel}%", color = AccentEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("📍 ${String.format("%,d", state.odometer)} km", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AccentAmber),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🅿️", fontSize = 24.sp)
                Column {
                    Text(state.parkDurationStr, color = AccentAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("시각 정보 불러오는 중...", color = TextGray, fontSize = 11.sp)
                }
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("⚡ 최근 운행일 전체 기록", color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardDarker)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("7월 25일 (총 ${trips.size}건)", color = TextGray, fontSize = 10.sp)
                }
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("6.45", color = AccentIndigo, fontSize = 32.sp, fontWeight = FontWeight.Black)
                Text("km/kWh (평균)", color = TextGray, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
            }

            Divider(color = BorderGray)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricColumn("총 이동거리", "26.6 km")
                MetricColumn("총 사용배터리", "-9.7%")
                MetricColumn("총 운전시간", "57 분")
                MetricColumn("총 사용에너지", "-5.8 kWh")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardDarker)
                    .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("🌐 당일 이동 경로", color = AccentIndigo, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "서울 강남구 대치동 → 서울 성동구 성수동 → 경기 성남시 분당구",
                        color = TextWhite,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            trips.forEach { trip ->
                TripDetailCard(trip)
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🛞 타이어 공기압 (TPMS)", color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("🌡️ ${state.outsideTemp}°C", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TpmsBox("앞 왼쪽 (FL)", "${state.tpmsFl} psi", Modifier.weight(1f))
                TpmsBox("앞 오른쪽 (FR)", "${state.tpmsFr} psi", Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TpmsBox("뒤 왼쪽 (RL)", "${state.tpmsRl} psi", Modifier.weight(1f))
                TpmsBox("뒤 오른쪽 (RR)", "${state.tpmsRr} psi", Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGray, fontSize = 10.sp)
        Text(value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TpmsBox(label: String, pressure: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardDarker)
            .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = TextGray, fontSize = 10.sp)
            Text(pressure, color = AccentBlue, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun TripDetailCard(trip: TripItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardDarker)
            .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(trip.startDong, color = AccentBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("→", color = AccentIndigo, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(trip.endDong, color = AccentEmerald, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(
                "주행 ${trip.moveKm}km, 시간 ${trip.durationMin}분, 배터리 -${trip.useBattery}% (${trip.startBat}% → ${trip.endBat}%), 누적 ${String.format("%,d", trip.odometer)}km",
                color = TextGray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun GuardianScreen(
    isGuardianActive: Boolean,
    onToggleGuardian: (Boolean) -> Unit,
    accessToken: String,
    logs: List<String>,
    onClearLogs: () -> Unit,
    onTestFlash: () -> Unit,
    onTestNtfy: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AccentRed.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛡️", fontSize = 20.sp)
                    Column {
                        Text("감시 모드 가디언", color = AccentRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("백그라운드 감시 활성화", color = TextGray, fontSize = 10.sp)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (isGuardianActive) "기능 ON" else "기능 OFF",
                        color = if (isGuardianActive) AccentEmerald else TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = isGuardianActive,
                        onCheckedChange = onToggleGuardian,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AccentEmerald,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = BorderGray
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardDarker)
                    .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("가디언 감시 상태", color = TextGray, fontSize = 11.sp)
                        Text("마지막 확인: 방금 전", color = TextGray, fontSize = 10.sp)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isGuardianActive) AccentRed.copy(alpha = 0.2f) else CardDark)
                            .border(1.dp, if (isGuardianActive) AccentRed else BorderGray, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (isGuardianActive) "🛡️ 가디언 가동 중" else "🌙 대기 모드",
                            color = if (isGuardianActive) AccentRed else TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardDarker)
                    .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔑 Tesla Access Token", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(accessToken))
                                Toast.makeText(context, "✅ 토큰이 복사되었습니다!", Toast.LENGTH_SHORT).show()
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentBlue.copy(alpha = 0.15f), contentColor = AccentBlue),
                            border = BorderStroke(1.dp, AccentBlue.copy(alpha = 0.3f)),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("📋 토큰 복사", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        accessToken,
                        color = AccentEmerald,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTestFlash,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardDarker, contentColor = TextWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Text("⚡ 전조등 테스트", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onTestNtfy,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardDarker, contentColor = TextWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Text("📱 ntfy 테스트", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("실시간 작업 및 디버깅 로그", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "로그 지우기",
                        color = AccentBlue,
                        fontSize = 10.sp,
                        modifier = Modifier.clickable { onClearLogs() }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0B0C10))
                        .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    LazyColumn {
                        items(logs) { log ->
                            Text(log, color = LogGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyReportScreen() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("📊 월간 리포트", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("총 주행거리", "452.8 km", AccentEmerald, Modifier.weight(1f))
                MetricCard("주행한 날", "14 일", AccentBlue, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("충전 횟수", "8 회", AccentTeal, Modifier.weight(1f))
                MetricCard("추정 충전 비용", "32,400 원", AccentIndigo, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun BatteryScreen() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("🔋 배터리 상태 및 열화율", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricCard("최근 열화율", "98.5%", AccentIndigo, Modifier.weight(1f))
                MetricCard("100% 예상거리", "428 km", AccentEmerald, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DrivingMapPlaceholder() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardDark),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderGray),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🗺️", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("카카오맵 주행 경로 화면", color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardDarker)
            .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(label, color = TextGray, fontSize = 10.sp)
            Text(value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ConfigModal(
    config: ConfigState,
    onSave: (ConfigState) -> Unit,
    onDismiss: () -> Unit
) {
    var kakaoKey by remember { mutableStateOf(config.kakaoKey) }
    var supabaseUrl by remember { mutableStateOf(config.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(config.supabaseKey) }
    var ghToken by remember { mutableStateOf(config.ghToken) }
    var vehicleId by remember { mutableStateOf(config.vehicleId) }
    var ntfyTopic by remember { mutableStateOf(config.ntfyTopic) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🔑 설정 정보 입력", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("카카오맵 API Key", color = TextGray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL", color = TextGray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key", color = TextGray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Tesla Vehicle ID", color = TextGray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ntfyTopic,
                    onValueChange = { ntfyTopic = it },
                    label = { Text("ntfy 알림 토픽", color = TextGray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onSave(ConfigState(kakaoKey, supabaseUrl, supabaseKey, ghToken, vehicleId, ntfyTopic))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("저장 및 적용", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = BorderGray)
                    ) {
                        Text("취소", color = TextWhite)
                    }
                }
            }
        }
    }
}
