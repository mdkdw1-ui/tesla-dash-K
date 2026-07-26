package com.mdkdw1.ui.tesla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeslaUiState(
    val isLoading: Boolean = false,
    val isConnected: Boolean = false,
    val vehicleState: VehicleState = VehicleState(),
    val journalLogs: List<JournalLogItem> = emptyList(),
    val batteryRecords: List<BatteryRecord> = emptyList(),
    val consumables: List<ConsumableItem> = emptyList(),
    val dailySummary: DailyDriveSummary = DailyDriveSummary(),
    val monthlyReport: MonthlyReport = MonthlyReport(),
    val appSettings: AppSettings = AppSettings(),
    val selectedMainTab: MainTab = MainTab.MONITOR,
    val selectedSubTab: MonitorSubTab = MonitorSubTab.VEHICLE
)

class TeslaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TeslaRepository(application)
    private val settingsManager = EncryptedSettingsManager(application)

    private val _uiState = MutableStateFlow(TeslaUiState())
    val uiState: StateFlow<TeslaUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        refreshAllData()
    }

    fun loadSettings() {
        val savedSettings = settingsManager.loadSettings()
        _uiState.update { it.copy(appSettings = savedSettings) }
    }

    fun saveSettings(newSettings: AppSettings) {
        settingsManager.saveSettings(newSettings)
        _uiState.update { it.copy(appSettings = newSettings) }
        repository.reinitializeSupabase()
        refreshAllData()
    }

    fun selectMainTab(tab: MainTab) {
        _uiState.update { it.copy(selectedMainTab = tab) }
    }

    fun selectSubTab(tab: MonitorSubTab) {
        _uiState.update { it.copy(selectedSubTab = tab) }
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val isConn = repository.isConnectedToSupabase()
            val vehicle = repository.fetchVehicleState()
            val journals = repository.fetchJournalLogs()
            val batteries = repository.fetchBatteryRecords()
            val consumables = repository.fetchConsumables()

            // 주행 일지 요약 연산
            val driveLogs = journals.filter { it.type == JournalType.DRIVE }
            val totalDist = driveLogs.sumOf { it.distanceKm }
            val totalDuration = driveLogs.sumOf { it.durationMinutes }
            val avgEff = if (driveLogs.isNotEmpty()) driveLogs.map { it.efficiencyWhKm }.average().toInt() else 0

            val summary = DailyDriveSummary(
                dateText = "최근 운행 데이터",
                totalDistanceKm = totalDist,
                totalDurationMinutes = totalDuration,
                avgEfficiencyWhKm = avgEff,
                driveCount = driveLogs.size
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    isConnected = isConn,
                    vehicleState = vehicle,
                    journalLogs = journals,
                    batteryRecords = batteries,
                    consumables = consumables,
                    dailySummary = summary
                )
            }
        }
    }
}
