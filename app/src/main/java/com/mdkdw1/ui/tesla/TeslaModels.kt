package com.mdkdw1.ui.tesla

data class VehicleState(
    val statusText: String = "온라인",
    val batteryLevel: Int = 82,
    val odometer: Double = 34521.4,
    val insideTemp: Double = 21.5,
    val outsideTemp: Double = 18.0,
    val isLocked: Boolean = true,
    val climateOn: Boolean = false,
    val isCharging: Boolean = false,
    val sentryModeOn: Boolean = true,
    val flTire: Double = 41.2,
    val frTire: Double = 41.0,
    val rlTire: Double = 40.8,
    val rrTire: Double = 40.9,
    val lastUpdatedTimestamp: Long = System.currentTimeMillis() - (1000 * 60 * 135)
)

data class DriveLogItem(
    val id: String,
    val date: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val efficiencyWhKm: Int,
    val batteryStart: Int,
    val batteryEnd: Int,
    val isCharging: Boolean = false,
    val startLocation: String = "서울시 강남구",
    val endLocation: String = "경기도 성남시"
)

data class MonthlyReport(
    val monthStr: String = "2026년 07월",
    val totalDistanceKm: Double = 1248.5,
    val avgEfficiency: Int = 145,
    val totalDriveTimeHours: Double = 32.4,
    val topDriveTimeDays: List<Pair<String, Int>> = listOf(
        "07월 15일" to 140,
        "07월 20일" to 115,
        "07월 10일" to 95,
        "07월 05일" to 80,
        "07월 22일" to 75
    ),
    val topDistanceDays: List<Pair<String, Double>> = listOf(
        "07월 15일" to 185.2,
        "07월 20일" to 142.0,
        "07월 10일" to 110.5,
        "07월 05일" to 98.4,
        "07월 22일" to 85.0
    )
)

data class BatteryDegradationItem(
    val id: String,
    val date: String,
    val degradationPercent: Double,
    val maxEstimatedRangeKm: Double,
    val sohPercent: Double
)

data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val githubKey: String = "",
    val teslaAccessToken: String = "",
    val githubToken: String = "",
    val isAutoSync: Boolean = true
)

data class SentryEventItem(
    val id: String,
    val timestamp: String,
    val eventType: String,
    val location: String,
    val videoUrl: String = ""
)
