package com.mdkdw1.ui.tesla

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.concurrent.TimeUnit

// 색상 테마 정의
val DarkBackground = Color(0xFF0D0E12)
val CardBackground = Color(0xFF161820)
val BorderColor = Color(0xFF262936)
val PrimaryGreen = Color(0xFF34D399)
val SecondaryBlue = Color(0xFF818CF8)
val AccentAmber = Color(0xFFD97706)
val TextGray = Color(0xFF9CA3AF)

@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel = viewModel()) {
    val mainTab by viewModel.currentMainTab.collectAsState()
    val subTab by viewModel.currentSubTab.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. 상단 바 (앱 타이틀 + 우측 싱크/설정 버튼)
            TeslaTopAppBar(
                isSyncing = isSyncing,
                onSyncClick = { viewModel.refreshData() },
                onSettingsClick = { viewModel.toggleSettingsDialog(true) }
            )

            // 2. 메인 탭 (테슬라 모니터 / 감시 가디언)
            MainTabRow(
                selectedTab = mainTab,
                onTabSelected = { viewModel.selectMainTab(it) }
            )

            // 3. 메인 탭에 따른 화면 분기
            when (mainTab) {
                MainTab.MONITOR -> {
                    // 하위 탭 (차량정보, 주행정보, 월간리포트, 배터리)
                    SubTabRow(
                        selectedTab = subTab,
                        onTabSelected = { viewModel.selectSubTab(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 하위 탭 콘텐츠
                    when (subTab) {
                        MonitorSubTab.VEHICLE -> VehicleInfoTabContent(viewModel)
                        MonitorSubTab.DRIVE -> DriveInfoTabContent(viewModel)
                        MonitorSubTab.MONTHLY -> MonthlyReportTabContent(viewModel)
                        MonitorSubTab.BATTERY -> BatteryTabContent(viewModel)
                    }
                }
                MainTab.GUARDIAN -> {
                    GuardianTabContent()
                }
            }
        }
    }

    // 설정 대화상자
    if (showSettingsDialog) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleSettingsDialog(false) }
        )
    }
}

// 상단 앱 바
@Composable
fun TeslaTopAppBar(
    isSyncing: Boolean,
    onSyncClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Tesla Command Hub",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
                onClick = onSyncClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                if (isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = PrimaryGreen,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "데이터 싱크 갱신",
                        tint = PrimaryGreen
                    )
                }
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(CardBackground)
                    .border(1.dp, BorderColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = TextGray
                )
            }
        }
    }
}

// 메인 탭 바
@Composable
fun MainTabRow(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(CardBackground, RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        MainTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) SecondaryBlue else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = if (isSelected) Color.White else TextGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 하위 탭 바
@Composable
fun SubTabRow(
    selectedTab: MonitorSubTab,
    onTabSelected: (MonitorSubTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MonitorSubTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PrimaryGreen.copy(alpha = 0.2f) else CardBackground)
                    .border(
                        1.dp,
                        if (isSelected) PrimaryGreen else BorderColor,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = if (isSelected) PrimaryGreen else TextGray,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Sub-Tab 1: 차량정보 탭
// -----------------------------------------------------------------------------
@Composable
fun VehicleInfoTabContent(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val journalLogs by viewModel.journalLogs.collectAsState()
    val summary by viewModel.recentDriveSummary.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. 최상단: 차량 상태, 배터리 남은 것, 총 주행거리
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = vehicleState.statusText,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "총 ${String.format("%,.1f", vehicleState.totalOdometer)} km",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 배터리 남은 상태 게이지 바
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "배터리 잔량", color = TextGray, fontSize = 13.sp)
                        Text(
                            text = "${vehicleState.batteryPercent}%",
                            color = PrimaryGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { vehicleState.batteryPercent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = PrimaryGreen,
                        trackColor = BorderColor
                    )
                }
            }
        }

        // 2. 주차 정보 (마지막 수신 시간 계산)
        item {
            val diffMillis = System.currentTimeMillis() - vehicleState.lastUpdated.time
            val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) % 60

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = "주차시간",
                        tint = AccentAmber,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "현재 주차 경과 시간", color = TextGray, fontSize = 12.sp)
                        Text(
                            text = "${hours}시간 ${minutes}분 동안 주차 중",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 3. 최근 운행일 전체 기록
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "최근 운행일 전체기록 (${summary.dateText})",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryMetricItem("이동 거리", "${String.format("%.1f", summary.totalDistanceKm)} km")
                        SummaryMetricItem("이동 시간", "${summary.totalDurationMinutes} 분")
                        SummaryMetricItem("평균 전비", "${summary.avgEfficiencyWhKm} Wh/km")
                    }
                }
            }
        }

        // 4. 타이어 공기압 (Supabase 데이터)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "타이어 공기압 (bar)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TirePressureBox("전륜 좌", vehicleState.frontLeftTire)
                        TirePressureBox("전륜 우", vehicleState.frontRightTire)
                        TirePressureBox("후륜 좌", vehicleState.rearLeftTire)
                        TirePressureBox("후륜 우", vehicleState.rearRightTire)
                    }
                }
            }
        }

        // 5. 운행기록일지 Header
        item {
            Text(
                text = "운행 및 충전 기록일지",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 1km 이상 운행 및 충전 카운트 목록 출력
        items(journalLogs) { log ->
            JournalLogCard(log)
        }
    }
}

