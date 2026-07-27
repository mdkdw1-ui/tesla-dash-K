package com.mdkdw1.ui.tesla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(
    private val repository: TeslaRepository,
    private val encryptedSettingsManager: EncryptedSettingsManager
) : ViewModel() {

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _driveLogs = MutableStateFlow<List<DriveLogItem>>(emptyList())
    val driveLogs: StateFlow<List<DriveLogItem>> = _driveLogs.asStateFlow()

    private val _monthlyReport = MutableStateFlow(MonthlyReport())
    val monthlyReport: StateFlow<MonthlyReport> = _monthlyReport.asStateFlow()

    private val _batteryList = MutableStateFlow<List<BatteryDegradationItem>>(emptyList())
    val batteryList: StateFlow<List<BatteryDegradationItem>> = _batteryList.asStateFlow()

    private val _sentryEvents = MutableStateFlow<List<SentryEventItem>>(emptyList())
    val sentryEvents: StateFlow<List<SentryEventItem>> = _sentryEvents.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _mainTabState = MutableStateFlow(0) // 0: 테슬라 모니터, 1: 감시 가디언
    val mainTabState: StateFlow<Int> = _mainTabState.asStateFlow()

    private val _subTabState = MutableStateFlow(0)  // 0: 차량정보, 1: 주행정보, 2: 월간리포트, 3: 배터리
    val subTabState: StateFlow<Int> = _subTabState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val savedSettings = encryptedSettingsManager.loadSettings()
            _settings.value = savedSettings

            _driveLogs.value = listOf(
                DriveLogItem("1", "2026-07-26 18:30", 42.5, 45, 142, 82, 73, false, "서울 강남", "경기 성남"),
                DriveLogItem("2", "2026-07-25 09:10", 15.2, 22, 138, 88, 85, false, "서울 서초", "서울 마포"),
                DriveLogItem("3", "2026-07-24 22:00", 0.0, 60, 0, 40, 88, true, "슈퍼차저 강남", "슈퍼차저 강남"),
                DriveLogItem("4", "2026-07-23 14:15", 88.0, 78, 155, 95, 75, false, "서울 강남", "강원 춘천"),
                DriveLogItem("5", "2026-07-22 08:30", 0.6, 3, 180, 76, 75, false, "단거리 이동", "단거리 이동")
            )

            _batteryList.value = (1..15).map { i ->
                BatteryDegradationItem(
                    id = "$i",
                    date = "2026-07-${30 - i}",
                    degradationPercent = 4.2 + (i * 0.05),
                    maxEstimatedRangeKm = 482.0 - (i * 0.2),
                    sohPercent = 95.8 - (i * 0.05)
                )
            }

            _sentryEvents.value = listOf(
                SentryEventItem("s1", "2026-07-27 10:14", "움직임 감지", "주차장 B2 A-04"),
                SentryEventItem("s2", "2026-07-26 21:05", "충격 감지 (경고)", "주차장 B1 C-12"),
                SentryEventItem("s3", "2026-07-25 15:30", "문열림 시도 감지", "야외 주차장")
            )
        }
    }

    fun setMainTab(index: Int) {
        _mainTabState.value = index
    }

    fun setSubTab(index: Int) {
        _subTabState.value = index
    }

    fun syncData() {
        viewModelScope.launch {
            _isSyncing.value = true
            _vehicleState.value = _vehicleState.value.copy(
                lastUpdatedTimestamp = System.currentTimeMillis()
            )
            _isSyncing.value = false
        }
    }

    fun toggleLock() {
        _vehicleState.value = _vehicleState.value.copy(isLocked = !_vehicleState.value.isLocked)
    }

    fun toggleClimate() {
        _vehicleState.value = _vehicleState.value.copy(climateOn = !_vehicleState.value.climateOn)
    }

    fun toggleCharging() {
        _vehicleState.value = _vehicleState.value.copy(isCharging = !_vehicleState.value.isCharging)
    }

    fun openTrunk() {}
    fun openFrunk() {}
    fun honkHorn() {}
    fun flashLights() {}

    fun toggleSentry() {
        _vehicleState.value = _vehicleState.value.copy(sentryModeOn = !_vehicleState.value.sentryModeOn)
    }

    fun saveSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            _settings.value = newSettings
            encryptedSettingsManager.saveSettings(newSettings)
        }
    }
}
