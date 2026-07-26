package com.mdkdw1.ui.tesla

import java.util.Date

// 메인 탭 정의
enum class MainTab(val title: String) {
    MONITOR("테슬라 모니터"),
    GUARDIAN("감시 가디언")
}

// 테슬라 모니터 하위 탭 정의
enum class MonitorSubTab(val title: String) {
    VEHICLE("차량정보"),
    DRIVE("주행정보"),
    MONTHLY("월간리포트"),
    BATTERY("배터리")
}

// 차량 현재 상태
data class VehicleState(
    val statusText: String = "주차 중", // 수면 중, 주차 중, 주행 중 등
    val batteryPercent: Int = 82,
    val totalOdometer: Double = 34520.0, // 총 주행거리 (km)
    val lastUpdated: Date = Date(),
    val frontLeftTire: Double = 2.9, // bar
    val frontRightTire: Double = 2.9,
    val rearLeftTire: Double = 2.8,
    val rearRightTire: Double = 2.8
)

// 일지 아이템 (주행 또는 충전)
enum class JournalType { DRIVE, CHARGE }

data class JournalLogItem(
    val id: String,
    val type: JournalType,
    val dateText: String,
    val timeText: String,
    val distanceKm: Double,       // 주행거리 (1km 미만 제외)
    val efficiencyWhKm: Int,      // 전비
    val batteryStart: Int,
    val batteryEnd: Int,
    val addedBatteryPercent: Int, // 충전 시 증가한 배터리 %
    val durationMinutes: Int,
    val location: String
)

// 최근 운행일 전체 기록 요약
data class DailyDriveSummary(
    val dateText: String = "최근 운행일",
    val totalDistanceKm: Double = 0.0,
    val totalDurationMinutes: Int = 0,
    val avgEfficiencyWhKm: Int = 0,
    val driveCount: Int = 0
)

// 월간 리포트
data class MonthlyReport(
    val monthYear: String, // 예: "2026년 7월"
    val totalDistanceKm: Double,
    val totalDriveMinutes: Int,
    val avgEfficiencyWhKm: Int,
    val totalChargePercent: Int,
    val topDriveTimeDays: List<Pair<String, Int>>,   // 일자, 운전시간(분) Top 5
    val topDriveDistanceDays: List<Pair<String, Double>> // 일자, 주행거리(km) Top 5
)

// 배터리 열화 데이터 (최근 50개 레코드)
data class BatteryRecord(
    val dateText: String,
    val batteryPercent: Int,
    val calculated100Km: Double, // 100% 환산 주행가능거리
    val degradationRate: Double  // 열화율 (%)
)

// 설정 정보
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val githubToken: String = ""
)
