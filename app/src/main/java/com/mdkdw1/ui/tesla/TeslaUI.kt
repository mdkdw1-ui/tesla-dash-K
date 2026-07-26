package com.mdkdw1.ui.tesla

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// ==========================================
// 테마 색상 정의 (index-2.html 다크 모드)
// ==========================================
val DarkBackground = Color(0xFF0D0E12)
val CardBackground = Color(0xFF161820)
val BorderColor = Color(0xFF262936)
val PrimaryAccent = Color(0xFF38BDF8)
val SecondaryAccent = Color(0xFF818CF8)
val SuccessGreen = Color(0xFF34D399)
val WarningAmber = Color(0xFFF59E0B)
val TextMuted = Color(0xFF9CA3AF)

@Composable
fun TeslaDashApp(viewModel: TeslaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 상단 헤더
            HeaderSection(
                isConnected = uiState.isConnected,
                isLoading = uiState.isLoading,
                onRefresh = { viewModel.refreshAllData() },
                onOpenSettings = { showSettingsDialog = true }
            )

            // 메인 탭 (테슬라 모니터 / 감시 가디언)
            MainTabRow(
                selectedTab = uiState.selectedMainTab,
                onTabSelected = { viewModel.selectMainTab(it) }
            )

            if (uiState.selectedMainTab == MainTab.MONITOR) {
                // 서브 탭 (차량정보, 주행정보, 월간리포트, 배터리)
                SubTabRow(
                    selectedSubTab = uiState.selectedSubTab,
                    onSubTabSelected = { viewModel.selectSubTab(it) }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    when (uiState.selectedSubTab) {
                        MonitorSubTab.VEHICLE -> VehicleInfoTab(uiState)
                        MonitorSubTab.DRIVE -> DriveInfoTab(uiState)
                        MonitorSubTab.MONTHLY -> MonthlyReportTab(uiState)
                        MonitorSubTab.BATTERY -> BatteryTab(uiState)
                    }
                }
            } else {
                // 감시 가디언 탭 콘텐츠
                GuardianTab(uiState)
            }
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentSettings = uiState.appSettings,
                onDismiss = { showSettingsDialog = false },
                onSave = { newSettings ->
                    viewModel.saveSettings(newSettings)
                    showSettingsDialog = false
                }
            )
        }
    }
}

@Composable
fun HeaderSection(
    isConnected: Boolean,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isConnected) SuccessGreen else WarningAmber)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tesla Command Hub",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row {
            IconButton(onClick = onRefresh, enabled = !isLoading) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "새로고침",
                    tint = TextMuted
                )
            }
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = TextMuted
                )
            }
        }
    }
}

