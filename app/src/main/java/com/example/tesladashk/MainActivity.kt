package com.example.tesladashk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import com.example.tesladashk.service.GuardianService
import com.example.tesladashk.ui.DashboardApp
import com.example.tesladashk.viewmodel.TeslaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TeslaViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            safeStartService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 글로벌 예외 처리기: 앱 내 어디서든 Crash가 나면 스마트폰 화면에 에러문구 렌더링
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val stackTrace = throwable.stackTraceToString()
            runOnUiThread {
                showNativeErrorScreen("🚨 [Global Crash Detected]\n\n$stackTrace")
            }
        }

        // 2. UI 렌더링 및 예외 포착
        try {
            setContent {
                MaterialTheme {
                    DashboardApp(viewModel = viewModel)
                }
            }

            // 3. 서비스 시작 및 예외 포착
            checkAndStartService()

        } catch (e: Throwable) {
            showNativeErrorScreen("🚨 [Startup Error]\n\n${e.stackTraceToString()}")
        }
    }

    // Compose가 고장 나도 켜지는 최상위 순수 안드로이드 에러 화면
    private fun showNativeErrorScreen(errorText: String) {
        val scrollView = ScrollView(this).apply {
            setBackgroundColor(Color.BLACK)
            setPadding(40, 60, 40, 60)
        }
        val textView = TextView(this).apply {
            text = errorText
            setTextColor(Color.YELLOW)
            textSize = 11f
        }
        scrollView.addView(textView)
        setContentView(scrollView)
    }

    private fun checkAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                safeStartService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            safeStartService()
        }
    }

    private fun safeStartService() {
        try {
            val serviceIntent = Intent(this, GuardianService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } catch (e: Exception) {
            showNativeErrorScreen("🚨 [Foreground Service Launch Error]\n\n${e.stackTraceToString()}")
        }
    }
}
