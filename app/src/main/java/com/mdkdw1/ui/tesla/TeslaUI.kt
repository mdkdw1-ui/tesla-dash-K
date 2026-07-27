package com.mdkdw1.ui.tesla

import androidx.compose.foundation.background
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

// index-2.html 다크 모드 색상 팔레트
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
    val mainTabState by viewModel.mainTabState.collectAsState()
    val subTabState by viewModel.subTabState.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    val vehicleState by viewModel.vehicleState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val driveLogs by viewModel.driveLogs.collectAsState()
    val monthlyReport by viewModel.monthlyReport.collectAsState()
    val batteryList by viewModel.batteryList.collectAsState()
    val sentryEvents by viewModel.sentryEvents.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    val lastDriveLog = driveLogs.firstOrNull { !it.isCharging }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Tesla Command Hub",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(if (vehicleState.statusText == "온라인") AccentGreen.copy(alpha = 0.2f) else AccentAmber.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = vehicleState.statusText,
                                color = if (vehicleState.statusText == "온라인") AccentGreen else AccentAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.syncData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "동기화",
                            tint = if (isSyncing) AccentAmber else TextPrimary
                        )
                    }
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
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
            // [최상단 메인 탭] 테슬라 모니터 | 감시 가디언
            TabRow(
                selectedTabIndex = mainTabState,
                containerColor = DarkCard,
                contentColor = TextPrimary,
                divider = { HorizontalDivider(color = DarkBorder) }
            ) {
                Tab(
                    selected = mainTabState == 0,
                    onClick = { viewModel.setMainTab(0) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = if (mainTabState == 0) AccentAmber else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "테슬라 모니터",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (mainTabState == 0) AccentAmber else TextSecondary
                            )
                        }
                    }
                )
                Tab(
                    selected = mainTabState == 1,
                    onClick = { viewModel.setMainTab(1) },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = if (mainTabState == 1) AccentAmber else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "감시 가디언",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = if (mainTabState == 1) AccentAmber else TextSecondary
                            )
                        }
                    }
                )
            }

            if (mainTabState == 0) {
                // [서브 탭] 차량정보 | 주행정보 | 월간리포트 | 배터리
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
                            onClick = { viewModel.setSubTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 14.sp,
                                    color = if (subTabState == index) AccentBlue else TextSecondary,
                                    fontWeight = if (subTabState == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
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
                GuardianTabContent(sentryEvents = sentryEvents)
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

    // 1km 미만 제외 주행 일지 필터링
    val filteredLogs = remember(driveLogs) {
        driveLogs.filter { it.isCharging || it.distanceKm >= 1.0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // [1] 차량 상태 요약 카드
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("차량 상태 요약", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusItem("배터리 잔량", "${vehicleState.batteryLevel}%", "Supabase")
                    StatusItem("총 주행거리", "${String.format("%.1f", vehicleState.odometer)} km", "누적 오도미터")
                    StatusItem("실내/외 온도", "${vehicleState.insideTemp}℃ / ${vehicleState.outsideTemp}℃", "HVAC")
                    StatusItem("감시 모드", if (vehicleState.sentryModeOn) "활성" else "비활성", "Sentry")
                }
            }
        }

        // [2] 원격 제어 카드 (8종 버튼)
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
                        colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.isLocked) AccentBlue else Color(0xFF374151)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (vehicleState.isLocked) "잠김 해제" else "잠금", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.toggleClimate() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.climateOn) AccentAmber else Color(0xFF374151)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (vehicleState.climateOn) "공조 끄기" else "공조 켜기", fontSize = 12.sp)
                    }
                    Button(
                        onClick = { viewModel.toggleCharging() },
                        colors = ButtonDefaults.buttonColors(containerColor = if (vehicleState.isCharging) Color(0xFFEF4444) else Color(0xFF374151)),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (vehicleState.isCharging) "충전 중지" else "충전 시작", fontSize = 12.sp)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.openTrunk() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("트렁크", fontSize = 12.sp, color = TextPrimary)
                    }
                    OutlinedButton(
                        onClick = { viewModel.openFrunk() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("프렁크", fontSize = 12.sp, color = TextPrimary)
                    }
                    OutlinedButton(
                        onClick = { viewModel.honkHorn() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("경적", fontSize = 12.sp, color = TextPrimary)
                    }
                    OutlinedButton(
                        onClick = { viewModel.flashLights() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("비상등", fontSize = 12.sp, color = TextPrimary)
                    }
                }
            }
        }

        // [3] 주차 시간 카드
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

        // [4] 최근 운행 요약 카드
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

        // [5] 타이어 공기압 카드 (4륜 그리드)
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
                HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    TireItem("후륜 좌 (RL)", vehicleState.rlTire)
                    TireItem("후륜 우 (RR)", vehicleState.rrTire)
                }
            }
        }

        // [6] 운행 기록 일지 (1km 미만 자동 필터링 적용)
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("운행 기록 일지 (1km 미만 자동 제외)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (filteredLogs.isNotEmpty()) {
                    filteredLogs.forEach { log ->
                        DriveLogRowItem(log)
                        HorizontalDivider(color = DarkBorder, thickness = 0.5.dp)
                    }
                } else {
                    Text("표시할 유효 주행 일지가 없습니다.", color = TextSecondary, fontSize = 12.sp)
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
        Text("상세 주행 및 충전 히스토리", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        driveLogs.forEach { log ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(log.date, color = TextSecondary, fontSize = 12.sp)
                        Text(if (log.isCharging) "충전" else "주행", color = if (log.isCharging) AccentGreen else AccentBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    if (log.isCharging) {
                        Text("[충전 완료] 배터리 ${log.batteryStart}% ➔ ${log.batteryEnd}%", color = AccentGreen, fontWeight = FontWeight.Bold)
                        Text("장소: ${log.startLocation}", color = TextSecondary, fontSize = 12.sp)
                    } else {
                        Text("주행 거리: ${log.distanceKm} km | 소요: ${log.durationMinutes}분", color = TextPrimary, fontWeight = FontWeight.Medium)
                        Text("전비: ${log.efficiencyWhKm} Wh/km (${log.startLocation} ➔ ${log.endLocation})", color = TextSecondary, fontSize = 12.sp)
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
                        StatusItem("현재 열화율", "${latest.degradationPercent}%", "SOH ${latest.sohPercent}%")
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
                Text("측정 히스토리 (최근 50개)", color = TextPrimary, fontWeight = FontWeight.Bold)
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
fun GuardianTabContent(sentryEvents: List<SentryEventItem>) {
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
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AccentAmber, modifier = Modifier.size(48.dp))
                Text("감시 가디언 보안 모드 작동 중", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("실시간 차량 주변 움직임 및 이벤트 로그를 기록합니다.", color = TextSecondary, fontSize = 12.sp)
            }
        }

        Text("감시 이벤트 알림 로그", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)

        sentryEvents.forEach { event ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(event.eventType, color = AccentAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("위치: ${event.location}", color = TextPrimary, fontSize = 12.sp)
                        Text(event.timestamp, color = TextSecondary, fontSize = 11.sp)
                    }
                    IconButton(onClick = { /* 영상 재생 처리 */ }) {
                        Icon(Icons.Default.PlayCircle, contentDescription = "영상 보기", tint = AccentBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusItem(title: String, value: String, subtext: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextSecondary, fontSize = 11.sp)
        Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("설정 (AES-256 암호화 저장)", color = TextPrimary, fontSize = 18.sp) },
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
                    label = { Text("GitHub Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
                )
                OutlinedTextField(
                    value = teslaAccessToken,
                    onValueChange = { teslaAccessToken = it },
                    label = { Text("Tesla Access Token") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AccentBlue)
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
                            kakaoMapKey = kakaoMapKey,
                            githubKey = githubKey,
                            teslaAccessToken = teslaAccessToken,
                            githubToken = currentSettings.githubToken,
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
