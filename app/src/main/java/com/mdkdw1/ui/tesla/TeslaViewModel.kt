package com.mdkdw1.ui.tesla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(
    private val repository: TeslaRepository = TeslaRepository(),
    private val encryptedSettingsManager: EncryptedSettingsManager? = null
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

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            encryptedSettingsManager?.let { manager ->
                _settings.value = manager.loadSettings()
            }

            val state = repository.fetchVehicleState()
            _vehicleState.value = state

            val journals = repository.fetchJournalLogs()
            _driveLogs.value = journals.map { journal ->
                DriveLogItem(
                    id = journal.id,
                    date = journal.date,
                    isCharging = journal.type == JournalType.CHARGE,
                    distanceKm = journal.distanceKm,
                    durationMinutes = journal.durationMinutes,
                    efficiencyWhKm = journal.efficiencyWhKm,
                    batteryStart = journal.batteryStart,
                    batteryEnd = journal.batteryEnd
                )
            }

            val batteryRecords = repository.fetchBatteryRecords()
            _batteryList.value = batteryRecords.map { record ->
                BatteryDegradationItem(
                    date = record.date,
                    degradationPercent = record.degradationPercent,
                    maxEstimatedRangeKm = record.maxEstimatedRangeKm
                )
            }

            // 월간 리포트 데이터 세팅
            _monthlyReport.value = MonthlyReport(
                monthStr = "2026년 7월",
                totalDistanceKm = 1240.5,
                avgEfficiency = 148,
                totalDriveTimeHours = 32.5,
                topDriveTimeDays = listOf("07-15" to 140, "07-22" to 125, "07-10" to 110, "07-05" to 95, "07-18" to 80),
                topDistanceDays = listOf("07-15" to 185.2, "07-22" to 142.0, "07-10" to 120.5, "07-05" to 98.4, "07-18" to 85.0)
            )
        }
    }

    fun syncData() {
        loadData()
    }

    fun saveSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            encryptedSettingsManager?.saveSettings(newSettings)
            _settings.value = newSettings
        }
    }

    fun toggleLock() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(isLocked = !current.isLocked)
    }

    fun toggleClimate() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(
            isClimateOn = !current.isClimateOn,
            climateOn = !current.climateOn
        )
    }

    fun toggleCharging() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(isCharging = !current.isCharging)
    }
}
