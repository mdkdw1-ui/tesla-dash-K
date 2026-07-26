package com.mdkdw1.ui.tesla

import kotlinx.serialization.Serializable

@Serializable
data class VehicleData(
    val id: String = "",
    val name: String = "Model Y Long Range",
    val batteryLevel: Int = 82,
    val rangeKm: Int = 412,
    val maxRangeKm: Int = 502,
    val state: String = "online", // "online", "asleep", "offline"
    val climateOn: Boolean = false,
    val insideTemp: Float = 21.5f,
    val outsideTemp: Float = 18.0f,
    val locked: Boolean = true,
    val sentryMode: Boolean = true,
    val speed: Int = 0,
    val odometer: Double = 24500.0,
    val latitude: Double = 37.5665,
    val longitude: Double = 126.9780,
    val lastUpdated: String = "방금 전"
)

@Serializable
data class BatteryDegradation(
    val date: String = "",
    val odometer: Int = 0,
    val maxRangeKm: Int = 502,
    val degradationPercent: Float = 2.4f
)

@Serializable
data class ChargeRecord(
    val id: String = "",
    val date: String = "",
    val location: String = "",
    val energyAddedKwh: Double = 0.0,
    val startPercent: Int = 0,
    val endPercent: Int = 0,
    val costKrw: Int = 0,
    val fastCharge: Boolean = false
)

@Serializable
data class ConsumableItem(
    val id: String = "",
    val name: String = "",
    val status: String = "양호", // "양호", "점검 필요", "교체 권장"
    val lastChangedKm: Int = 0,
    val intervalKm: Int = 20000,
    val currentKm: Int = 24500,
    val progressPercent: Float = 80.0f
)

@Serializable
data class DailyTrip(
    val date: String = "",
    val distanceKm: Double = 0.0,
    val efficiencyWhKm: Int = 0,
    val startLocation: String = "",
    val endLocation: String = ""
)

@Serializable
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseAnonKey: String = "",
    val kakaoMapApiKey: String = "",
    val teslaAccessToken: String = "",
    val isAutoRefreshEnabled: Boolean = true,
    val refreshIntervalSec: Int = 30
)
