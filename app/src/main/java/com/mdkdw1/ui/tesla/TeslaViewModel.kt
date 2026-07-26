package com.mdkdw1.ui.tesla

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class TeslaViewModel(application: Application) : AndroidViewModel(application) {

    var vehicleState by mutableStateOf(VehicleState())
        private set

    var settings by mutableStateOf(AppSettings())
        private set

    var driveLogs by mutableStateOf<List<DriveLogItem>>(emptyList())
        private set

    var monthlyReport by mutableStateOf(MonthlyReport())
        private set

    var batteryList by mutableStateOf<List<BatteryDegradationItem>>(emptyList())
        private set

    init {
        loadData()
    }

    private fun loadData() {
        // 샘플 주행기록 초기화 (1km 미만 제외 및 충전 처리 포함)
        val rawLogs = listOf(
            DriveLogItem("1", "2026-07-27 07:10", false, 15.4, 25, 142, 82, 78),
            DriveLogItem("2", "2026-07-26 22:00", true, 0.0, 120, 0, 45, 82),
            DriveLogItem("3", "2026-07-26 18:30", false, 0.5, 3, 210, 46, 45),
            DriveLogItem("4", "2026-07-26 08:00", false, 32.1, 45, 155, 60, 46),
            DriveLogItem("5", "2026-07-25 19:15", false, 8.2, 18, 160, 65, 60)
        )

        driveLogs = rawLogs.filter { log ->
            if (log.isCharging) true else log.distanceKm >= 1.0
        }

        monthlyReport = MonthlyReport(
            monthStr = "2026년 7월",
            totalDistanceKm = 1240.5,
            avgEfficiency = 148,
            totalDriveTimeHours = 32.5,
            topDriveTimeDays = listOf("07-15" to 140, "07-03" to 115, "07-22" to 95, "07-10" to 80, "07-18" to 75),
            topDistanceDays = listOf("07-15" to 185.2, "07-03" to 142.0, "07-22" to 110.5, "07-10" to 98.4, "07-18" to 85.0)
        )

        batteryList = List(50) { index ->
            BatteryDegradationItem(
                date = "07-${50 - index}",
                degradationPercent = 94.5 + (index * 0.02),
                maxEstimatedRangeKm = 485.0 + (index * 0.1)
            )
        }.reversed()
    }

    fun saveSettings(newSettings: AppSettings) {
        settings = newSettings
    }

    fun toggleLock() {
        val nextLock = !vehicleState.isLocked
        val newStatus = if (nextLock) "주차 중" else "잠금 해제됨"
        vehicleState = vehicleState.copy(isLocked = nextLock, statusText = newStatus)
    }

    fun toggleClimate() {
        val nextClimate = !vehicleState.climateOn
        vehicleState = vehicleState.copy(climateOn = nextClimate)
    }

    fun toggleCharging() {
        val nextCharging = !vehicleState.isCharging
        val newStatus = if (nextCharging) "충전 중" else "주차 중"
        vehicleState = vehicleState.copy(isCharging = nextCharging, statusText = newStatus)
    }

    fun syncData() {
        // Supabase / GitHub sync.js API 연동 수행
        loadData()
    }
}
