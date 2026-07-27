package com.mdkdw1.ui.tesla

// 앱 설정 정보 (GitHub 키/토큰 중복 제거, 테슬라 ID/PW 제거)
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val githubToken: String = "",
    val isAutoSync: Boolean = true,
    val syncIntervalMinutes: Int = 15
)

// 내 차량 정보 및 통합 상태
data class VehicleState(
    val vehicleName: String = "Model Y Long Range",
    val model: String = "Model Y",
    val vin: String = "5YJSA1E28MF******",
    val odometerKm: Double = 34520.0,
    val batteryPercent: Int = 78,
    val estimatedRangeKm: Int = 385,
    val insideTemp: Double = 21.5,
    val outsideTemp: Double = 18.0,
    val sentryModeOn: Boolean = true,
    val isCharging: Boolean = false,
    val chargePowerKw: Double = 0.0,
    val flTire: Double = 42.0, // 전좌 (PSI)
    val frTire: Double = 42.0, // 전우 (PSI)
    val rlTire: Double = 41.5, // 후좌 (PSI)
    val rrTire: Double = 41.5, // 후우 (PSI)
    val statusText: String = "Online",
    val carSoftwareVersion: String = "v12 (2024.14.9)",
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

// 주행 기록 항목
data class DriveLogItem(
    val id: String = "",
    val date: String = "",
    val startLocation: String = "",
    val endLocation: String = "",
    val distanceKm: Double = 0.0,
    val energyUsedKwh: Double = 0.0,
    val efficiencyWhKm: Double = 0.0
)

// 월간 리포트
data class MonthlyReport(
    val month: String = "",
    val totalDistanceKm: Double = 0.0,
    val totalChargeCostWon: Int = 0,
    val totalEnergyKwh: Double = 0.0
)

// 배터리 열화 데이터
data class BatteryDegradationItem(
    val date: String = "",
    val degradationPercent: Double = 0.0,
    val fullRangeKm: Double = 0.0
)

// 감시 가디언 이벤트 데이터
data class SentryEventItem(
    val id: String = "",
    val timestamp: String = "",
    val location: String = "",
    val eventType: String = "Motion Detected",
    val videoUrl: String? = null
)

// 소모품 항목 데이터
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val lastChangedKm: Double = 0.0,
    val replacementIntervalKm: Double = 20000.0,
    val statusPercent: Int = 100
)
