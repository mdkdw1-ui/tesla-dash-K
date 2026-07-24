package com.example.tesladashk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.tesladashk.service.GuardianService
import com.example.tesladashk.ui.DashboardApp
import com.example.tesladashk.viewmodel.TeslaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TeslaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // GuardianService 시작
        val serviceIntent = Intent(this, GuardianService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }

        setContent {
            MaterialTheme {
                DashboardApp(viewModel = viewModel)
            }
        }
    }
}
