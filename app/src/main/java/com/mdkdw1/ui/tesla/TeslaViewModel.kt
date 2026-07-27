package com.mdkdw1.ui.tesla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(
    private val repository: TeslaRepository
) : ViewModel() {

    // 탭 상태 (0: 테슬라 모니터, 1: 감시 가디언)
    private val _currentMainTab = MutableStateFlow(0)
    val currentMainTab: StateFlow<Int> = _currentMainTab.asStateFlow()

    // 테슬라 모니터 하위 서브 탭 (0: 주행 기록, 1: 배터리 & 충전, 2: 소모품 관리, 3: 내 차량 정보)
    private val _currentSubTab = MutableStateFlow(0)
    val currentSubTab: StateFlow<Int> = _currentSubTab.asStateFlow()

    // 앱 설정
    private val _settings = MutableStateFlow(repository.getSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // 차량 정보 및 상태
    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    // 주행 기록
    private val _driveLogs = MutableStateFlow<List<DriveLogItem>>(emptyList())
    val driveLogs: StateFlow<List<DriveLogItem>> = _driveLogs.asStateFlow()

    // 배터리 열화
    private val _batteryDegradation = MutableStateFlow<List<BatteryDegradationItem>>(emptyList())
    val batteryDegradation: StateFlow<List<BatteryDegradationItem>> = _batteryDegradation.asStateFlow()

    // 감시 가디언 이벤트
    private val _sentryEvents = MutableStateFlow<List<SentryEventItem>>(emptyList())
    val sentryEvents: StateFlow<List<SentryEventItem>> = _sentryEvents.asStateFlow()

    // 설정 다이얼로그 표시 여부
    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshData()
    }

    fun setMainTab(tabIndex: Int) {
        _currentMainTab.value = tabIndex
    }

    fun setSubTab(tabIndex: Int) {
        _currentSubTab.value = tabIndex
    }

    fun openSettings() {
        _showSettingsDialog.value = true
    }

    fun closeSettings() {
        _showSettingsDialog.value = false
    }

    fun saveSettings(newSettings: AppSettings) {
        repository.saveSettings(newSettings)
        _settings.value = newSettings
        closeSettings()
        refreshData()
    }

    fun toggleSentryMode(enabled: Boolean) {
        _vehicleState.value = _vehicleState.value.copy(sentryModeOn = enabled)
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _vehicleState.value = repository.fetchVehicleState()
                _driveLogs.value = repository.fetchDriveLogs()
                _batteryDegradation.value = repository.fetchBatteryDegradation()
                _sentryEvents.value = repository.fetchSentryEvents()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

class TeslaViewModelFactory(
    private val repository: TeslaRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeslaViewModel::class.java)) {
            return TeslaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
