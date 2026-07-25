package com.example.tesladashk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.tesladashk.ui.DashboardApp
import com.example.tesladashk.ui.theme.TeslaDashTheme
import com.example.tesladashk.viewmodel.TeslaViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TeslaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeslaDashTheme {
                DashboardApp(viewModel = viewModel)
            }
        }
    }
}
