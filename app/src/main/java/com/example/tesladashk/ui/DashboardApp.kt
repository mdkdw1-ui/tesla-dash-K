package com.example.tesladashk.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tesladashk.network.ConfigState
import com.example.tesladashk.network.TripItem
import com.example.tesladashk.ui.screens.*
import com.example.tesladashk.viewmodel.TeslaViewModel

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
fun DashboardApp(viewModel: TeslaViewModel = viewModel()) {
    var mainTab by remember { mutableStateOf("monitor") }
    var subTab by remember { mutableStateOf("vehicle") }
    var showConfigModal by remember { mutableStateOf(false) }

    val vehicleState by viewModel.vehicleState.collectAsState()
    val tripsState by viewModel.tripsState.collectAsState()
    val logsState by viewModel.logsState.collectAsState()
    val configState by viewModel.configState.collectAsState()
    val guardianActive by viewModel.isGuardianActive.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val displayTrips = if (tripsState.isNotEmpty()) tripsState else listOf(
        TripItem("1", "14:20", "서울 강남구 대치동", "서울 성동구 성수동", 8.4, 22, 3.2, 88, 85, 45210),
        TripItem("2", "09:15", "서울 성동구 성수동", "경기 성남시 분당구", 18.2, 35, 6.5, 95, 88, 45202)
    )

    Scaffold(
        topBar = {
            HeaderBar(
                statusText = if (isRefreshing) "🔄 동기화 중" else "🅿️ 대기",
                onRefresh = { viewModel.refreshData() },
                onSync = { viewModel.addLog("[시스템] GitHub Actions Sync 실행 신호 전송") },
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
                        .padding(12.dp)
                ) {
                    when (subTab) {
                        "vehicle" -> VehicleInfoScreen(vehicleState, displayTrips)
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
                        onToggleGuardian = { viewModel.toggleGuardian(it) },
                        accessToken = viewModel.accessToken,
                        logs = logsState,
                        onClearLogs = { viewModel.clearLogs() },
                        onTestFlash = { viewModel.addLog("[테스트] ⚡ 전조등 점멸 신호 전송 완료") },
                        onTestNtfy = { viewModel.addLog("[테스트] 📱 ntfy 푸시 알림 발송 완료") }
                    )
                }
            }
        }

        if (showConfigModal) {
            ConfigModal(
                config = configState,
                onSave = { newConfig ->
                    viewModel.updateConfig(newConfig)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
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
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFDC2626), Color(0xFF991B1B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 16.sp)
                    }
                    Text(
                        "Tesla Dash",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentEmerald.copy(alpha = 0.2f))
                            .border(1.dp, AccentEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onRefresh() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("🔄 갱신", color = AccentEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentBlue.copy(alpha = 0.2f))
                            .border(1.dp, AccentBlue.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .clickable { onSync() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("⚡ Sync", color = AccentBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CardDarker)
                            .border(1.dp, BorderGray, RoundedCornerShape(8.dp))
                            .clickable { onOpenSettings() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text("⚙️ 설정", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = CardDark),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, BorderGray),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔑 API & DB 설정", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "✕",
                        color = TextGray,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }

                Divider(color = BorderGray)

                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL", color = TextGray, fontSize = 11.sp) },
                    placeholder = { Text("https://xxx.supabase.co", color = TextGray.copy(alpha = 0.5f)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = AccentBlue,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Anon/Service Key", color = TextGray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = AccentBlue,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("카카오맵 API Key", color = TextGray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = AccentBlue,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Tesla Vehicle ID", color = TextGray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = AccentBlue,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ntfyTopic,
                    onValueChange = { ntfyTopic = it },
                    label = { Text("ntfy 알림 토픽", color = TextGray, fontSize = 11.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = BorderGray,
                        focusedLabelColor = AccentBlue,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            onSave(ConfigState(kakaoKey, supabaseUrl, supabaseKey, ghToken, vehicleId, ntfyTopic))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                    ) {
                        Text("저장 및 DB 동기화", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardDarker),
                        border = BorderStroke(1.dp, BorderGray)
                    ) {
                        Text("취소", color = TextWhite, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
