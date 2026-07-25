package com.example.tesladashk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.AppConfig
import com.example.tesladashk.network.DrivingTrip
import com.example.tesladashk.network.VehicleRow
import com.example.tesladashk.utils.ConfigStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private val _vehicleRows = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleRows: StateFlow<List<VehicleRow>> = _vehicleRows.asStateFlow()

    private val _trips = MutableStateFlow<List<DrivingTrip>>(emptyList())
    val trips: StateFlow<List<DrivingTrip>> = _trips.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("-")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

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
            _isLoading.value = true
            // 동기화 처리 완료 후 로딩 해제
            _isLoading.value = false
        }
    }
}