@Composable
fun MainTabRow(selectedTab: MainTab, onTabSelected: (MainTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(CardBackground, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        MainTab.values().forEach { tab ->
            val isSelected = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) PrimaryAccent else Color.Transparent)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    color = if (isSelected) Color.Black else TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SubTabRow(selectedSubTab: MonitorSubTab, onSubTabSelected: (MonitorSubTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MonitorSubTab.values().forEach { tab ->
            val isSelected = tab == selectedSubTab
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) PrimaryAccent else BorderColor,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(if (isSelected) PrimaryAccent.copy(alpha = 0.15f) else CardBackground)
                    .clickable { onSubTabSelected(tab) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = tab.title,
                    color = if (isSelected) PrimaryAccent else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ==========================================
// 1. 차량 정보 탭 (Dashboard)
// ==========================================
@Composable
fun VehicleInfoTab(uiState: TeslaUiState) {
    val vehicle = uiState.vehicleState

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CardContainer {
                Column {
                    Text(
                        text = vehicle.vehicleName,
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "${vehicle.batteryPercent}%",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = vehicle.statusText,
                            color = SuccessGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = vehicle.batteryPercent / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SuccessGreen,
                        trackColor = BorderColor
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "예상 주행거리",
                    value = "${vehicle.estimatedRangeKm} km",
                    icon = Icons.Default.DirectionsCar
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "총 주행거리",
                    value = "${vehicle.totalOdometer.toInt()} km",
                    icon = Icons.Default.Speed
                )
            }
        }

        item {
            CardContainer {
                Text("타이어 공기압 (bar)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    TireItem("전좌", vehicle.frontLeftTire)
                    TireItem("전우", vehicle.frontRightTire)
                    TireItem("후좌", vehicle.rearLeftTire)
                    TireItem("후우", vehicle.rearRightTire)
                }
            }
        }
    }
}

@Composable
fun TireItem(label: String, pressure: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = TextMuted, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$pressure",
            color = if (pressure >= 2.8) SuccessGreen else WarningAmber,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
    }
}

// ==========================================
// 2. 주행 정보 탭
// ==========================================
@Composable
fun DriveInfoTab(uiState: TeslaUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CardContainer {
                Text("최근 운행 요약", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SummaryMetric("총 거리", "${uiState.dailySummary.totalDistanceKm} km")
                    SummaryMetric("총 시간", "${uiState.dailySummary.totalDurationMinutes} 분")
                    SummaryMetric("평균 전비", "${uiState.dailySummary.avgEfficiencyWhKm} Wh/km")
                }
            }
        }

        item {
            Text(
                text = "주행 / 충전 기록 Log",
                color = TextMuted,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(uiState.journalLogs) { item ->
            JournalLogCard(item)
        }
    }
}

@Composable
fun JournalLogCard(item: JournalLogItem) {
    CardContainer {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (item.type == JournalType.DRIVE) PrimaryAccent.copy(alpha = 0.2f) else SuccessGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (item.type == JournalType.DRIVE) "주행" else "충전",
                            color = if (item.type == JournalType.DRIVE) PrimaryAccent else SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "${item.dateText} ${item.timeText}", color = TextMuted, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = item.location, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (item.type == JournalType.DRIVE) {
                    Text(text = "${item.distanceKm} km", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "${item.efficiencyWhKm} Wh/km", color = TextMuted, fontSize = 12.sp)
                } else {
                    Text(text = "+${item.addedBatteryPercent}%", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "${item.durationMinutes} 분 소요", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// 3. 월간 리포트 탭
// ==========================================
@Composable
fun MonthlyReportTab(uiState: TeslaUiState) {
    val report = uiState.monthlyReport

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CardContainer {
                Text(
                    text = if (report.monthYear.isNotBlank()) report.monthYear else "월간 분석 리포트",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SummaryMetric("총 주행거리", "${report.totalDistanceKm} km")
                    SummaryMetric("총 주행시간", "${report.totalDriveMinutes / 60}시간 ${report.totalDriveMinutes % 60}분")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SummaryMetric("평균 전비", "${report.avgEfficiencyWhKm} Wh/km")
                    SummaryMetric("총 충전량", "${report.totalChargePercent} %")
                }
            }
        }

        item {
            CardContainer {
                Text("주행 거리 Top 5 일자", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                report.topDriveDistanceDays.forEach { (date, dist) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = date, color = TextMuted, fontSize = 13.sp)
                        Text(text = "$dist km", color = PrimaryAccent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. 배터리 열화 분석 탭
// ==========================================
@Composable
fun BatteryTab(uiState: TeslaUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CardContainer {
                Text("배터리 열화율 추이 (%)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                SimpleLineChart(
                    data = uiState.batteryRecords.map { it.degradationRate.toFloat() },
                    lineColor = SecondaryAccent
                )
            }
        }

        item {
            CardContainer {
                Text("100% 환산 가능 거리 추이 (km)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                SimpleLineChart(
                    data = uiState.batteryRecords.map { it.calculated100Km.toFloat() },
                    lineColor = SuccessGreen
                )
            }
        }
    }
}

// ==========================================
// 5. 감시 가디언 탭
// ==========================================
@Composable
fun GuardianTab(uiState: TeslaUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CardContainer {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("센트리 모드 (감시)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("주변 이벤트 감지 및 녹화 중", color = TextMuted, fontSize = 12.sp)
                }
                Switch(
                    checked = uiState.vehicleState.isSentryModeOn,
                    onCheckedChange = {},
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryAccent)
                )
            }
        }

        CardContainer(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "실시간 감시 가디언 카메라 스트리밍 연결 준비 완료",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }
    }
}

// ==========================================
// 6. 설정 다이얼로그 (Settings Dialog)
// ==========================================
@Composable
fun SettingsDialog(
    currentSettings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit
) {
    var supabaseUrl by remember { mutableStateOf(currentSettings.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentSettings.supabaseKey) }
    var kakaoMapKey by remember { mutableStateOf(currentSettings.kakaoMapKey) }
    var teslaToken by remember { mutableStateOf(currentSettings.teslaAccessToken) }
    var githubToken by remember { mutableStateOf(currentSettings.githubToken) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CardBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("앱 및 API 설정", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    singleLine = true,
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = kakaoMapKey,
                    onValueChange = { kakaoMapKey = it },
                    label = { Text("KakaoMap API Key") },
                    singleLine = true,
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = teslaToken,
                    onValueChange = { teslaToken = it },
                    label = { Text("Tesla Access Token") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors()
                )

                OutlinedTextField(
                    value = githubToken,
                    onValueChange = { githubToken = it },
                    label = { Text("GitHub Token") },
                    singleLine = true,
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소", color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                AppSettings(
                                    supabaseUrl = supabaseUrl,
                                    supabaseKey = supabaseKey,
                                    kakaoMapKey = kakaoMapKey,
                                    teslaAccessToken = teslaToken,
                                    githubToken = githubToken
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                    ) {
                        Text("저장 및 적용", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// 공통 UI 컴포넌트 & 헬퍼 함수
// ==========================================
@Composable
fun CardContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column { content() }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    CardContainer(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, color = TextMuted, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SummaryMetric(title: String, value: String) {
    Column {
        Text(text = title, color = TextMuted, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
fun SimpleLineChart(data: List<Float>, lineColor: Color) {
    if (data.isEmpty()) return

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        val maxVal = data.maxOrNull() ?: 1f
        val minVal = data.minOrNull() ?: 0f
        val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

        val widthStep = size.width / (data.size - 1).coerceAtLeast(1)
        val points = data.mapIndexed { index, value ->
            val x = index * widthStep
            val y = size.height - ((value - minVal) / range * size.height)
            Offset(x, y)
        }

        val path = Path().apply {
            points.forEachIndexed { i, pt ->
                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
            }
        }

        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryAccent,
    unfocusedBorderColor = BorderColor,
    focusedLabelColor = PrimaryAccent,
    unfocusedLabelColor = TextMuted,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
