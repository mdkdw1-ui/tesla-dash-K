package com.mdkdw1.ui.tesla

// 앱 암호화 저장 설정 모델
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val githubKey: String = "",
    val teslaAccessToken: String = "",
    val githubToken: String = "",
    val isAutoSync: Boolean = true
)

// 운행/충전 기록 일지 관련 Enum 및 모델
enum class JournalType {
    DRIVE, CHARGE
}

data class JournalLogItem(
    val id: String,
    val type: JournalType,
    val date: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val efficiencyWhKm: Int,
    val batteryStart: Int,
    val batteryEnd: Int
)

// 기존 호환용 주행 기록 모델 (필요 시 JournalLogItem 변환 사용)
data class DriveLogItem(
    val id: String,
    val date: String,
    val isCharging: Boolean,
    val distanceKm: Double,
    val durationMinutes: Int,
    val efficiencyWhKm: Int,
    val batteryStart: Int,
    val batteryEnd: Int
)

// 배터리 열화 및 환산 주행거리 기록 모델
data class BatteryRecord(
    val id: String = "",
    val date: String,
    val degradationPercent: Double,
    val maxEstimatedRangeKm: Double
)

// 기존 호환용 배터리 모델
data class BatteryDegradationItem(
    val date: String,
    val degradationPercent: Double,
    val maxEstimatedRangeKm: Double
)

// 소모품 관리 데이터 모델
data class ConsumableItem(
    val id: String,
    val name: String,
    val replaceIntervalKm: Int,
    val lastReplacedKm: Double,
    val currentOdometerKm: Double
)

// 차량 종합 상태 모델 (TeslaRepository 매개변수 전체 호환)
data class VehicleState(
    val vehicleName: String = "My Model Y",
    val statusText: String = "주차 중",
    val batteryLevel: Int = 78,
    val batteryPercent: Int = 78,
    val range: Int = 380,
    val estimatedRangeKm: Double = 380.0,
    val odometer: Double = 45210.5,
    val totalOdometer: Double = 45210.5,
    val cabinTemp: Double = 21.5,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 18.0,
    val isLocked: Boolean = true,
    val isClimateOn: Boolean = false,
    val climateOn: Boolean = false,
    val isCharging: Boolean = false,
    val chargeStatus: String = "Disconnected",
    val isTrunkOpen: Boolean = false,
    val isFrunkOpen: Boolean = false,
    val isSentryModeOn: Boolean = false,
    val speedKmh: Double = 0.0,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis() - (3 * 3600 * 1000 + 25 * 60 * 1000),
    val flTire: Double = 41.2,
    val frTire: Double = 41.5,
    val rlTire: Double = 40.8,
    val rrTire: Double = 41.0
)

// 월간 리포트 데이터 모델
data class MonthlyReport(
    val monthStr: String = "2026년 7월",
    val totalDistanceKm: Double = 1240.5,
    val avgEfficiency: Int = 148,
    val totalDriveTimeHours: Double = 32.5,
    val topDriveTimeDays: List<Pair<String, Int>> = emptyList(),
    val topDistanceDays: List<Pair<String, Double>> = emptyList()
)