@Composable
fun SummaryMetricItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = TextGray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = PrimaryGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TirePressureBox(label: String, pressure: Double) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(DarkBackground, RoundedCornerShape(8.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(text = label, color = TextGray, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$pressure",
            color = if (pressure in 2.7..3.1) PrimaryGreen else AccentAmber,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun JournalLogCard(log: JournalLogItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (log.type == JournalType.DRIVE) SecondaryBlue.copy(alpha = 0.2f)
                            else PrimaryGreen.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (log.type == JournalType.DRIVE) Icons.Default.DirectionsCar else Icons.Default.Bolt,
                        contentDescription = null,
                        tint = if (log.type == JournalType.DRIVE) SecondaryBlue else PrimaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (log.type == JournalType.DRIVE) log.location else "충전 완료 (${log.location})",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${log.dateText} ${log.timeText} · ${log.durationMinutes}분 소요",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                if (log.type == JournalType.DRIVE) {
                    Text(
                        text = "${log.distanceKm} km",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${log.efficiencyWhKm} Wh/km",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                } else {
                    Text(
                        text = "+${log.addedBatteryPercent}%",
                        color = PrimaryGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${log.batteryStart}% → ${log.batteryEnd}%",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Sub-Tab 2: 주행정보 탭
// -----------------------------------------------------------------------------
@Composable
fun DriveInfoTabContent(viewModel: TeslaViewModel) {
    val journalLogs by viewModel.journalLogs.collectAsState()
    val driveLogs = journalLogs.filter { it.type == JournalType.DRIVE }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "전체 주행 기록 (1km 이상)",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        items(driveLogs) { log ->
            JournalLogCard(log)
        }
    }
}

// -----------------------------------------------------------------------------
// Sub-Tab 3: 월간리포트 탭
// -----------------------------------------------------------------------------
@Composable
fun MonthlyReportTabContent(viewModel: TeslaViewModel) {
    val reportState by viewModel.monthlyReport.collectAsState()
    val report = reportState ?: return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${report.monthYear} 리포트",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 주요 누적/평균 통계
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SummaryMetricItem("총 주행거리", "${report.totalDistanceKm} km")
                        SummaryMetricItem("총 운전시간", "${report.totalDriveMinutes / 60}시간 ${report.totalDriveMinutes % 60}분")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SummaryMetricItem("월 평균 전비", "${report.avgEfficiencyWhKm} Wh/km")
                        SummaryMetricItem("총 충전량", "+${report.totalChargePercent}%")
                    }
                }
            }
        }

        // 운전시간 Top 5 일자
        item {
            TopRankCard(
                title = "⏱️ 운전시간 Top 5 일자",
                items = report.topDriveTimeDays.map { Pair(it.first, "${it.second / 60}시간 ${it.second % 60}분") }
            )
        }

        // 주행거리 Top 5 일자
        item {
            TopRankCard(
                title = "🛣️ 주행거리 Top 5 일자",
                items = report.topDriveDistanceDays.map { Pair(it.first, "${it.second} km") }
            )
        }
    }
}

