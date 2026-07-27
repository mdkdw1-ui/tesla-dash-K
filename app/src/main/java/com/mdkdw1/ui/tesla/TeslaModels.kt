package com.mdkdw1.ui.tesla

data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "",
    val teslaToken: String = "",
    val vehicleId: String = ""
)

data class VehicleState(
    val isOnline: Boolean = true,
    val batteryLevel: Int = 78,
    val estimatedRangeKm: Int = 385,
    val extrapolated100RangeKm: Int = 493,
    val chargeLimit: Int = 80,
    val isCharging: Boolean = false,
    val isChargePortOpen: Boolean = false,
    val insideTemp: Float = 21.5f,
    val outsideTemp: Float = 18.0f,
    val targetTemp: Float = 21.0f,
    val speedKmh: Int = 0,
    val isLocked: Boolean = true,
    val isClimateOn: Boolean = false,
    val isSentryOn: Boolean = true,
    val latitude: Double = 37.5665,
    val longitude: Double = 126.9780,
    val odometerKm: Double = 15420.0
)

data class BatteryDegradationData(
    val date: String = "",
    val mileage: Int = 0,
    val degradationRate: Float = 0f,
    val estimatedMaxRangeKm: Int = 0
)

data class ChargingSession(
    val id: String = "",
    val date: String = "",
    val location: String = "",
    val startPercent: Int = 0,
    val endPercent: Int = 0,
    val energyAddedKwh: Double = 0.0,
    val cost: Int = 0,
    val remainingPercent: Int = 0
)

data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastReplacedKm: Int = 0,
    val replacementIntervalKm: Int = 10000,
    val currentMileageKm: Int = 0
) {
    val remainingKm: Int
        get() = (lastReplacedKm + replacementIntervalKm) - currentMileageKm

    val remainingPercent: Float
        get() = if (replacementIntervalKm > 0) {
            ((remainingKm.toFloat() / replacementIntervalKm.toFloat()) * 100f).coerceIn(0f, 100f)
        } else 0f
}
