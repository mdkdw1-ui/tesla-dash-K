package com.mdkdw1.ui.tesla

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeslaRepository(private val context: Context) {

    private val settingsManager = EncryptedSettingsManager(context)

    fun getConfig(): AppConfig {
        return settingsManager.getConfig()
    }

    fun saveConfig(config: AppConfig) {
        settingsManager.saveConfig(config)
    }

    suspend fun fetchVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        VehicleState(
            batteryLevel = 78,
            batteryRangeKm = 412.5,
            isCharging = false,
            isLocked = true,
            vehicleName = "Tesla Model Y",
            odometerKm = 24520.0,
            insideTemp = 21.5,
            outsideTemp = 18.0,
            latitude = 37.5665,
            longitude = 126.9780
        )
    }

    suspend fun fetchLatestDailyTrip(): DailyTrip? = withContext(Dispatchers.IO) {
        DailyTrip(
            date = "2026-07-26",
            distanceKm = 45.2,
            efficiencyWhPerKm = 142.0
        )
    }

    suspend fun fetchBatteryDegradation(): List<BatteryDegradation> = withContext(Dispatchers.IO) {
        listOf(
            BatteryDegradation("2024-01-01", 75.0, 100.0),
            BatteryDegradation("2024-06-01", 74.2, 98.9),
            BatteryDegradation("2025-01-01", 73.5, 98.0),
            BatteryDegradation("2025-06-01", 72.8, 97.1),
            BatteryDegradation("2026-01-01", 72.0, 96.0),
            BatteryDegradation("2026-07-01", 71.5, 95.3)
        )
    }

    suspend fun fetchChargeRecords(): List<ChargeRecord> = withContext(Dispatchers.IO) {
        listOf(
            ChargeRecord("2026-07-25", 42.5, 12500, "Gangnam Supercharger"),
            ChargeRecord("2026-07-20", 35.0, 9800, "Home Charger"),
            ChargeRecord("2026-07-15", 50.0, 15000, "Pangyo Supercharger")
        )
    }

    suspend fun fetchConsumables(): List<ConsumableItem> = withContext(Dispatchers.IO) {
        listOf(
            ConsumableItem("1", "Air Filter", 15000.0, 20000.0),
            ConsumableItem("2", "Wiper Blades", 10000.0, 15000.0),
            ConsumableItem("3", "Brake Fluid", 20000.0, 40000.0),
            ConsumableItem("4", "Tire Rotation", 20000.0, 10000.0)
        )
    }
}

// TeslaHubUI 호환용 타입 별칭
typealias TeslaHubRepository = TeslaRepository
