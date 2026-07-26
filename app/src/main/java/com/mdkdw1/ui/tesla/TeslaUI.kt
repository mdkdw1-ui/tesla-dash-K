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
import java.text.SimpleDateFormat
import java.util.*

// 다크 테마 색상 팔레트
val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF9CA3AF)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)

// 앱 설정 모델 (암호화 저장 대상)
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val githubKey: String = ""
)

// 차량 상태 모델
data class VehicleState(
    val statusText: String = "주차 중",
    val batteryLevel: Int = 78,
    val totalOdometer: Double = 45210.5,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis() - (3 * 3600 * 1000 + 25 * 60 * 1000), // 3시간 25분 전
    val flTire: Double = 41.2,
    val frTire: Double = 41.5,
    val rlTire: Double = 40.8,
    val rrTire: Double = 41.0
)

// 주행/충전 기록 모델
data class DriveLogItem(
    val id: String,
    val date: String,
    val isCharging: Boolean,
    val distanceKm: Double,
    val durationMinutes: Int,
    val efficiencyWhKm: Int,
    val batteryStart: Int,
    val batteryEnd: Int
)

// 월간 리포트 데이터 모델
data class MonthlyReport(
    val monthStr: String,
    val totalDistanceKm: Double,
    val avgEfficiency: Int,
    val totalDriveTimeHours: Double,
    val topDriveTimeDays: List<Pair<String, Int>>, // 날짜, 분
    val topDistanceDays: List<Pair<String, Double>> // 날짜, km
)

// 배터리 열화 데이터 모델 (최근 50개)
data class BatteryDegradationItem(
    val date: String,
    val degradationPercent: Double,
    val maxEstimatedRangeKm: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen() {
    // 임시 상태 관리 (실제 구현 시 ViewModel 연동)
    var mainTabState by remember { mutableIntStateOf(0) } // 0: 테슬라 모니터, 1: 감시 가디언
    var subTabState by remember { mutableIntStateOf(0) }  // 0: 차량정보, 1: 주행정보, 2: 월간리포트, 3: 배터리

    var isSyncing by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(AppSettings()) }

    val vehicleState = remember { VehicleState() }

    // 샘플 주행/충전 데이터 (1km 미만 주행은 제외, 주행거리 변화 없이 배터리 1% 이상 상승 시 충전 처리)
    val rawLogs = remember {
        listOf(
            DriveLogItem("1", "2026-07-27 07:10", false, 15.4, 25, 142, 82, 78),
            DriveLogItem("2", "2026-07-26 22:00", true, 0.0, 120, 0, 45, 82), // 충전
            DriveLogItem("3", "2026-07-26 18:30", false, 0.5, 3, 210, 46, 45),  // 1km 미만 -> 제거 대상
            DriveLogItem("4", "2026-07-26 08:00", false, 32.1, 45, 155, 60, 46),
            DriveLogItem("5", "2026-07-25 19:15", false, 8.2, 18, 160, 65, 60)
        )
    }

    // 조건 적용된 최종 운행기록일지 필터링
    val filteredLogs = remember(rawLogs) {
        rawLogs.filter { log ->
            if (log.isCharging) {
                true
            } else {
                // 1km 미만 주행은 안 보여줌
                log.distanceKm >= 1.0
            }
        }
    }

    // 최근 운행일 데이터
    val lastDriveLog = filteredLogs.firstOrNull { !it.isCharging }

    // 월간 리포트 샘플 데이터
    val monthlyReport = remember {
        MonthlyReport(
            monthStr = "2026년 7월",
            totalDistanceKm = 1240.5,
            avgEfficiency = 148,
            totalDriveTimeHours = 32.5,
            topDriveTimeDays = listOf(
                "07-15" to 140, "07-03" to 115, "07-22" to 95, "07-10" to 80, "07-18" to 75
            ),
            topDistanceDays = listOf(
                "07-15" to 185.2, "07-03" to 142.0, "07-22" to 110.5, "07-10" to 98.4, "07-18" to 85.0
            )
        )
    }

    // 배터리 최근 50개 데이터 샘플
    val batteryDataList = remember {
        List(50) { index ->
            BatteryDegradationItem(
                date = "07-${50 - index}",
                degradationPercent = 94.5 + (index * 0.02),
                maxEstimatedRangeKm = 485.0 + (index * 0.1)
            )
        }.reversed()
    }

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
                    // 데이터 싱크 갱신 버튼
                    IconButton(onClick = {
                        isSyncing = true
                        // TODO: api/sync.js 연동 갱신 수행
                        isSyncing = false
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "데이터 동기화",
                            tint = if (isSyncing) AccentAmber else TextPrimary
                        )
                    }
                    // 설정 버튼
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
            // 상단 메인 탭 (테슬라 모니터 / 감시 가디언)
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
                // 테슬라 모니터 하위 4개 탭
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

                // 하위 탭별 메인 컨텐츠 영역
                Box(modifier = Modifier.fillMaxSize()) {
                    when (subTabState) {
                        0 -> VehicleInfoTabContent(
                            vehicleState = vehicleState,
                            lastDriveLog = lastDriveLog,
                            driveLogs = filteredLogs
                        )
                        1 -> DriveInfoTabContent(driveLogs = filteredLogs)
                        2 -> MonthlyReportTabContent(report = monthlyReport)
                        3 -> BatteryTabContent(batteryList = batteryDataList)
                    }
                }
            } else {
                // 감시 가디언 탭
                GuardianTabContent()
            }
        }
    }

    // 설정 입력 및 암호화 저장 다이얼로그
    if (showSettingsDialog) {
        SettingsDialog(
            currentSettings = settings,
            onDismiss = { showSettingsDialog = false },
            onSave = { updatedSettings ->
                settings = updatedSettings
                // TODO: EncryptedSharedPreferences 저장 처리
                showSettingsDialog = false
            }
        )
    }
}

