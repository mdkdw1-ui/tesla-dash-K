package com.mdkdw1.ui.tesla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsManager = SecureSettingsManager(applicationContext)
        val repository = TeslaHubRepository()

        setContent {
            MainHubScreen(
                repository = repository,
                settingsManager = settingsManager
            )
        }
    }
}
