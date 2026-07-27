package com.mdkdw1.ui.tesla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsManager = EncryptedSettingsManager(applicationContext)
        val initialSettings = settingsManager.loadSettings()
        val repository = TeslaRepository(initialSettings)
        val factory = TeslaViewModelFactory(settingsManager, repository)

        val viewModel = ViewModelProvider(this, factory)[TeslaViewModel::class.java]

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    TeslaMainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
