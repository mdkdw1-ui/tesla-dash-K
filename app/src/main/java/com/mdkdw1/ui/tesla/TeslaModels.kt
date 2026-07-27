package com.mdkdw1.ui.tesla

// 앱 암호화 설정 모델
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val teslaClientId: String = "",
    val teslaClientSecret: String = "",
    val githubKey: String = "",
    val githubToken: String = "",
    val isAutoSync: Boolean = true
)

// 차량 상태 정보 모델
data class VehicleState(
    val vehicleName: String = "Model Y",
    val batteryLevel: Int = 80,
    val estimatedRange: Double = 380.0,
    val maxRange: Double = 475.0,
    val isCharging: Boolean = false,
    val chargeLimit: Int = 80,
    val chargeCurrent: Int = 32,
    val isLocked: Boolean = true,
    val climateOn: Boolean = false,
    val insideTemp: Double = 21.5,
    val outsideTemp: Double = 18.0,
    val targetTemp: Double = 20.0,
    val sentryModeOn: Boolean = false,
    val trunkOpen: Boolean = false,
    val frunkOpen: Boolean = false,
    val flTire: Double = 42.0,
    val frTire: Double = 41.5,
    val rlTire: Double = 42.0,
    val rrTire: Double = 42.5,
    val statusText: String = "주차됨",
    val lastUpdatedTimestamp: String = "방금 전"
)

// 주행 기록 아이템
data class DriveLogItem(
    val id: String = "",
    val date: String = "",
    val distanceKm: Double = 0.0,
    val durationMinutes: Int = 0,
    val energyUsedKwh: Double = 0.0,
    val startLocation: String = "",
    val endLocation: String = "",
    val avgEfficiencyWhPerKm: Double = 0.0
)

// 월간 주행/전비 보고서
data class MonthlyReport(
    val month: String = "",
    val totalDistanceKm: Double = 0.0,
    val totalEnergyKwh: Double = 0.0,
    val totalCostKrw: Int = 0,
    val avgEfficiency: Double = 0.0
)

// 배터리 열화율 데이터 아이템
data class BatteryDegradationItem(
    val date: String = "",
    val odometerKm: Double = 0.0,
    val maxCapacityKwh: Double = 0.0,
    val degradationPercent: Double = 0.0,
    val estimated100PercentRange: Double = 0.0
)

// 감시 모드(Sentry) 이벤트 아이템
data class SentryEventItem(
    val id: String = "",
    val timestamp: String = "",
    val location: String = "",
    val cameraAngle: String = "",
    val videoUrl: String = ""
)

// 소모품 관리 아이템
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastReplacedDate: String = "",
    val lastReplacedKm: Double = 0.0,
    val replacementIntervalKm: Double = 0.0,
    val replacementIntervalMonths: Int = 0,
    val currentUsagePercent: Double = 0.0
)

// 일지 유형 및 아이템
enum class JournalType {
    DRIVE, CHARGE, MAINTENANCE, SENTRY
}

data class JournalLogItem(
    val id: String = "",
    val type: JournalType = JournalType.DRIVE,
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val cost: Int = 0
)

// 배터리 이력
data class BatteryRecord(
    val date: String = "",
    val degradationPercent: Double = 0.0,
    val range100PercentKm: Double = 0.0
)
