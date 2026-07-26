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
    private val repository = TeslaRepository(settingsManager)

    // UI 상태
    private val _currentMainTab = MutableStateFlow(MainTab.MONITOR)
    val currentMainTab: StateFlow<MainTab> = _currentMainTab.asStateFlow()

    private val _currentSubTab = MutableStateFlow(MonitorSubTab.VEHICLE)
    val currentSubTab: StateFlow<MonitorSubTab> = _currentSubTab.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _journalLogs = MutableStateFlow<List<JournalLogItem>>(emptyList())
    val journalLogs: StateFlow<List<JournalLogItem>> = _journalLogs.asStateFlow()

    private val _recentDriveSummary = MutableStateFlow(DailyDriveSummary())
    val recentDriveSummary: StateFlow<DailyDriveSummary> = _recentDriveSummary.asStateFlow()

    private val _monthlyReport = MutableStateFlow<MonthlyReport?>(null)
    val monthlyReport: StateFlow<MonthlyReport?> = _monthlyReport.asStateFlow()

    private val _batteryRecords = MutableStateFlow<List<BatteryRecord>>(emptyList())
    val batteryRecords: StateFlow<List<BatteryRecord>> = _batteryRecords.asStateFlow()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    init {
        loadSettings()
        refreshData()
    }

    fun selectMainTab(tab: MainTab) {
        _currentMainTab.value = tab
    }

    fun selectSubTab(tab: MonitorSubTab) {
        _currentSubTab.value = tab
    }

    fun toggleSettingsDialog(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun loadSettings() {
        _appSettings.value = settingsManager.loadSettings()
    }

    fun saveSettings(url: String, key: String, githubToken: String) {
        val updated = AppSettings(url, key, githubToken)
        settingsManager.saveSettings(updated)
        _appSettings.value = updated
        _showSettingsDialog.value = false
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.triggerDataSync()
            _vehicleState.value = repository.getVehicleState()
            _journalLogs.value = repository.getJournalLogs()
            _recentDriveSummary.value = repository.getRecentDriveSummary()
            _monthlyReport.value = repository.getMonthlyReport()
            _batteryRecords.value = repository.getRecent50BatteryRecords()
            _isSyncing.value = false
        }
    }
}