@Composable
fun TopRankCard(title: String, items: List<Pair<String, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            items.forEachIndexed { index, pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${index + 1}. ${pair.first}",
                        color = TextGray,
                        fontSize = 13.sp
                    )
                    Text(
                        text = pair.second,
                        color = PrimaryGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// Sub-Tab 4: 배터리 탭 (최근 50개 자료 기반 차트)
// -----------------------------------------------------------------------------
@Composable
fun BatteryTabContent(viewModel: TeslaViewModel) {
    val batteryRecords by viewModel.batteryRecords.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "배터리 성능분석 (최근 50개 기록)",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 1. 배터리 열화율 차트
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "배터리 열화율 (%)", color = Color.White, fontSize = 14.sp)
                        val latestDeg = batteryRecords.lastOrNull()?.degradationRate ?: 0.0
                        Text(
                            text = "${latestDeg}%",
                            color = SecondaryBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LineChartCanvas(
                        data = batteryRecords.map { it.degradationRate.toFloat() },
                        lineColor = SecondaryBlue
                    )
                }
            }
        }

        // 2. 100% 환산 주행가능거리 차트
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "100% 환산 주행가능거리 (km)", color = Color.White, fontSize = 14.sp)
                        val latestRange = batteryRecords.lastOrNull()?.calculated100Km ?: 0.0
                        Text(
                            text = "${latestRange} km",
                            color = PrimaryGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LineChartCanvas(
                        data = batteryRecords.map { it.calculated100Km.toFloat() },
                        lineColor = PrimaryGreen
                    )
                }
            }
        }
    }
}

// Jetpack Compose Canvas 선 그래프 컴포넌트
@Composable
fun LineChartCanvas(data: List<Float>, lineColor: Color) {
    if (data.isEmpty()) return

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val width = size.width
        val height = size.height

        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

        val points = data.mapIndexed { index, value ->
            val x = index.toFloat() / (data.size - 1) * width
            val y = height - ((value - minVal) / range * height * 0.8f + height * 0.1f)
            Offset(x, y)
        }

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
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

// -----------------------------------------------------------------------------
// Main Tab 2: 감시 가디언 탭
// -----------------------------------------------------------------------------
@Composable
fun GuardianTabContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "감시 가디언 모드 작동 중",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "실시간 테슬라 센트리 이벤트 및 위험 감지 모니터링",
                color = TextGray,
                fontSize = 13.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 설정 대화상자 (Supabase URI, KEY, GitHub Token 입력 & 암호화 저장)
// -----------------------------------------------------------------------------
@Composable
fun SettingsDialog(
    viewModel: TeslaViewModel,
    onDismiss: () -> Unit
) {
    val currentSettings by viewModel.appSettings.collectAsState()

    var url by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var key by remember { mutableStateOf(currentSettings.supabaseKey) }
    var token by remember { mutableStateOf(currentSettings.githubToken) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "앱 보안 설정",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "입력한 인증 키는 AES-256으로 내부 저장소에 안전하게 암호화되어 저장됩니다.",
                    color = TextGray,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Supabase URL") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = PrimaryGreen,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Supabase Key") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = PrimaryGreen,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = token,
                    onValueChange = { token = it },
                    label = { Text("GitHub Key (sync.js 용)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = BorderColor,
                        focusedLabelColor = PrimaryGreen,
                        unfocusedLabelColor = TextGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소", color = TextGray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.saveSettings(url, key, token) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("저장", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