// -------------------------------------------------------------------
// 1. 차량정보 탭
// -------------------------------------------------------------------
@Composable
fun VehicleInfoTabContent(
    vehicleState: VehicleState,
    lastDriveLog: DriveLogItem?,
    driveLogs: List<DriveLogItem>
) {
    val scrollState = rememberScrollState()

    // 주차 경과 시간 계산 (현재 시간 - 마지막 데이터 받기 시각)
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
                    StatusItem("상태", vehicleState.statusText, "테슬라 API 연동")
                    StatusItem("배터리 잔량", "${vehicleState.batteryLevel}%", "Supabase 연동")
                    StatusItem("총 주행거리", "${String.format("%.1f", vehicleState.totalOdometer)} km", "Supabase 연동")
                }
            }
        }

        // [2] 주차 시간
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

        // [3] 최근 운행일 전체기록
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

        // [4] 타이어 공기압 (PSI)
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

        // [5] 운행기록일지 (주행 / 충전 구분, 1km 미만 제외)
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

// -------------------------------------------------------------------
// 2. 주행정보 탭
// -------------------------------------------------------------------
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

// -------------------------------------------------------------------
// 3. 월간리포트 탭
// -------------------------------------------------------------------
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

        // 운전시간 TOP 5
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

        // 운전거리 TOP 5
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

// -------------------------------------------------------------------
// 4. 배터리 탭 (최근 50개 기준 열화율 및 주행가능거리)
// -------------------------------------------------------------------
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

        // 최근 50개 상세 기록 목록
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

// -------------------------------------------------------------------
// 5. 감시 가디언 탭
// -------------------------------------------------------------------
@Composable
fun GuardianTabContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("감시 가디언 모니터링 모드 동작 중", color = TextSecondary, fontSize = 16.sp)
    }
}

// -------------------------------------------------------------------
// 보조 UI 컴포넌트 및 다이얼로그
// -------------------------------------------------------------------
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
    var githubKey by remember { mutableStateOf(currentSettings.githubKey) }

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
                    label = { Text("Supabase API Key") },
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
                    onSave(AppSettings(supabaseUrl, supabaseKey, githubKey))
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
