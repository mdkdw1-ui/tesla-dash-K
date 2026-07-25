package com.example.tesladashk.network

import com.google.gson.annotations.SerializedName

data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "159c5d7588efc5939d431f005912f9f3",
    val vehicleId: String = "3744141651867089",
    val ntfyTopic: String = "MJYAz6ZyjXiujaTDpJ",
    val accessToken: String = ""
)

data class VehicleRow(
    val id: String? = null,
    @SerializedName("vehicle_id") val vehicleId: String? = null,
    val state: String? = null,
    @SerializedName("battery_level") val batteryLevel: Int? = null,
    val odometer: Double? = null,
    @SerializedName("outside_temp") val outsideTemp: Double? = null,
    @SerializedName("sentry_mode") val sentryMode: Boolean? = null,
    @SerializedName("tpms_fl") val tpmsFl: Double? = null,
    @SerializedName("tpms_fr") val tpmsFr: Double? = null,
    @SerializedName("tpms_rl") val tpmsRl: Double? = null,
    @SerializedName("tpms_rr") val tpmsRr: Double? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class LatLngPoint(val lat: Double, val lng: Double)

data class DrivingTrip(
    val id: String = "",
    val timestamp: Long = 0L,
    val stateType: String = "주행",
    val startTime: String = "",
    val endTime: String = "",
    val durationText: String = "",
    val moveKM: Double = 0.0,
    val batteryUsedPercent: Double = 0.0,
    val startBattery: Int = 0,
    val endBattery: Int = 0,
    val endOdometer: Double = 0.0,
    val dateGroup: String = "",
    var startDong: String = "조회중",
    var endDong: String = "조회중",
    val path: List<LatLngPoint> = emptyList()
)

data class SentryStatusResponse(
    val success: Boolean = false,
    @SerializedName("sentry_mode") val sentryMode: Boolean? = null,
    val locked: Boolean? = null,
    @SerializedName("doors_open") val doorsOpen: DoorsOpen? = null,
    @SerializedName("trunks_open") val trunksOpen: TrunksOpen? = null,
    @SerializedName("sentry_mode_type") val sentryModeType: String? = null
)

data class DoorsOpen(
    val df: Boolean? = null,
    val dr: Boolean? = null,
    val pf: Boolean? = null,
    val pr: Boolean? = null
)

data class TrunksOpen(
    val ft: Boolean? = null,
    val rt: Boolean? = null
)
