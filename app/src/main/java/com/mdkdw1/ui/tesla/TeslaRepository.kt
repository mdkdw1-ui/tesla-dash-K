package com.mdkdw1.ui.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeslaRepository {

    suspend fun fetchVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        // Supabase / Tesla API 연동 데이터를 수신받아 생성
        return@withContext VehicleState(
            vehicleName = "My Model Y",
            statusText = "주차 중",
            batteryLevel = 78,
            batteryPercent = 78,
            range = 380,
            estimatedRangeKm = 380.0,
            odometer = 45210.5,
            totalOdometer = 45210.5,
            cabinTemp = 21.5,
            insideTempC = 21.5,
            outsideTempC = 18.0,
            isLocked = true,
            isClimateOn = false,
            climateOn = false,
            isCharging = false,
            chargeStatus = "Disconnected",
            isTrunkOpen = false,
            isFrunkOpen = false,
            isSentryModeOn = false,
            speedKmh = 0.0,
            lastUpdatedTimestamp = System.currentTimeMillis() - (3 * 3600 * 1000 + 25 * 60 * 1000),
            flTire = 41.2,
            frTire = 41.5,
            rlTire = 40.8,
            rrTire = 41.0
        )
    }

    suspend fun fetchJournalLogs(): List<JournalLogItem> = withContext(Dispatchers.IO) {
        return@withContext listOf(
            JournalLogItem("1", JournalType.DRIVE, "2026-07-27 07:10", 15.4, 25, 142, 82, 78),
            JournalLogItem("2", JournalType.CHARGE, "2026-07-26 22:00", 0.0, 120, 0, 45, 82),
            JournalLogItem("3", JournalType.DRIVE, "2026-07-26 08:00", 32.1, 45, 155, 60, 46)
        )
    }

    suspend fun fetchBatteryRecords(): List<BatteryRecord> = withContext(Dispatchers.IO) {
        return@withContext List(50) { index ->
            BatteryRecord(
                id = "bat_$index",
                date = "07-${50 - index}",
                degradationPercent = 94.5 + (index * 0.02),
                maxEstimatedRangeKm = 485.0 + (index * 0.1)
            )
        }.reversed()
    }

    suspend fun fetchConsumables(): List<ConsumableItem> = withContext(Dispatchers.IO) {
        return@withContext listOf(
            ConsumableItem("c1", "에어컨 필터", 20000, 30000.0, 45210.5),
            ConsumableItem("c2", "와이퍼 블레이드", 15000, 35000.0, 45210.5),
            ConsumableItem("c3", "브레이크 오일", 40000, 10000.0, 45210.5),
            ConsumableItem("c4", "타이어 위치 교환", 10000, 40000.0, 45210.5)
        )
    }
}
