package com.example.tesladash.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// 주행 기록 데이터 클래스
@Serializable
data class Trip(
    @SerialName("timestamp") val timestamp: String,
    @SerialName("move_km") val moveKM: Double = 0.0,
    @SerialName("start_dong") val startDong: String? = null,
    @SerialName("end_dong") val endDong: String? = null,
    @SerialName("duration_min") val durationMin: Int = 0
)

// 차량 로그 데이터 클래스 (배터리 및 상태)
@Serializable
data class VehicleLog(
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("battery_level") val batteryLevel: Int = 100,
    @SerialName("est_battery_range") val estBatteryRange: Double = 0.0
)

// UI용 변환 데이터 클래스
data class BatteryMetric(
    val dateLabel: String,
    val fullRangeKm: Int,
    val degradation: Double
)
