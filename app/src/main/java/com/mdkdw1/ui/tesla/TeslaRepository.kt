package com.mdkdw1.ui.tesla

import android.content.Context

class TeslaRepository(private val context: Context) {
    private val settingsManager = EncryptedSettingsManager(context)

    fun getSettings(): AppConfig {
        return settingsManager.loadSettings()
    }

    fun saveSettings(config: AppConfig) {
        settingsManager.saveSettings(config)
    }

    suspend fun fetchVehicleData(): VehicleState {
        // Supabase / Tesla API 연동 지점 (실제 네트워크 연동 시 API 요청 처리)
        return VehicleState(
            vehicleName = "Model Y Long Range",
            batteryLevel = 82,
            estimatedRangeKm = 412,
            odometerKm = 35240,
            isLocked = true,
            isClimateOn = false,
            insideTempC = 21.5,
            outsideTempC = 24.0,
            isTrunkOpen = false,
            isFrunkOpen = false,
            isSentryModeOn = true,
            speedKmh = 0,
            chargeStatus = "Discharging"
        )
    }

    suspend fun fetchConsumableItems(): List<ConsumableItem> {
        return listOf(
            ConsumableItem("에어컨 캐빈 필터", 12500, 20000, "2025-10-15"),
            ConsumableItem("타이어 위치 교환", 8000, 10000, "2026-01-10"),
            ConsumableItem("브레이크 오일 점검", 22000, 40000, "2025-03-20"),
            ConsumableItem("와이퍼 블레이드", 5000, 15000, "2025-11-05")
        )
    }

    suspend fun fetchDailyTrips(): List<DailyTrip> {
        return listOf(
            DailyTrip("2026-07-26", 42.5, 152.0, 6.46),
            DailyTrip("2026-07-25", 18.2, 168.0, 3.05),
            DailyTrip("2026-07-24", 85.0, 145.0, 12.32),
            DailyTrip("2026-07-23", 31.4, 158.0, 4.96),
            DailyTrip("2026-07-22", 64.0, 150.0, 9.60)
        )
    }

    suspend fun fetchChargeRecords(): List<ChargeRecord> {
        return listOf(
            ChargeRecord("2026-07-25", 45.0, 13500, "수원 수퍼차저"),
            ChargeRecord("2026-07-21", 52.0, 15600, "판교 완속 충전소"),
            ChargeRecord("2026-07-18", 38.5, 11550, "강남 수퍼차저")
        )
    }

    suspend fun fetchDegradationData(): List<BatteryDegradationPoint> {
        return listOf(
            BatteryDegradationPoint(5000, 79.5, 0.6),
            BatteryDegradationPoint(10000, 78.8, 1.5),
            BatteryDegradationPoint(18000, 78.2, 2.25),
            BatteryDegradationPoint(25000, 77.6, 3.0),
            BatteryDegradationPoint(35000, 76.8, 4.0)
        )
    }

    suspend fun sendCommand(command: String): Boolean {
        // 제어 명령 전송 성공 처리
        return true
    }

    suspend fun resetConsumable(itemName: String): Boolean {
        // 소모품 교체 주기 리셋 처리
        return true
    }
}
