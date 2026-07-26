package com.mdkdw1.ui.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeslaRepository(private val config: AppConfig) {

    suspend fun fetchVehicleState(): Result<VehicleState> = withContext(Dispatchers.IO) {
        try {
            val state = VehicleState(
                vehicleName = "Tesla Model Y Long Range",
                batteryLevel = 78,
                usableBatteryLevel = 76,
                isCharging = false,
                chargeState = "Disconnected",
                estimatedRangeKm = 392.5,
                odometerKm = 24850.0,
                insideTempC = 22.0,
                outsideTempC = 19.5,
                isLocked = true,
                isSentryMode = true,
                isClimateOn = false,
                speedKmh = 0.0,
                gear = "P",
                latitude = 37.5665,
                longitude = 126.9780
            )
            Result.success(state)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchLatestDailyTrip(): Result<DailyTrip> = withContext(Dispatchers.IO) {
        try {
            val trip = DailyTrip(
                date = "2026-07-26",
                distanceKm = 42.8,
                energyKwh = 6.4,
                efficiencyWhKm = 149.5,
                batteryUsedPercent = 8.5
            )
            Result.success(trip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchBatteryDegradation(): Result<List<BatteryDegradation>> = withContext(Dispatchers.IO) {
        try {
            val list = listOf(
                BatteryDegradation(5000.0, 78.0, 0.5, "2024-01-15"),
                BatteryDegradation(10000.0, 77.2, 1.5, "2024-06-20"),
                BatteryDegradation(18000.0, 76.5, 2.4, "2025-02-10"),
                BatteryDegradation(24850.0, 75.8, 3.3, "2026-07-26")
            )
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchChargeRecords(): Result<List<ChargeRecord>> = withContext(Dispatchers.IO) {
        try {
            val records = listOf(
                ChargeRecord("1", "2026-07-25 22:30", 20, 80, 45.2, 14200, "강남 슈퍼차저"),
                ChargeRecord("2", "2026-07-22 19:10", 35, 90, 41.5, 12800, "판교 데스티네이션"),
                ChargeRecord("3", "2026-07-18 08:00", 15, 80, 48.9, 15300, "Supercharger Seongnam")
            )
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchConsumables(): Result<List<ConsumableItem>> = withContext(Dispatchers.IO) {
        try {
            val consumables = listOf(
                ConsumableItem("1", "에어컨 캐빈 필터", 10000.0, 20000.0, 24850.0),
                ConsumableItem("2", "타이어 위치 교환", 12000.0, 10000.0, 24850.0),
                ConsumableItem("3", "브레이크 액 점검", 0.0, 40000.0, 24850.0),
                ConsumableItem("4", "와이퍼 블레이드", 15000.0, 15000.0, 24850.0)
            )
            Result.success(consumables)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
