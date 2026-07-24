package com.example.tesladashk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.tesladashk.service.GuardianService
import com.example.tesladashk.ui.DashboardApp
import com.example.tesladashk.viewmodel.TeslaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TeslaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 앱이 꺼져 있던 상태에서 테슬라 로그인 딥링크로 실행되었을 때 처리
        handleDeepLink(intent)
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardApp(
                        viewModel = viewModel,
                        onToggleService = { isChecked ->
                            val serviceIntent = Intent(this, GuardianService::class.java)
                            if (isChecked) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    startForegroundService(serviceIntent)
                                } else {
                                    startService(serviceIntent)
                                }
                            } else {
                                stopService(serviceIntent)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        // 앱이 이미 켜져 있는 상태에서 테슬라 로그인 후 돌아올 때 처리
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        intent?.data?.let { uri ->
            if (uri.scheme == "tesladashk" && uri.host == "oauth-callback") {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    // TODO: 테슬라 인증 코드(code)를 이용해 백엔드 토큰 교환 요청 수행
                }
            }
        }
    }
}
