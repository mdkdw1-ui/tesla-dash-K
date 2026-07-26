package com.mdkdw1.ui.tesla

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VehicleState(
    @SerialName("status") val status: String = "주차 중",
    @SerialName("battery_level") val batteryLevel: Int = 0,
    @SerialName("odometer") val odometer: Double = 0.0,
    @SerialName("is_charging") val isCharging: Boolean = false,
    @SerialName("charge_power") val chargePower: Double = 0.0,
    @SerialName("is_sentry") val isSentry: Boolean = false,
    @SerialName("tpms_fl") val tpmsFl: Double = 0.0,
    @SerialName("tpms_fr") val tpmsFr: Double = 0.0,
    @SerialName("tpms_rl") val tpmsRl: Double = 0.0,
    @SerialName("tpms_rr") val tpmsRr: Double = 0.0,
    @SerialName("outside_temp") val outsideTemp: Double = 0.0,
    @SerialName("last_updated") val lastUpdated: String = "--:--"
)

@Serializable
data class DailyTripRecord(
    @SerialName("date") val date: String = "",
    @SerialName("total_distance") val totalDistance: Double = 0.0,
    @SerialName("used_battery") val usedBattery: Int = 0,
    @SerialName("drive_time_min") val driveTimeMin: Int = 0,
    @SerialName("used_kwh") val usedKwh: Double = 0.0,
    @SerialName("efficiency") val efficiency: Double = 0.0
)

@Serializable
data class BatteryHealthRecord(
    @SerialName("id") val id: String = "",
    @SerialName("timestamp") val timestamp: String = "",
    @SerialName("degradation") val degradation: Double = 100.0,
    @SerialName("est_range") val estRange: Double = 0.0
)

@Serializable
data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val githubToken: String = ""
)
