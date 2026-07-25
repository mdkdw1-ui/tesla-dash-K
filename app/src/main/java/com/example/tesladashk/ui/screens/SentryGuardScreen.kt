package com.example.tesladashk.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.viewmodel.DashboardViewModel
import com.example.tesladashk.viewmodel.LogMessage

@Composable
fun SentryGuardScreen(viewModel: DashboardViewModel) {
    val logList by viewModel.logList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🛡️ 감시모드 가디언",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { viewModel.triggerSyncAndFetch() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "동기화 중..." else "수동 데이터 갱신")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 상태 패널
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("가디언 모니터링", color = Color.Gray, fontSize = 12.sp)
                    Text("실시간 로그 모니터링 활성화", color = Color(0xFF22C55E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = { viewModel.clearLogs() }) {
                    Text("로그 지우기", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "💻 데이터 & 시스템 로그 터미널",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 로그 콘솔 창 (검은색 바탕 콘솔 박스)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF050507), shape = RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF27272A), shape = RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            if (logList.isEmpty()) {
                Text(
                    text = "표시할 로그가 없습니다. [수동 데이터 갱신]을 눌러 테스트해보세요.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(items = logList) { log ->
                        LogItemRow(log = log)
                    }
                }
            }
        }
    }
}

@Composable
fun LogItemRow(log: LogMessage) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "[${log.timestamp}] ",
            color = Color(0xFF71717A),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = log.text,
            color = if (log.isError) Color(0xFFEF4444) else Color(0xFF10B981),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
