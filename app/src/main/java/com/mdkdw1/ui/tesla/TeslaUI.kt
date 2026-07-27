package com.mdkdw1.ui.tesla

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView

val DarkBackground = Color(0xFF0D0E12)
val DarkCard = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFF3F4F6)
val TextSecondary = Color(0xFF9CA3AF)
val AccentAmber = Color(0xFFD97706)
val AccentBlue = Color(0xFF3B82F6)
val AccentGreen = Color(0xFF10B981)
val AccentRed = Color(0xFFEF4444)
val AccentPurple = Color(0xFF818CF8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeslaMainScreen(viewModel: TeslaViewModel) {
    val vehicleState by viewModel.vehicleState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val currentTab by viewModel.currentMainTab.collectAsState()

    Scaffold(
        topBar = {
            HeaderBar(
                vehicleName = vehicleState.vehicleName,
                lastUpdated = vehicleState.lastUpdated,
                onRefresh = { viewModel.refreshState() }
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> DashboardScreen(vehicleState = vehicleState, viewModel = viewModel)
                1 -> BatteryDegradationScreen(viewModel = viewModel)
                2 -> ChargingHistoryScreen(viewModel = viewModel)
                3 -> ConsumableManagementScreen(viewModel = viewModel)
                4 -> KakaoMapScreen(settings = settings)
                5 -> SettingsScreen(settings = settings, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HeaderBar(
    vehicleName: String,
    lastUpdated: String,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkCard)
            .border(BorderStroke(1.dp, DarkBorder))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = "Tesla",
                tint = AccentAmber,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = vehicleName,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "동기화: $lastUpdated",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        IconButton(onClick = onRefresh) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = AccentBlue
            )
        }
    }
}

@Composable
fun BottomNavBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        NavItem("대시보드", Icons.Default.Dashboard),
        NavItem("배터리/열화", Icons.Default.BatteryChargingFull),
        NavItem("충전 기록", Icons.Default.EvStation),
        NavItem("소모품", Icons.Default.Build),
        NavItem("위치 지도", Icons.Default.Map),
        NavItem("설정", Icons.Default.Settings)
    )

    NavigationBar(
        containerColor = DarkCard,
        contentColor = TextPrimary,
        tonalElevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentAmber,
                    selectedTextColor = AccentAmber,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                    indicatorColor = DarkBorder
                )
            )
        }
    }
}

data class NavItem(val label: String, val icon: ImageVector)

@Composable
fun DashboardScreen(
    vehicleState: VehicleState,
    viewModel: TeslaViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, AccentAmber)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("배터리 잔량", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        text = vehicleState.chargeState,
                        color = if (vehicleState.isCharging) AccentGreen else AccentBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${vehicleState.batteryLevel}%",
                        color = TextPrimary,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "예상 주행거리: ${vehicleState.batteryRangeKm} km",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = vehicleState.batteryLevel / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = if (vehicleState.batteryLevel > 20) AccentGreen else AccentRed,
                    trackColor = DarkBorder
                )
            }
        }

        Text(
            text = "빠른 제어 명령",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlCard(
                title = "도어 잠금",
                subtitle = if (vehicleState.isLocked) "잠김" else "열림",
                icon = if (vehicleState.isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                isActive = vehicleState.isLocked,
                activeColor = AccentGreen,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleLock() }
            )
            ControlCard(
                title = "실내 공조",
                subtitle = if (vehicleState.climateOn) "${vehicleState.insideTempC}°C 작동중" else "꺼짐",
                icon = Icons.Default.AcUnit,
                isActive = vehicleState.climateOn,
                activeColor = AccentBlue,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleClimate() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ControlCard(
                title = "센트리 모드",
                subtitle = if (vehicleState.sentryMode) "감시 중" else "비활성",
                icon = Icons.Default.Security,
                isActive = vehicleState.sentryMode,
                activeColor = AccentAmber,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleSentry() }
            )
            ControlCard(
                title = "트렁크 / 프렁크",
                subtitle = "트렁크: ${if (vehicleState.trunkOpen) "열림" else "닫힘"}",
                icon = Icons.Default.SensorDoor,
                isActive = vehicleState.trunkOpen || vehicleState.frunkOpen,
                activeColor = AccentPurple,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.toggleTrunk() }
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "차량 상태 정보",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatusDetailItem("총 누적 주행", "${vehicleState.odometerKm} km")
                    StatusDetailItem("실내 온도", "${vehicleState.insideTempC} °C")
                    StatusDetailItem("외기 온도", "${vehicleState.outsideTempC} °C")
                }
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = DarkBorder)
                Spacer(modifier = Modifier.height(12.dp))
                Text("타이어 공기압 (bar)", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("전좌: ${vehicleState.tirePressureFl}", color = TextPrimary, fontSize = 13.sp)
                    Text("전우: ${vehicleState.tirePressureFr}", color = TextPrimary, fontSize = 13.sp)
                    Text("후좌: ${vehicleState.tirePressureRl}", color = TextPrimary, fontSize = 13.sp)
                    Text("후우: ${vehicleState.tirePressureRr}", color = TextPrimary, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun ControlCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) activeColor.copy(alpha = 0.15f) else DarkCard
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) activeColor else DarkBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isActive) activeColor else TextSecondary,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = if (isActive) activeColor else TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatusDetailItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BatteryDegradationScreen(viewModel: TeslaViewModel) {
    val records by viewModel.degradationRecords.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "배터리 열화율 분석",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        val latestDeg = records.lastOrNull()?.degradationPct ?: 0.0
        val latestRange = records.lastOrNull()?.fullRangeKm ?: 0.0

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("현재 열화율", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "$latestDeg %",
                        color = AccentPurple,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = DarkBorder
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("100% 환산 거리", color = TextSecondary, fontSize = 12.sp)
                    Text(
                        text = "$latestRange km",
                        color = AccentGreen,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "누적 열화율 추이 (%)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                DegradationChart(records = records)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "측정 히스토리",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                records.forEach { rec ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(rec.date, color = TextSecondary, fontSize = 13.sp)
                        Text("${rec.odometerKm.toInt()} km", color = TextPrimary, fontSize = 13.sp)
                        Text("${rec.fullRangeKm} km", color = AccentGreen, fontSize = 13.sp)
                        Text("${rec.degradationPct}%", color = AccentPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Divider(color = DarkBorder)
                }
            }
        }
    }
}

