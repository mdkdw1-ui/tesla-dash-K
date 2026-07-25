package com.example.tesladashk.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tesladashk.network.AppConfig
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun SettingsScreen(
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentConfig by viewModel.config.collectAsState()

    var supabaseUrl by remember { mutableStateOf(currentConfig.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(currentConfig.supabaseKey) }
    var kakaoKey by remember { mutableStateOf(currentConfig.kakaoKey) }
    var nativeKakaoKey by remember { mutableStateOf(currentConfig.nativeKakaoKey) }
    var userUid by remember { mutableStateOf(currentConfig.userUid) }
    var vehicleId by remember { mutableStateOf(currentConfig.vehicleId) }
    var ntfyTopic by remember { mutableStateOf(currentConfig.ntfyTopic) }
    var accessToken by remember { mutableStateOf(currentConfig.accessToken) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D0E12))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚙️ 앱 설정",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
            ) {
                Text("닫기", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF13151C)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                
                Text("🔑 연동 정보 설정", color = Color(0xFF3B82F6), fontSize = 14.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = supabaseUrl,
                    onValueChange = { supabaseUrl = it },
                    label = { Text("Supabase URL") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = supabaseKey,
                    onValueChange = { supabaseKey = it },
                    label = { Text("Supabase Anon Key") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = userUid,
                    onValueChange = { userUid = it },
                    label = { Text("User UID (예: dwHcQZ...)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kakaoKey,
                    onValueChange = { kakaoKey = it },
                    label = { Text("카카오맵 REST API Key") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nativeKakaoKey,
                    onValueChange = { nativeKakaoKey = it },
                    label = { Text("카카오 네이티브 앱 Key (지도용)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = vehicleId,
                    onValueChange = { vehicleId = it },
                    label = { Text("Vehicle ID") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val newConfig = AppConfig(
                            supabaseUrl = supabaseUrl,
                            supabaseKey = supabaseKey,
                            kakaoKey = kakaoKey,
                            nativeKakaoKey = nativeKakaoKey,
                            userUid = userUid,
                            vehicleId = vehicleId,
                            ntfyTopic = ntfyTopic,
                            accessToken = accessToken
                        )
                        viewModel.saveConfig(context, newConfig)
                        Toast.makeText(context, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("💾 설정 저장하기", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
