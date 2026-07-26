package com.mdkdw1.ui.tesla

// 앱 암호화 설정 모델
data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val refreshIntervalSec: Int = 30
)

// 차량 상태 모델
data class VehicleState(
    val vehicleName: String = "Tesla Model Y",
    val batteryLevel: Int = 82,
    val usableBatteryLevel: Int = 80,
    val isCharging: Boolean = false,
    val chargeState: String = "Disconnected",
    val estimatedRangeKm: Double = 385.0,
    val odometerKm: Double = 24500.0,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 18.0,
    val isLocked: Boolean = true,
    val isSentryMode: Boolean = true,
    val isClimateOn: Boolean = false,
    val speedKmh: Double = 0.0,
    val gear: String = "P",
    val latitude: Double = 37.5665,
    val longitude: Double = 126.9780
)

// 일일 주행 데이터
data class DailyTrip(
    val date: String = "",
    val distanceKm: Double = 0.0,
    val energyKwh: Double = 0.0,
    val efficiencyWhKm: Double = 0.0,
    val batteryUsedPercent: Double = 0.0
)

// 배터리 열화 데이터
data class BatteryDegradation(
    val odometerKm: Double = 0.0,
    val maxCapacityKwh: Double = 0.0,
    val degradationPercent: Double = 0.0,
    val recordDate: String = ""
)

// 충전 기록
data class ChargeRecord(
    val id: String = "",
    val date: String = "",
    val startPercent: Int = 0,
    val endPercent: Int = 0,
    val energyAddedKwh: Double = 0.0,
    val costKrw: Int = 0,
    val location: String = ""
)

// 소모품 항목
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastReplacedKm: Double = 0.0,
    val intervalKm: Double = 20000.0,
    val currentOdometerKm: Double = 24500.0
) {
    val remainingKm: Double
        get() = (lastReplacedKm + intervalKm) - currentOdometerKm
        
    val healthPercent: Int
        get() = ((remainingKm / intervalKm) * 100).coerceIn(0.0, 100.0).toInt()
}
