package com.mdkdw1.ui.tesla

// 앱 설정 모델 (AES-256 암호화 저장)
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val githubKey: String = "",
    val isAutoSync: Boolean = true
)

// 차량 상태 데이터 모델
data class VehicleState(
    val statusText: String = "주차 중",
    val batteryLevel: Int = 78,
    val range: Int = 380,
    val odometer: Double = 45210.5,
    val cabinTemp: Double = 21.5,
    val isLocked: Boolean = true,
    val climateOn: Boolean = false,
    val isCharging: Boolean = false,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis() - (3 * 3600 * 1000 + 25 * 60 * 1000),
    val flTire: Double = 41.2,
    val frTire: Double = 41.5,
    val rlTire: Double = 40.8,
    val rrTire: Double = 41.0
)

// 주행 / 충전 기록 모델
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

// 월간 리포트 모델
data class MonthlyReport(
    val monthStr: String = "2026년 7월",
    val totalDistanceKm: Double = 1240.5,
    val avgEfficiency: Int = 148,
    val totalDriveTimeHours: Double = 32.5,
    val topDriveTimeDays: List<Pair<String, Int>> = emptyList(),
    val topDistanceDays: List<Pair<String, Double>> = emptyList()
)

// 배터리 열화 데이터 모델
data class BatteryDegradationItem(
    val date: String,
    val degradationPercent: Double,
    val maxEstimatedRangeKm: Double
)
