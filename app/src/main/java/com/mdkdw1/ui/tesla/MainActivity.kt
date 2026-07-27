package com.mdkdw1.ui.tesla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val encryptedSettingsManager = EncryptedSettingsManager(applicationContext)
        val repository = TeslaRepository(encryptedSettingsManager)

        val factory = TeslaViewModelFactory(repository, encryptedSettingsManager)
        val viewModel = ViewModelProvider(this, factory)[TeslaViewModel::class.java]

        setContent {
            TeslaMainScreen(viewModel = viewModel)
        }
    }
}

class TeslaViewModelFactory(
    private val repository: TeslaRepository,
    private val encryptedSettingsManager: EncryptedSettingsManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeslaViewModel::class.java)) {
            return TeslaViewModel(repository, encryptedSettingsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
