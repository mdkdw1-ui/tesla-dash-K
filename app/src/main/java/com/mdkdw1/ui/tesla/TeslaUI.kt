package com.mdkdw1.ui.tesla

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF9CA3AF)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    var mainTabState by remember { mutableIntStateOf(0) } // 0: 테슬라 모니터, 1: 감시 가디언
    var subTabState by remember { mutableIntStateOf(0) }  // 0: 차량정보, 1: 주행정보, 2: 월간리포트, 3: 배터리

    var isSyncing by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val vehicleState = viewModel.vehicleState
    val settings = viewModel.settings
    val driveLogs = viewModel.driveLogs
    val monthlyReport = viewModel.monthlyReport
    val batteryList = viewModel.batteryList

    val lastDriveLog = driveLogs.firstOrNull { !it.isCharging }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tesla Command Hub",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        isSyncing = true
                        viewModel.syncData()
                        isSyncing = false
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "데이터 동기화",
                            tint = if (isSyncing) AccentAmber else TextPrimary
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "설정",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkCard)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = mainTabState,
                containerColor = DarkCard,
                contentColor = TextPrimary,
                divider = { HorizontalDivider(color = DarkBorder) }
            ) {
                Tab(
                    selected = mainTabState == 0,
                    onClick = { mainTabState = 0 },
                    text = { Text("테슬라 모니터", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = mainTabState == 1,
                    onClick = { mainTabState = 1 },
                    text = { Text("감시 가디언", fontWeight = FontWeight.Bold) }
                )
            }

            if (mainTabState == 0) {
                ScrollableTabRow(
                    selectedTabIndex = subTabState,
                    containerColor = DarkCard,
                    contentColor = TextPrimary,
                    edgePadding = 16.dp,
                    divider = { HorizontalDivider(color = DarkBorder) }
                ) {
                    val subTabs = listOf("차량정보", "주행정보", "월간리포트", "배터리")
                    subTabs.forEachIndexed { index, title ->
                        Tab(
                            selected = subTabState == index,
                            onClick = { subTabState = index },
                            text = { Text(title, fontSize = 14.sp) }
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    when (subTabState) {
                        0 -> VehicleInfoTabContent(
                            vehicleState = vehicleState,
                            lastDriveLog = lastDriveLog,
                            driveLogs = driveLogs,
                            viewModel = viewModel
                        )
                        1 -> DriveInfoTabContent(driveLogs = driveLogs)
                        2 -> MonthlyReportTabContent(report = monthlyReport)
                        3 -> BatteryTabContent(batteryList = batteryList)
                    }
                }
            } else {
                GuardianTabContent()
            }
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentSettings = settings,
            onDismiss = { showSettingsDialog = false },
            onSave = { updatedSettings ->
                viewModel.saveSettings(updatedSettings)
                showSettingsDialog = false
            }
        )
    }
}

@Composable
fun VehicleInfoTabContent(
    vehicleState: VehicleState,
    lastDriveLog: DriveLogItem?,
    driveLogs: List<DriveLogItem>,
    viewModel: TeslaViewModel
) {
    val scrollState = rememberScrollState()

    val diffMillis = System.currentTimeMillis() - vehicleState.lastUpdatedTimestamp
    val parkedHours = diffMillis / (1000 * 60 * 60)
    val parkedMinutes = (diffMillis % (1000 * 60 * 60)) / (1000 * 60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // [1] 상단 차량상태
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("차량 상태 요약", color = TextSecondary, fontSize = 14.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusItem("상태", vehicleState.statusText, "테슬라 API")
                    StatusItem("배터리 잔량", "${vehicleState.batteryLevel}%", "Supabase")
                    StatusItem("총 주행거리", "${String.format("%.1f", vehicleState.odometer)} km", "Supabase")
                }
            }
        }

        // [2] 원격 제어
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("원격 제어", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.toggleLock() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.isLocked) AccentBlue else Color.DarkGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (vehicleState.isLocked) "잠김 해제" else "잠금")
                    }
                    Button(
                        onClick = { viewModel.toggleClimate() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.climateOn) AccentAmber else Color.DarkGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (vehicleState.climateOn) "공조 끄기" else "공조 켜기")
                    }
                    Button(
                        onClick = { viewModel.toggleCharging() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.isCharging) Color.Red else Color.DarkGray),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (vehicleState.isCharging) "충전 중지" else "충전 시작")
                    }
                }
            }
        }

        // [3] 주차 시간
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.LocalParking, contentDescription = null, tint = AccentBlue)
                    Text("주차 시간", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
                Text("${parkedHours}시간 ${parkedMinutes}분 동안 주차 중", color = AccentAmber, fontWeight = FontWeight.Medium)
            }
        }

        // [4] 최근 운행일 전체기록
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("최근 운행 요약 (마지막 이동일)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (lastDriveLog != null) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatusItem("이동 거리", "${lastDriveLog.distanceKm} km", lastDriveLog.date)
                        StatusItem("이동 시간", "${lastDriveLog.durationMinutes} 분", "소요 시간")
                        StatusItem("전비", "${lastDriveLog.efficiencyWhKm} Wh/km", "평균 소비")
                    }
                } else {
                    Text("최근 주행 기록이 없습니다.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        }

        // [5] 타이어 공기압 (PSI)
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("타이어 공기압 (PSI)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TireItem("전륜 좌 (FL)", vehicleState.flTire)
                    TireItem("전륜 우 (FR)", vehicleState.frTire)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TireItem("후륜 좌 (RL)", vehicleState.rlTire)
                    TireItem("후륜 우 (RR)", vehicleState.rrTire)
                }
            }
        }

        // [6] 운행기록일지
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("운행 기록 일지 (1km 미만 제외)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                driveLogs.forEach { log ->
                    DriveLogRowItem(log)
                    HorizontalDivider(color = DarkBorder)
                }
            }
        }
    }
}

