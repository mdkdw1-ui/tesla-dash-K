package com.mdkdw1.ui.tesla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = EncryptedSettingsManager(application)

    private val _appSettings = MutableStateFlow(settingsManager.getSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _chargingHistory = MutableStateFlow<List<ChargingSession>>(emptyList())
    val chargingHistory: StateFlow<List<ChargingSession>> = _chargingHistory.asStateFlow()

    private val _batteryDegradation = MutableStateFlow<List<BatteryDegradationData>>(emptyList())
    val batteryDegradation: StateFlow<List<BatteryDegradationData>> = _batteryDegradation.asStateFlow()

    private val _consumables = MutableStateFlow<List<ConsumableItem>>(emptyList())
    val consumables: StateFlow<List<ConsumableItem>> = _consumables.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _batteryDegradation.value = listOf(
            BatteryDegradationData("2024-01", 1000, 0.5f, 500),
            BatteryDegradationData("2024-03", 5000, 1.2f, 496),
            BatteryDegradationData("2024-06", 10000, 2.1f, 491),
            BatteryDegradationData("2024-09", 15000, 2.8f, 488)
        )
        _chargingHistory.value = listOf(
            ChargingSession("1", "2024-09-20", "강남 슈퍼차저", 20, 80, 42.5, 12500, 80),
            ChargingSession("2", "2024-09-22", "집 완속 충전기", 35, 90, 38.0, 7600, 90)
        )
        _consumables.value = listOf(
            ConsumableItem("1", "에어컨 필터", 0, 15000, 12000),
            ConsumableItem("2", "와이퍼 블레이드", 0, 20000, 8000),
            ConsumableItem("3", "타이어 위치 교환", 0, 10000, 9500),
            ConsumableItem("4", "브레이크 오일", 0, 40000, 15000)
        )
    }

    fun refreshData() {
        viewModelScope.launch {
            loadData()
        }
    }

    fun saveSettings(newSettings: AppSettings) {
        settingsManager.saveSettings(newSettings)
        _appSettings.value = newSettings
        refreshData()
    }

    fun clearSettings() {
        settingsManager.clearSettings()
        _appSettings.value = AppSettings()
    }

    fun toggleDoorLock() {
        _vehicleState.value = _vehicleState.value.copy(isLocked = !_vehicleState.value.isLocked)
    }

    fun toggleClimate(enable: Boolean? = null) {
        val current = _vehicleState.value.isClimateOn
        _vehicleState.value = _vehicleState.value.copy(isClimateOn = enable ?: !current)
    }

    fun toggleSentryMode() {
        _vehicleState.value = _vehicleState.value.copy(isSentryOn = !_vehicleState.value.isSentryOn)
    }

    fun openTrunk() {
        // 트렁크 개폐 제어 로직
    }

    fun openFrunk() {
        // 프렁크 개폐 제어 로직
    }

    fun toggleChargePort() {
        _vehicleState.value = _vehicleState.value.copy(isChargePortOpen = !_vehicleState.value.isChargePortOpen)
    }

    fun flashLights() {
        // 전조등 점등 제어 로직
    }

    fun honkHorn() {
        // 경적 울림 제어 로직
    }

    fun adjustTargetTemp(delta: Float) {
        val newTemp = (_vehicleState.value.targetTemp + delta).coerceIn(16f, 30f)
        _vehicleState.value = _vehicleState.value.copy(targetTemp = newTemp)
    }
}
