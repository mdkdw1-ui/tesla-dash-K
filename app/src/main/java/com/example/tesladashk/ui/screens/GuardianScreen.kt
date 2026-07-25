package com.example.tesladashk.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.ui.*

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
                        Text("마지막 동기화: 방금 전", color = TextGray, fontSize = 10.sp)
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
                    Text("실시간 Supabase & 디버깅 로그", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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