@Composable
fun DriveInfoTabContent(driveLogs: List<DriveLogItem>) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("상세 주행 히스토리", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        driveLogs.forEach { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(log.date, color = TextSecondary, fontSize = 12.sp)
                    if (log.isCharging) {
                        Text("[충전 완료] 배터리 ${log.batteryStart}% ➔ ${log.batteryEnd}%", color = AccentGreen, fontWeight = FontWeight.Bold)
                    } else {
                        Text("주행 거리: ${log.distanceKm} km | 소요: ${log.durationMinutes}분", color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text("전비: ${log.efficiencyWhKm} Wh/km (소모: ${log.batteryStart}% ➔ ${log.batteryEnd}%)", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MonthlyReportTabContent(report: MonthlyReport) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${report.monthStr} 누적 요약", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatusItem("총 주행거리", "${report.totalDistanceKm} km", "월 누적")
                    StatusItem("평균 전비", "${report.avgEfficiency} Wh/km", "월 평균")
                    StatusItem("총 운전시간", "${report.totalDriveTimeHours} 시간", "월 누적")
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("운전 시간 TOP 5 일자", color = TextPrimary, fontWeight = FontWeight.Bold)
                report.topDriveTimeDays.forEachIndexed { index, item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${index + 1}. ${item.first}", color = TextSecondary)
                        Text("${item.second} 분", color = AccentAmber, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("주행 거리 TOP 5 일자", color = TextPrimary, fontWeight = FontWeight.Bold)
                report.topDistanceDays.forEachIndexed { index, item ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${index + 1}. ${item.first}", color = TextSecondary)
                        Text("${item.second} km", color = AccentBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BatteryTabContent(batteryList: List<BatteryDegradationItem>) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("최근 50개 데이터 기준 배터리 상태", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                val latest = batteryList.firstOrNull()
                if (latest != null) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatusItem("현재 열화율", "${latest.degradationPercent}%", "SOH")
                        StatusItem("100% 환산 거리", "${latest.maxEstimatedRangeKm} km", "최대 주행가능")
                    }
                }
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("최근 50개 측정 히스토리", color = TextPrimary, fontWeight = FontWeight.Bold)
                batteryList.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.date, color = TextSecondary, fontSize = 12.sp)
                        Text("열화율: ${item.degradationPercent}%", color = TextPrimary, fontSize = 12.sp)
                        Text("100% 환산: ${item.maxEstimatedRangeKm} km", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                }
            }
        }
    }
}

@Composable
fun GuardianTabContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("감시 가디언 모니터링 모드 동작 중", color = TextSecondary, fontSize = 16.sp)
    }
}

@Composable
fun StatusItem(title: String, value: String, subtext: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(subtext, color = TextSecondary, fontSize = 10.sp)
    }
}

@Composable
fun TireItem(label: String, psi: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text("${psi} PSI", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun DriveLogRowItem(log: DriveLogItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(log.date, color = TextSecondary, fontSize = 11.sp)
            if (log.isCharging) {
                Text("[충전] +${log.batteryEnd - log.batteryStart}% 수충전", color = AccentGreen, fontWeight = FontWeight.Bold)
            } else {
                Text("주행 ${log.distanceKm} km (${log.durationMinutes}분)", color = TextPrimary, fontWeight = FontWeight.Medium)
            }
        }
        Text(
            if (log.isCharging) "${log.batteryStart}% ➔ ${log.batteryEnd}%" else "${log.efficiencyWhKm} Wh/km",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SettingsDialog(
    currentSettings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit
) {
    var supabaseUrl by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoMapKey by remember { mutableStateOf(currentSettings.kakaoMapKey) }
    var githubKey by remember { mutableStateOf(currentSettings.githubKey) }
    var teslaAccessToken by remember { mutableStateOf(currentSettings.teslaAccessToken) }
    var githubToken by remember { mutableStateOf(currentSettings.githubToken) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("설정 (보안 암호화 저장)", color = TextPrimary, fontSize = 18.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )
                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )
                OutlinedTextField(
                    value = kakaoMapKey,
                    onValueChange = { kakaoMapKey = it },
                    label = { Text("Kakao Map Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )
                OutlinedTextField(
                    value = githubKey,
                    onValueChange = { githubKey = it },
                    label = { Text("GitHub Key (sync.js 갱신용)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // 명시적 인자 이름(Named Arguments)을 지정하여 Type Mismatch 방지
                    onSave(
                        AppSettings(
                            supabaseUrl = supabaseUrl,
                            supabaseKey = supabaseKey,
                            kakaoMapKey = kakaoMapKey,
                            githubKey = githubKey,
                            teslaAccessToken = teslaAccessToken,
                            githubToken = githubToken,
                            isAutoSync = currentSettings.isAutoSync
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("저장")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = TextSecondary)
            }
        }
    )
}
