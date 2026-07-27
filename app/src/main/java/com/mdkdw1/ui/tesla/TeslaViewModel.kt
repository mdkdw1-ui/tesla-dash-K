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

    val vehicleState: StateFlow<VehicleState> = repository.vehicleState
    val settings: StateFlow<AppSettings> = repository.settings

    private val _driveLogs = MutableStateFlow<List<DriveLogItem>>(emptyList())
    val driveLogs: StateFlow<List<DriveLogItem>> = _driveLogs.asStateFlow()

    private val _monthlyReports = MutableStateFlow<List<MonthlyReport>>(emptyList())
    val monthlyReports: StateFlow<List<MonthlyReport>> = _monthlyReports.asStateFlow()

    private val _batteryDegradationList = MutableStateFlow<List<BatteryDegradationItem>>(emptyList())
    val batteryDegradationList: StateFlow<List<BatteryDegradationItem>> = _batteryDegradationList.asStateFlow()

    private val _sentryEvents = MutableStateFlow<List<SentryEventItem>>(emptyList())
    val sentryEvents: StateFlow<List<SentryEventItem>> = _sentryEvents.asStateFlow()

    private val _consumables = MutableStateFlow<List<ConsumableItem>>(emptyList())
    val consumables: StateFlow<List<ConsumableItem>> = _consumables.asStateFlow()

    private val _journalLogs = MutableStateFlow<List<JournalLogItem>>(emptyList())
    val journalLogs: StateFlow<List<JournalLogItem>> = _journalLogs.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _driveLogs.value = repository.getDriveLogs()
            _monthlyReports.value = repository.getMonthlyReports()
            _batteryDegradationList.value = repository.getBatteryDegradationData()
            _sentryEvents.value = repository.getSentryEvents()
            _consumables.value = repository.getConsumables()
            _journalLogs.value = repository.getJournalLogs()
        }
    }

    fun toggleLock() {
        repository.toggleLock()
    }

    fun toggleClimate() {
        repository.toggleClimate()
    }

    fun toggleCharging() {
        repository.toggleCharging()
    }

    fun toggleSentryMode() {
        repository.toggleSentryMode()
    }

    fun saveSettings(newSettings: AppSettings) {
        repository.saveSettings(newSettings)
    }
}
