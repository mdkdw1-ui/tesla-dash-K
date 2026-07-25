package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.ui.theme.*
import com.example.tesladashk.viewmodel.TeslaViewModel

@Composable
fun GuardianScreen(viewModel: TeslaViewModel) {
    val isGuardianActive by viewModel.isGuardianActive.collectAsState()
    val logs by viewModel.logs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 상단 가디언 상태 패널
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardDark)
                    .border(1.dp, BorderGray, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🛡️ 감시 가디언 모드", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background((if (isGuardianActive) AccentEmerald else AccentRed).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            if (isGuardianActive) "작동 중" else "중지됨",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isGuardianActive) AccentEmerald else AccentRed
                        )
                    }
                }

                Text(
                    "차량의 상태, 이상 징후 및 실시간 로그를 수신하고 모니터링합니다.",
                    fontSize = 12.sp,
                    color = TextGray
                )

                Button(
                    onClick = { viewModel.toggleGuardian() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isGuardianActive) AccentRed else AccentEmerald
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isGuardianActive) "가디언 감시 중단하기" else "가디언 감시 시작하기",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 로그 헤더
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("📜 실시간 감시 로그", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Text(
                    "로그 지우기",
                    fontSize = 12.sp,
                    color = AccentBlue,
                    modifier = Modifier.clickable { viewModel.clearLogs() }
                )
            }
        }

        // 로그 목록
        if (logs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("기록된 로그가 없습니다.", color = TextGray, fontSize = 13.sp)
                }
            }
        } else {
            items(logs) { logMsg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardDarker)
                        .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Text(logMsg, fontSize = 12.sp, color = LogGreen)
                }
            }
        }
    }
}
