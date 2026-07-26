package com.mdkdw1.ui.tesla

import java.util.Date

// ==========================================
// 1. 앱 설정 모델 (Encrypted Settings)
// ==========================================
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val teslaAccessToken: String = "",
    val githubToken: String = ""
)

// ==========================================
// 2. Navigation & Tab Enums
// ==========================================
enum class MainTab(val title: String) {
    MONITOR("테슬라 모니터"),
    GUARDIAN("감시 가디언")
}

enum class MonitorSubTab(val title: String) {
    VEHICLE("차량정보"),
    DRIVE("주행정보"),
    MONTHLY("월간리포트"),
    BATTERY("배터리")
}

// ==========================================
// 3. 차량 상태 데이터 모델
// ==========================================
data class VehicleState(
    val vehicleName: String = "Model Y Long Range",
    val statusText: String = "주차 중",
    val batteryPercent: Int = 82,
    val estimatedRangeKm: Int = 412,
    val totalOdometer: Double = 35240.0,
    val isLocked: Boolean = true,
    val isClimateOn: Boolean = false,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 24.0,
    val isTrunkOpen: Boolean = false,
    val isFrunkOpen: Boolean = false,
    val isSentryModeOn: Boolean = true,
    val speedKmh: Int = 0,
    val chargeStatus: String = "Discharging",
    val lastUpdated: Date = Date(),
    val frontLeftTire: Double = 2.9,
    val frontRightTire: Double = 2.9,
    val rearLeftTire: Double = 2.8,
    val rearRightTire: Double = 2.8
)

// ==========================================
// 4. 소모품 관리 모델
// ==========================================
data class ConsumableItem(
    val name: String,
    val currentKm: Int,
    val maxKm: Int,
    val lastReplacedDate: String
) {
    val progressRatio: Float
        get() = (currentKm.toFloat() / maxKm.toFloat()).coerceIn(0f, 1f)
}

// ==========================================
// 5. 주행 & 충전 일지 모델
// ==========================================
enum class JournalType { DRIVE, CHARGE }

data class JournalLogItem(
    val id: String = "",
    val type: JournalType = JournalType.DRIVE,
    val dateText: String = "",
    val timeText: String = "",
    val distanceKm: Double = 0.0,
    val efficiencyWhKm: Int = 0,
    val batteryStart: Int = 0,
    val batteryEnd: Int = 0,
    val addedBatteryPercent: Int = 0,
    val durationMinutes: Int = 0,
    val location: String = ""
)

data class DailyDriveSummary(
    val dateText: String = "최근 운행일",
    val totalDistanceKm: Double = 0.0,
    val totalDurationMinutes: Int = 0,
    val avgEfficiencyWhKm: Int = 0,
    val driveCount: Int = 0
)

data class MonthlyReport(
    val monthYear: String = "",
    val totalDistanceKm: Double = 0.0,
    val totalDriveMinutes: Int = 0,
    val avgEfficiencyWhKm: Int = 0,
    val totalChargePercent: Int = 0,
    val topDriveTimeDays: List<Pair<String, Int>> = emptyList(),
    val topDriveDistanceDays: List<Pair<String, Double>> = emptyList()
)

// ==========================================
// 6. 배터리 열화 데이터 모델
// ==========================================
data class BatteryRecord(
    val dateText: String,
    val batteryPercent: Int,
    val calculated100Km: Double,
    val degradationRate: Double
)
