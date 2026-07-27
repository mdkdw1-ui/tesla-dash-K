package com.mdkdw1.ui.tesla

data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = "",
    val kakaoMapKey: String = "",
    val teslaRefreshToken: String = "",
    val aesPassword: String = "",
    val targetSoc: Int = 80,
    val chargeAmps: Int = 32,
    val updateIntervalSec: Int = 30,
    val autoRefresh: Boolean = true,
    val pushNotification: Boolean = true
)

data class VehicleState(
    val vehicleName: String = "Model Y Long Range",
    val batteryLevel: Int = 78,
    val batteryRangeKm: Double = 412.5,
    val isCharging: Boolean = false,
    val chargeState: String = "충전 대기",
    val isLocked: Boolean = true,
    val climateOn: Boolean = false,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 18.0,
    val sentryMode: Boolean = true,
    val trunkOpen: Boolean = false,
    val frunkOpen: Boolean = false,
    val speedKm: Double = 0.0,
    val odometerKm: Double = 34520.0,
    val tirePressureFl: Double = 2.9,
    val tirePressureFr: Double = 2.9,
    val tirePressureRl: Double = 2.9,
    val tirePressureRr: Double = 2.9,
    val lastUpdated: String = "방금 전"
)

data class DegradationRecord(
    val id: String = "",
    val date: String,
    val odometerKm: Double,
    val fullRangeKm: Double,
    val degradationPct: Double
)

data class ChargeRecord(
    val id: String = "",
    val date: String,
    val location: String,
    val addedKwh: Double,
    val costKrw: Int,
    val durationMinutes: Int,
    val chargeType: String
)

data class ConsumableItem(
    val id: String,
    val name: String,
    val iconName: String,
    val lastReplacedDate: String,
    val lastReplacedOdoKm: Double,
    val replacementIntervalKm: Double,
    val currentOdoKm: Double
) {
    val remainingPct: Int
        get() {
            val driven = currentOdoKm - lastReplacedOdoKm
            val remain = replacementIntervalKm - driven
            return ((remain / replacementIntervalKm) * 100).toInt().coerceIn(0, 100)
        }
}
