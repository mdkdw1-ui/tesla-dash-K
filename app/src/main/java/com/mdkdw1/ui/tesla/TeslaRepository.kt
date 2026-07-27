package com.mdkdw1.ui.tesla

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TeslaRepository(private val settingsManager: EncryptedSettingsManager) {

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState

    private val _settings = MutableStateFlow(settingsManager.getSettings())
    val settings: StateFlow<AppSettings> = _settings

    fun getSettings(): AppSettings {
        return settingsManager.getSettings()
    }

    fun saveSettings(newSettings: AppSettings) {
        settingsManager.saveSettings(newSettings)
        _settings.value = newSettings
    }

    fun toggleLock() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(
            isLocked = !current.isLocked,
            statusText = if (!current.isLocked) "잠김" else "잠금 해제됨"
        )
    }

    fun toggleClimate() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(
            climateOn = !current.climateOn
        )
    }

    fun toggleCharging() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(
            isCharging = !current.isCharging,
            statusText = if (!current.isCharging) "충전 중" else "주차됨"
        )
    }

    fun toggleSentryMode() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(
            sentryModeOn = !current.sentryModeOn
        )
    }

    fun getDriveLogs(): List<DriveLogItem> {
        return listOf(
            DriveLogItem("1", "2026-07-26", 45.2, 50, 8.5, "서울 강남구", "경기 성남시", 188.0),
            DriveLogItem("2", "2026-07-25", 112.0, 95, 21.0, "경기 성남시", "강원 원주시", 187.5),
            DriveLogItem("3", "2026-07-24", 18.5, 25, 3.2, "서울 강남구", "서울 마포구", 172.9)
        )
    }

    fun getMonthlyReports(): List<MonthlyReport> {
        return listOf(
            MonthlyReport("2026-07", 1250.0, 230.5, 45000, 184.4),
            MonthlyReport("2026-06", 1420.0, 260.0, 52000, 183.1),
            MonthlyReport("2026-05", 980.0, 175.0, 34000, 178.5)
        )
    }

    fun getBatteryDegradationData(): List<BatteryDegradationItem> {
        return listOf(
            BatteryDegradationItem("2026-07", 25000.0, 75.0, 2.1, 470.0),
            BatteryDegradationItem("2026-01", 18000.0, 75.8, 1.5, 473.0),
            BatteryDegradationItem("2025-07", 10000.0, 76.2, 0.8, 477.0)
        )
    }

    fun getSentryEvents(): List<SentryEventItem> {
        return listOf(
            SentryEventItem("s1", "2026-07-27 10:15", "강남역 주차장", "전면 카메라", "https://example.com/s1.mp4"),
            SentryEventItem("s2", "2026-07-26 18:40", "판교 테크노밸리", "좌측 카메라", "https://example.com/s2.mp4")
        )
    }

    fun getConsumables(): List<ConsumableItem> {
        return listOf(
            ConsumableItem("c1", "에어컨 필터", "2026-01-15", 15000.0, 20000.0, 12, 75.0),
            ConsumableItem("c2", "와이퍼 블레이드", "2025-11-10", 12000.0, 15000.0, 12, 80.0),
            ConsumableItem("c3", "타이어 위치 교환", "2026-03-01", 18000.0, 10000.0, 6, 90.0)
        )
    }

    fun getJournalLogs(): List<JournalLogItem> {
        return listOf(
            JournalLogItem("j1", JournalType.CHARGE, "2026-07-26", "슈퍼차저 급속 충전", "80%까지 충전 완료", 12500),
            JournalLogItem("j2", JournalType.MAINTENANCE, "2026-06-10", "타이어 공기압 점검", "전륜 공기압 보정", 0)
        )
    }
}
