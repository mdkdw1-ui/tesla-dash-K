package com.mdkdw1.ui.tesla

/**
 * Tesla Dash K - 데이터 모델 통합 정의
 */

// 1. 차량 상태 (Vehicle State) 데이터 클래스
data class VehicleState(
    val vehicleName: String = "Tesla Model Y",
    val batteryPercent: Int = 78,
    val range: Double = 345.0,
    val estimatedRangeKm: Double = 320.0,
    val totalOdometer: Double = 24500.0,
    val cabinTemp: Double = 21.5,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 18.0,
    val isClimateOn: Boolean = false,
    val chargeStatus: String = "Disconnected",
    val isTrunkOpen: Boolean = false,
    val isFrunkOpen: Boolean = false,
    val isSentryModeOn: Boolean = true,
    val speedKmh: Double = 0.0,
    val tpmsFrontLeft: Double = 2.9,
    val tpmsFrontRight: Double = 2.9,
    val tpmsRearLeft: Double = 2.8,
    val tpmsRearRight: Double = 2.8,
    val isLocked: Boolean = true,
    val isSteeringHeaterOn: Boolean = false,
    val seatHeaterDriver: Int = 0,
    val seatHeaterPassenger: Int = 0,
    val parkedTimeMinutes: Long = 120,
    val guardianAlertsCount: Int = 3,
    val latitude: Double = 37.5665,
    val longitude: Double = 126.9780,
    val locationName: String = "서울특별시 중구 세종대로 110"
) {
    // 기존 UI 및 View Model과의 하위 호환성을 위한 프로퍼티 별칭 (Getter)
    val carName: String get() = vehicleName
    val batteryLevel: Int get() = batteryPercent
    val ratedRange: Double get() = range
    val estRange: Double get() = estimatedRangeKm
    val odometer: Double get() = totalOdometer
    val climateOn: Boolean get() = isClimateOn
    val sentryMode: Boolean get() = isSentryModeOn
    val chargeState: String get() = chargeStatus
    val frontLeftPsi: Double get() = tpmsFrontLeft
    val frontRightPsi: Double get() = tpmsFrontRight
    val rearLeftPsi: Double get() = tpmsRearLeft
    val rearRightPsi: Double get() = tpmsRearRight
}

// 2. 주행 / 충전 / 주차 일지 Enum 및 데이터 클래스
enum class JournalType {
    DRIVE,
    CHARGE,
    PARK
}

data class JournalLogItem(
    val id: String = "",
    val timestamp: String = "",
    val type: JournalType = JournalType.DRIVE,
    val title: String = "",
    val startSoc: Int = 0,
    val endSoc: Int = 0,
    val distanceKm: Double = 0.0,
    val energyUsedKwh: Double = 0.0,
    val efficiencyWhKm: Double = 0.0,
    val costWon: Int = 0,
    val location: String = ""
) {
    val date: String get() = timestamp
    val typeName: String
        get() = when (type) {
            JournalType.DRIVE -> "주행"
            JournalType.CHARGE -> "충전"
            JournalType.PARK -> "주차"
        }
}

// 3. 배터리 열화 및 100% 환산 주행거리 기록 데이터 클래스
data class BatteryRecord(
    val id: String = "",
    val date: String = "",
    val degradationPercent: Double = 0.0,
    val maxRange100PercentKm: Double = 0.0,
    val fullCapacityKwh: Double = 0.0
) {
    val degradation: Double get() = degradationPercent
    val range100: Double get() = maxRange100PercentKm
    val capacity: Double get() = fullCapacityKwh
}

// 4. 소모품 관리 데이터 클래스
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastReplacedKm: Int = 0,
    val replacementIntervalKm: Int = 10000,
    val currentOdometer: Int = 24500,
    val lastReplacedDate: String = ""
) {
    val progressPercent: Float
        get() {
            val driven = currentOdometer - lastReplacedKm
            if (replacementIntervalKm <= 0) return 0f
            return (driven.toFloat() / replacementIntervalKm.toFloat()).coerceIn(0f, 1f)
        }

    val remainingKm: Int
        get() = (replacementIntervalKm - (currentOdometer - lastReplacedKm)).coerceAtLeast(0)
}

// 5. 보안 저장소 및 앱 설정 데이터 클래스
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val teslaAccessToken: String = "",
    val teslaRefreshToken: String = "",
    val autoRefreshIntervalSec: Int = 30
)

// 프로젝트 내 다양한 모델 명칭 대응을 위한 TypeAlias
typealias DriveLog = JournalLogItem
typealias BatteryHealth = BatteryRecord
typealias Consumable = ConsumableItem
