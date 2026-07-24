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
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardApp(
                        viewModel = viewModel,
                        onToggleService = { isChecked ->
                            val intent = Intent(this, GuardianService::class.java)
                            if (isChecked) {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    startForegroundService(intent)
                                } else {
                                    startService(intent)
                                }
                            } else {
                                stopService(intent)
                            }
                        }
                    )
                }
            }
        }
    }
}
