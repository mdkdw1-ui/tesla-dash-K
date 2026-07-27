package com.mdkdw1/ui/tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeslaRepository {

    suspend fun fetchVehicleState(settings: AppSettings): VehicleState = withContext(Dispatchers.IO) {
        VehicleState(
            vehicleName = "Model Y Long Range",
            batteryLevel = 82,
            estimatedRangeKm = 412,
            isCharging = false,
            chargingPowerKw = 0.0,
            insideTempC = 21.5,
            outsideTempC = 18.0,
            isLocked = true,
            climateOn = false,
            sentryMode = true,
            trunkOpen = false,
            frunkOpen = false,
            totalMileageKm = 34500,
            lastUpdated = "방금 전"
        )
    }

    suspend fun fetchBatteryDegradation(): List<DegradationRecord> = withContext(Dispatchers.IO) {
        listOf(
            DegradationRecord("2023-01", 5000, 99.5f, 78.0f),
            DegradationRecord("2023-06", 12000, 98.2f, 77.0f),
            DegradationRecord("2023-12", 20000, 97.1f, 76.1f),
            DegradationRecord("2024-03", 28000, 96.0f, 75.2f),
            DegradationRecord("2024-07", 34500, 95.2f, 74.6f)
        )
    }

    suspend fun fetchChargingHistory(): List<ChargeRecord> = withContext(Dispatchers.IO) {
        listOf(
            ChargeRecord("chg_1", "2024-07-25", "수원 슈퍼차저", 45.2f, 32, 14200, 20, 80),
            ChargeRecord("chg_2", "2024-07-20", "집밥 완속충전", 38.0f, 360, 7600, 30, 80),
            ChargeRecord("chg_3", "2024-07-15", "판교 슈퍼차저", 52.1f, 38, 16400, 15, 85),
            ChargeRecord("chg_4", "2024-07-10", "강남 슈퍼차저", 30.5f, 22, 9800, 40, 80)
        )
    }

    suspend fun fetchConsumables(): List<ConsumableItem> = withContext(Dispatchers.IO) {
        listOf(
            ConsumableItem(
                id = "c1",
                name = "캐빈 에어 필터",
                lastReplacedKm = 15000,
                replacementIntervalKm = 20000,
                currentMileageKm = 34500,
                lastReplacedOdoKm = 15000,
                lastReplacedDate = "2023-08-15"
            ),
            ConsumableItem(
                id = "c2",
                name = "와이퍼 블레이드",
                lastReplacedKm = 20000,
                replacementIntervalKm = 15000,
                currentMileageKm = 34500,
                lastReplacedOdoKm = 20000,
                lastReplacedDate = "2023-11-20"
            ),
            ConsumableItem(
                id = "c3",
                name = "브레이크 오일",
                lastReplacedKm = 0,
                replacementIntervalKm = 40000,
                currentMileageKm = 34500,
                lastReplacedOdoKm = 0,
                lastReplacedDate = "2022-01-10"
            ),
            ConsumableItem(
                id = "c4",
                name = "타이어 위치 교환",
                lastReplacedKm = 25000,
                replacementIntervalKm = 10000,
                currentMileageKm = 34500,
                lastReplacedOdoKm = 25000,
                lastReplacedDate = "2024-02-10"
            )
        )
    }

    suspend fun toggleDoorLock(currentState: VehicleState): VehicleState = withContext(Dispatchers.IO) {
        currentState.copy(isLocked = !currentState.isLocked)
    }

    suspend fun toggleClimate(currentState: VehicleState): VehicleState = withContext(Dispatchers.IO) {
        currentState.copy(climateOn = !currentState.climateOn)
    }

    suspend fun toggleSentry(currentState: VehicleState): VehicleState = withContext(Dispatchers.IO) {
        currentState.copy(sentryMode = !currentState.sentryMode)
    }

    suspend fun toggleTrunk(currentState: VehicleState): VehicleState = withContext(Dispatchers.IO) {
        currentState.copy(trunkOpen = !currentState.trunkOpen)
    }

    suspend fun toggleFrunk(currentState: VehicleState): VehicleState = withContext(Dispatchers.IO) {
        currentState.copy(frunkOpen = !currentState.frunkOpen)
    }

    suspend fun addChargeRecord(record: ChargeRecord): List<ChargeRecord> = withContext(Dispatchers.IO) {
        fetchChargingHistory() + record
    }

    suspend fun updateConsumable(item: ConsumableItem): List<ConsumableItem> = withContext(Dispatchers.IO) {
        fetchConsumables().map { if (it.id == item.id) item else it }
    }
}
