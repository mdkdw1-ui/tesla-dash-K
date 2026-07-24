package com.example.tesladashk

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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

    // Android 13+ 알림 권한 요청 런처
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startGuardianService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. UI 화면을 먼저 띄웁니다.
        setContent {
            MaterialTheme {
                DashboardApp(viewModel = viewModel)
            }
        }

        // 2. 권한 확인 후 안전하게 백그라운드 서비스 시작
        checkAndStartService()
    }

    private fun checkAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startGuardianService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startGuardianService()
        }
    }

    private fun startGuardianService() {
        try {
            val serviceIntent = Intent(this, GuardianService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