@Composable
fun DegradationChart(records: List<DegradationRecord>) {
    if (records.isEmpty()) return

    val maxPct = (records.maxOfOrNull { it.degradationPct } ?: 5.0).coerceAtLeast(5.0)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(8.dp)
    ) {
        val width = size.width
        val height = size.height
        val stepX = width / (records.size - 1).coerceAtLeast(1)

        val path = Path()
        records.forEachIndexed { index, rec ->
            val x = index * stepX
            val y = height - (rec.degradationPct / maxPct * height).toFloat()
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            drawCircle(
                color = AccentPurple,
                radius = 5f,
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = AccentPurple,
            style = Stroke(width = 3f)
        )
    }
}

@Composable
fun ChargingHistoryScreen(viewModel: TeslaViewModel) {
    val records by viewModel.chargeRecords.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "충전 기록 및 비용 관리",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showAddDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
            ) {
                Icon(Icons.Default.Add, contentDescription = "추가", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("기록 추가", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val totalKwh = records.sumOf { it.addedKwh }
        val totalCost = records.sumOf { it.costKrw }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("총 충전량", color = TextSecondary, fontSize = 12.sp)
                    Text("${String.format("%.1f", totalKwh)} kWh", color = AccentBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("총 지출 비용", color = TextSecondary, fontSize = 12.sp)
                    Text("${String.format("%,d", totalCost)} 원", color = AccentAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(records) { rec ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(rec.location, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${rec.date} • ${rec.chargeType} (${rec.durationMinutes}분)", color = TextSecondary, fontSize = 12.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${rec.addedKwh} kWh", color = AccentGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("${String.format("%,d", rec.costKrw)} 원", color = AccentAmber, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddChargeDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { date, loc, kwh, cost, dur, type ->
                viewModel.addChargeRecord(date, loc, kwh, cost, dur, type)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddChargeDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, Double, Int, Int, String) -> Unit
) {
    var date by remember { mutableStateOf("2024-12-25") }
    var location by remember { mutableStateOf("") }
    var kwh by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Supercharger") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkCard,
        title = { Text("충전 기록 추가", color = TextPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("충전소 위치") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = kwh,
                    onValueChange = { kwh = it },
                    label = { Text("충전량 (kWh)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = cost,
                    onValueChange = { cost = it },
                    label = { Text("비용 (원)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("소요 시간 (분)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(
                        date,
                        location.ifBlank { "자택 충전" },
                        kwh.toDoubleOrNull() ?: 0.0,
                        cost.toIntOrNull() ?: 0,
                        duration.toIntOrNull() ?: 0,
                        type
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
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

@Composable
fun ConsumableManagementScreen(viewModel: TeslaViewModel) {
    val items by viewModel.consumableItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "소모품 교환 및 상태 관리",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = BorderStroke(1.dp, DarkBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(item.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "남은 수명 ${item.remainingPct}%",
                                color = if (item.remainingPct > 30) AccentGreen else AccentRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = item.remainingPct / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (item.remainingPct > 30) AccentGreen else AccentRed,
                            trackColor = DarkBorder
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "최근 교환: ${item.lastReplacedDate} (${item.lastReplacedOdoKm.toInt()} km)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Button(
                                onClick = { viewModel.resetConsumableItem(item.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkBorder),
                                modifier = Modifier.height(32.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("교환 완료 등록", fontSize = 11.sp, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KakaoMapScreen(settings: AppSettings) {
    val apiKey = settings.kakaoMapKey

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "차량 실시간 위치 (카카오맵)",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(BorderStroke(1.dp, DarkBorder))
        ) {
            if (apiKey.isBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "설정 메뉴에서 Kakao Map API 키를 입력해주세요.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            } else {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = true
                            val html = """
                                <!DOCTYPE html>
                                <html>
                                <head>
                                    <meta charset="utf-8"/>
                                    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=$apiKey"></script>
                                    <style>
                                        html, body, #map { width:100%; height:100%; margin:0; padding:0; background-color:#0d0e12; }
                                    </style>
                                </head>
                                <body>
                                <div id="map"></div>
                                <script>
                                    var container = document.getElementById('map');
                                    var options = {
                                        center: new kakao.maps.LatLng(37.5665, 126.9780),
                                        level: 3
                                    };
                                    var map = new kakao.maps.Map(container, options);
                                    var markerPosition  = new kakao.maps.LatLng(37.5665, 126.9780); 
                                    var marker = new kakao.maps.Marker({
                                        position: markerPosition
                                    });
                                    marker.setMap(map);
                                </script>
                                </body>
                                </html>
                            """.trimIndent()
                            loadDataWithBaseURL("https://dapi.kakao.com", html, "text/html", "UTF-8", null)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(
    settings: AppSettings,
    viewModel: TeslaViewModel
) {
    val scrollState = rememberScrollState()

    var supabaseUrl by remember(settings) { mutableStateOf(settings.supabaseUrl) }
    var supabaseAnonKey by remember(settings) { mutableStateOf(settings.supabaseAnonKey) }
    var kakaoMapKey by remember(settings) { mutableStateOf(settings.kakaoMapKey) }
    var teslaRefreshToken by remember(settings) { mutableStateOf(settings.teslaRefreshToken) }
    var aesPassword by remember(settings) { mutableStateOf(settings.aesPassword) }
    var targetSoc by remember(settings) { mutableStateOf(settings.targetSoc.toString()) }
    var chargeAmps by remember(settings) { mutableStateOf(settings.chargeAmps.toString()) }
    var updateInterval by remember(settings) { mutableStateOf(settings.updateIntervalSec.toString()) }
    var autoRefresh by remember(settings) { mutableStateOf(settings.autoRefresh) }
    var pushNotification by remember(settings) { mutableStateOf(settings.pushNotification) }

    var saveMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "암호화 설정 및 서비스 연동",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("보안 저장소 (AES-256 Encrypted)", color = AccentAmber, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = supabaseAnonKey,
                    onValueChange = { supabaseAnonKey = it },
                    label = { Text("Supabase Anon Key") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = kakaoMapKey,
                    onValueChange = { kakaoMapKey = it },
                    label = { Text("Kakao Map API Key") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = teslaRefreshToken,
                    onValueChange = { teslaRefreshToken = it },
                    label = { Text("Tesla Refresh Token") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = aesPassword,
                    onValueChange = { aesPassword = it },
                    label = { Text("AES-256 비밀번호") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkCard),
            border = BorderStroke(1.dp, DarkBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("충전 및 동기화 설정", color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = targetSoc,
                        onValueChange = { targetSoc = it },
                        label = { Text("목표 SOC (%)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = chargeAmps,
                        onValueChange = { chargeAmps = it },
                        label = { Text("충전 전류 (A)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = updateInterval,
                    onValueChange = { updateInterval = it },
                    label = { Text("동기화 주기 (초)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("자동 새로고침", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = autoRefresh,
                        onCheckedChange = { autoRefresh = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("푸시 알림 수신", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = pushNotification,
                        onCheckedChange = { pushNotification = it }
                    )
                }
            }
        }

        Button(
            onClick = {
                val newSettings = AppSettings(
                    supabaseUrl = supabaseUrl,
                    supabaseAnonKey = supabaseAnonKey,
                    kakaoMapKey = kakaoMapKey,
                    teslaRefreshToken = teslaRefreshToken,
                    aesPassword = aesPassword,
                    targetSoc = targetSoc.toIntOrNull() ?: 80,
                    chargeAmps = chargeAmps.toIntOrNull() ?: 32,
                    updateIntervalSec = updateInterval.toIntOrNull() ?: 30,
                    autoRefresh = autoRefresh,
                    pushNotification = pushNotification
                )
                viewModel.saveSettings(newSettings)
                saveMessage = "설정이 성공적으로 저장되었습니다."
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AccentAmber)
        ) {
            Icon(Icons.Default.Save, contentDescription = "저장")
            Spacer(modifier = Modifier.width(8.dp))
            Text("설정 암호화 저장", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        if (saveMessage.isNotEmpty()) {
            Text(
                text = saveMessage,
                color = AccentGreen,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
