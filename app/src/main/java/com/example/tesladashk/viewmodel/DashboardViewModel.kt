package com.example.tesladashk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.AppConfig
import com.example.tesladashk.network.VehicleRow
import com.example.tesladashk.utils.ConfigStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private val _vehicleStates = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleStates: StateFlow<List<VehicleRow>> = _vehicleStates.asStateFlow()

    fun loadInitialConfig(context: Context) {
        val loaded = ConfigStorage.loadConfig(context)
        _config.value = loaded
    }

    fun saveConfig(context: Context, newConfig: AppConfig) {
        _config.value = newConfig
        ConfigStorage.saveConfig(context, newConfig)
    }

    fun triggerSyncAndFetch() {
        viewModelScope.launch {
            // Vercel Sync 및 Supabase 데이터 갱신 로직 실행
        }
    }
}
