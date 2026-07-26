package com.mdkdw1.ui.tesla

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var prefsStore: PrefsStore
    private lateinit var apiClient: TeslaApiClient
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefsStore = PrefsStore(this)
        apiClient = TeslaApiClient(prefsStore)
        notificationHelper = NotificationHelper(prefsStore)
        repository = AppRepository(prefsStore, apiClient, notificationHelper)

        setContent {
            TeslaDashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D0F12)
                ) {
                    TeslaDashboardScreen(repository = repository)
                }
            }
        }
    }
}

@Composable
fun TeslaDashboardScreen(repository: AppRepository) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // 대시보드 상태 관리
    var speed by remember { mutableFloatStateOf(0f) }
    var batteryPercent by remember { mutableIntStateOf(88) }
    var batteryRange by remember { mutableIntStateOf(420) }
    var gearState by remember { mutableStateOf("D") }
    var isLocked by remember { mutableStateOf(true) }
    var isSentryActive by remember { mutableStateOf(true) }
    var isClimateOn by remember { mutableStateOf(true) }
    var isTrunkOpen by remember { mutableStateOf(false) }
    var isDoorOpen by remember { mutableStateOf(false) }
    var insideTemp by remember { mutableFloatStateOf(21.5f) }
    var outsideTemp by remember { mutableFloatStateOf(18.0f) }
    var lastUpdated by remember { mutableStateOf(SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())) }

    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "SpeedGaugeAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0F12))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. 차량 상태 헤더 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161922)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282D3C))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = repository.latestSnapshot?.displayName ?: "Tesla Model Y",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "VIN: ${repository.latestSnapshot?.vin ?: "5YJSA1E28HF12388"}",
                        color = Color(0xFF8E95A5),
                        fontSize = 12.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            repository.refreshLatestVehicleData { snapshot, err ->
                                repository.postToMain {
                                    if (snapshot != null) {
                                        batteryPercent = snapshot.batteryLevel ?: batteryPercent
                                        isSentryActive = snapshot.sentryMode
                                        isLocked = snapshot.locked
                                        lastUpdated = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                                        Toast.makeText(context, "데이터 동기화 완료", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, err ?: "동기화 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("🔄 새로고침", fontSize = 12.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            repository.clearAuth()
                            Toast.makeText(context, "인증 정보가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282D3C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("로그아웃", fontSize = 12.sp, color = Color(0xFFE53935))
                    }
                }
            }
        }

        // 2. 클러스터 속도계 및 기어 선택
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161922)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282D3C))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PRND 및 배터리 상단 바
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("P", "R", "N", "D").forEach { gear ->
                            Text(
                                text = gear,
                                color = if (gearState == gear) Color(0xFF3B82F6) else Color(0xFF555555),
                                fontSize = 20.sp,
                                fontWeight = if (gearState == gear) FontWeight.Black else FontWeight.Normal,
                                modifier = Modifier.clickable {
                                    gearState = gear
                                    if (gear == "P") speed = 0f
                                }
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$batteryPercent%",
                            color = if (batteryPercent > 20) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF282D3C))
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = batteryPercent / 100f)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (batteryPercent > 20) Color(0xFF10B981) else Color(0xFFEF4444))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 원형 속도계 게이지
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0F1015))
                        .border(4.dp, Color(0xFF3B82F6), CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = animatedSpeed.toInt().toString(),
                            color = Color.White,
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "KM/H",
                            color = Color(0xFF8E95A5),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 주행 시뮬레이터 조작 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (speed >= 10f) speed -= 10f else speed = 0f
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282D3C))
                    ) {
                        Text("BRAKE (-10)", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            if (gearState == "D") speed += 10f
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text("DRIVE (+10)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. 빠른 제어 버튼 커맨드 바 (Quick Actions)
        Text(
            text = "빠른 제어 명령",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickActionButton(
                label = if (isLocked) "잠금 해제" else "차량 잠금",
                iconText = if (isLocked) "🔒" else "🔓",
                isActive = isLocked,
                activeColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            ) {
                isLocked = !isLocked
                Toast.makeText(context, if (isLocked) "차량이 잠겼습니다" else "차량 잠금이 해제되었습니다", Toast.LENGTH_SHORT).show()
            }

            QuickActionButton(
                label = "전조등 점등",
                iconText = "⚡",
                isActive = false,
                activeColor = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            ) {
                repository.triggerFlashLights { success, err ->
                    Toast.makeText(context, if (success) "전조등을 깜빡였습니다" else (err ?: "실패"), Toast.LENGTH_SHORT).show()
                }
            }

            QuickActionButton(
                label = "감시 모드",
                iconText = "🛡️",
                isActive = isSentryActive,
                activeColor = Color(0xFFEF4444),
                modifier = Modifier.weight(1f)
            ) {
                isSentryActive = !isSentryActive
                repository.updateSentryStateCache(isSentryActive)
            }

            QuickActionButton(
                label = "공조 제어",
                iconText = "🌡️",
                isActive = isClimateOn,
                activeColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            ) {
                isClimateOn = !isClimateOn
            }

            QuickActionButton(
                label = "트렁크",
                iconText = "🚗",
                isActive = isTrunkOpen,
                activeColor = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            ) {
                isTrunkOpen = !isTrunkOpen
            }
        }

        // 4. 세부 상태 카드가 위치한 2열 그리드
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 배터리 & 주행 정보 카드
            DashTileCard(
                title = "배터리 & 충전",
                modifier = Modifier.weight(1f)
            ) {
                Text("잔여 주행거리: ${batteryRange} km", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("충전 상태: Disconnected", color = Color(0xFF8E95A5), fontSize = 12.sp)
                Text("충전 전력: 0.0 kW", color = Color(0xFF8E95A5), fontSize = 12.sp)
            }

            // 실내외 공조 정보 카드
            DashTileCard(
                title = "공조 & 온도",
                modifier = Modifier.weight(1f)
            ) {
                Text("실내 온도: ${String.format("%.1f", insideTemp)}°C", color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("실외 온도: ${String.format("%.1f", outsideTemp)}°C", color = Color(0xFF8E95A5), fontSize = 12.sp)
                Text("공조 모드: ${if (isClimateOn) "ON (AC)" else "OFF"}", color = if (isClimateOn) Color(0xFF3B82F6) else Color(0xFF8E95A5), fontSize = 12.sp)
            }
        }

        // 5. TPMS 타이어 공기압 모니터링 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161922)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282D3C))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TPMS 타이어 공기압 모니터링", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TpmsBox(title = "전륜 좌측 (FL)", value = "2.9 BAR", isOk = true, modifier = Modifier.weight(1f))
                    TpmsBox(title = "전륜 우측 (FR)", value = "2.9 BAR", isOk = true, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TpmsBox(title = "후륜 좌측 (RL)", value = "2.8 BAR", isOk = true, modifier = Modifier.weight(1f))
                    TpmsBox(title = "후륜 우측 (RR)", value = "2.8 BAR", isOk = true, modifier = Modifier.weight(1f))
                }
            }
        }

        // 6. 보안 및 경계 점검 (Guardian Checks) 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161922)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282D3C))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("보안 및 도어 감지 상태", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isLocked && (isDoorOpen || isTrunkOpen)) "⚠️ 경고: 잠김 상태에서 열림 감지!" else "정상: 보안 경계 작동 중",
                        color = if (isLocked && (isDoorOpen || isTrunkOpen)) Color(0xFFEF4444) else Color(0xFF10B981),
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = {
                        repository.runGuardChecks()
                        Toast.makeText(context, "보안 경계 점검을 수행했습니다.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF282D3C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("경계 점검", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // 7. 카카오맵 경로 및 운행 기록 연동
        Button(
            onClick = {
                val intent = Intent(context, KakaoRouteMapActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE500))
        ) {
            Text("🗺️ 카카오맵 경로 및 운행 기록 보기", color = Color(0xFF191919), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        // 8. 하단 동기화 상태 표시 바
        Text(
            text = "마지막 업데이트: $lastUpdated | Supabase & Ntfy 연동 활성화",
            color = Color(0xFF555555),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun QuickActionButton(
    label: String,
    iconText: String,
    isActive: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) activeColor.copy(alpha = 0.2f) else Color(0xFF161922)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isActive) activeColor else Color(0xFF282D3C)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(iconText, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                color = if (isActive) activeColor else Color(0xFF8E95A5),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DashTileCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161922)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282D3C))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun TpmsBox(
    title: String,
    value: String,
    isOk: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF0F1015))
            .border(1.dp, Color(0xFF282D3C), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(title, color = Color(0xFF8E95A5), fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                value,
                color = if (isOk) Color(0xFF10B981) else Color(0xFFEF4444),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TeslaDashTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        background = Color(0xFF0D0F12),
        surface = Color(0xFF161922),
        primary = Color(0xFF3B82F6)
    )

    MaterialTheme(
        colorScheme = darkColors,
        content = content
    )
}
