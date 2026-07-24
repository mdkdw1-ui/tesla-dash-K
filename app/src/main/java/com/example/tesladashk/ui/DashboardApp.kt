package com.example.tesladashk.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tesladashk.ui.screens.GuardianScreen
import com.example.tesladashk.ui.screens.MonitorScreen
import com.example.tesladashk.viewmodel.TeslaViewModel

private object AppConfigManager {
    private const val PREF_NAME = "tesla_app_config"

    private fun getPrefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun isConfigured(context: Context): Boolean {
        return getPrefs(context).getString("api_url", null)?.isNotBlank() == true
    }

    fun saveConfig(context: Context, apiUrl: String, apiKey: String) {
        getPrefs(context).edit()
            .putString("api_url", apiUrl)
            .putString("api_key", apiKey)
            .apply()
    }

    fun getApiUrl(context: Context): String = getPrefs(context).getString("api_url", "") ?: ""
    fun getApiKey(context: Context): String = getPrefs(context).getString("api_key", "") ?: ""
}

enum class MainTab(val title: String) {
    MONITOR("테슬라 모니터"),
    GUARDIAN("감시 가디언")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardApp(viewModel: TeslaViewModel = viewModel()) {
    val context = LocalContext.current
    var isConfigured by remember { mutableStateOf(AppConfigManager.isConfigured(context)) }
    var currentMainTab by remember { mutableStateOf(MainTab.MONITOR) }
    var showSettingsScreen by remember { mutableStateOf(false) }

    if (!isConfigured || showSettingsScreen) {
        InlineSettingsScreen(
            onSaved = {
                isConfigured = true
                showSettingsScreen = false
            }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Tesla Command Hub", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Monitor & Guardian", color = Color.Gray, fontSize = 11.sp)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "최신 데이터를 불러옵니다...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = Color(0xFF00E676))
                    }
                    IconButton(onClick = { showSettingsScreen = true }) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.LightGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF111113))
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1C1C1E)) {
                MainTab.values().forEach { tab ->
                    NavigationBarItem(
                        selected = currentMainTab == tab,
                        onClick = { currentMainTab = tab },
                        label = { Text(tab.title, color = if (currentMainTab == tab) Color.White else Color.Gray) },
                        icon = {
                            Icon(
                                imageVector = if (tab == MainTab.MONITOR) Icons.Default.Home else Icons.Default.Lock,
                                contentDescription = tab.title,
                                tint = if (currentMainTab == tab) Color(0xFF2997FF) else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFF2C2C2E))
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            when (currentMainTab) {
                MainTab.MONITOR -> MonitorScreen(viewModel = viewModel)
                MainTab.GUARDIAN -> GuardianScreen(logs = emptyList(), onSendAlert = { _, _ -> })
            }
        }
    }
}

@Composable
private fun InlineSettingsScreen(onSaved: () -> Unit) {
    val context = LocalContext.current
    var apiUrl by remember { mutableStateOf(AppConfigManager.getApiUrl(context)) }
    var apiKey by remember { mutableStateOf(AppConfigManager.getApiKey(context)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111113))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Tesla API 연동 설정",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "차량 데이터 연동을 위한 API 주소와 인증 키를 입력해 주세요. 입력한 정보는 안전하게 저장됩니다.",
                    color = Color.Gray,
                    fontSize = 12.sp
                )

                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { apiUrl = it },
                    label = { Text("API 서버 URL") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key / Access Token") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )

                Button(
                    onClick = {
                        if (apiUrl.isNotBlank()) {
                            AppConfigManager.saveConfig(context, apiUrl, apiKey)
                            Toast.makeText(context, "설정이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                            onSaved()
                        } else {
                            Toast.makeText(context, "API URL을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE82127)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "저장하고 대시보드 진입", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
