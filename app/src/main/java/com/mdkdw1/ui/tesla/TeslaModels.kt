package com.mdkdw1/ui/tesla

/**
 * 앱 보안 설정 모델
 */
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "",
    val teslaToken: String = "",
    val vehicleId: String = "",
    val githubKey: String = ""
) {
    val kakaoMapKey: String get() = kakaoKey
}

/**
 * 테슬라 차량 제어 및 상태 모델
 */
data class VehicleState(
    val vehicleName: String = "Model Y Long Range",
    val batteryLevel: Int = 82,
    val estimatedRangeKm: Int = 412,
    val isCharging: Boolean = false,
    val chargingPowerKw: Double = 0.0,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 18.0,
    val isLocked: Boolean = true,
    val climateOn: Boolean = false,
    val sentryMode: Boolean = true,
    val trunkOpen: Boolean = false,
    val frunkOpen: Boolean = false,
    val totalMileageKm: Int = 34500,
    val lastUpdated: String = "방금 전"
)

/**
 * 배터리 열화율 데이터 모델
 */
data class DegradationRecord(
    val date: String = "",
    val mileageKm: Int = 0,
    val healthPercent: Float = 100f,
    val capacityAh: Float = 0f
) {
    val soh: Float get() = healthPercent
    val odo: Int get() = mileageKm
}
typealias BatteryDegradation = DegradationRecord

/**
 * 충전 히스토리 기록 모델
 */
data class ChargeRecord(
    val id: String = "",
    val date: String = "",
    val location: String = "",
    val addedKwh: Float = 0f,
    val durationMinutes: Int = 0,
    val costKrw: Int = 0,
    val startSoc: Int = 0,
    val endSoc: Int = 0
)
typealias ChargingRecord = ChargeRecord

/**
 * 소모품 관리 데이터 모델
 */
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastReplacedKm: Int = 0,
    val replacementIntervalKm: Int = 20000,
    val currentMileageKm: Int = 0,
    val lastReplacedOdoKm: Int = 0,
    val lastReplacedDate: String = ""
)
