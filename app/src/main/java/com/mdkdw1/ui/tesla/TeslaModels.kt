package com.mdkdw1.ui.tesla

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val vehicleId: String = ""
)

@Serializable
data class VehicleState(
    val batteryLevel: Int = 0,
    val batteryRangeKm: Double = 0.0,
    val isCharging: Boolean = false,
    val isLocked: Boolean = true,
    val vehicleName: String = "Model Y Long Range",
    val odometerKm: Double = 0.0,
    val insideTemp: Double = 20.0,
    val outsideTemp: Double = 20.0,
    val latitude: Double = 37.5665,
    val longitude: Double = 126.9780
)

@Serializable
data class DailyTrip(
    val date: String = "",
    val distanceKm: Double = 0.0,
    val efficiencyWhPerKm: Double = 0.0
)

@Serializable
data class BatteryDegradation(
    val date: String = "",
    val capacityKwh: Double = 0.0,
    val healthPercentage: Double = 0.0
)

@Serializable
data class ChargeRecord(
    val date: String = "",
    val addedKwh: Double = 0.0,
    val cost: Int = 0,
    val location: String = ""
)

@Serializable
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastReplacedKm: Double = 0.0,
    val cycleKm: Double = 0.0
)

// UI 클래스 간 호환성을 위한 타입 별칭
typealias VehicleStateData = VehicleState
typealias BatteryRecord = BatteryDegradation
