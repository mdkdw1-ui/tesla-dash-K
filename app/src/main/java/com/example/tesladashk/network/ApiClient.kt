package com.example.tesladashk.network

import com.google.gson.annotations.SerializedName

data class AppConfig(
    val kakaoKey: String = "159c5d7588efc5939d431f005912f9f3",
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val ghToken: String = "",
    val vehicleId: String = "3744141651867089",
    val ntfyTopic: String = "MJYAz6ZyjXiujaTDpJ",
    val accessToken: String = ""
)

data class VehicleRow(
    val id: String?,
    @SerializedName("vehicle_id") val vehicleId: String?,
    val vin: String?,
    @SerializedName("battery_level") val batteryLevel: Int?,
    val odometer: Double?,
    @SerializedName("outside_temp") val outsideTemp: Double?,
    @SerializedName("sentry_mode") val sentryMode: Boolean?,
    @SerializedName("is_sentry") val isSentry: Boolean?,
    @SerializedName("tpms_fl") val tpmsFl: Double?,
    @SerializedName("tpms_fr") val tpmsFr: Double?,
    @SerializedName("tpms_rl") val tpmsRl: Double?,
    @SerializedName("tpms_rr") val tpmsRr: Double?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("raw_data") val rawData: Any?
)

data class LatLngPoint(val lat: Double, val lng: Double)

data class DrivingTrip(
    val id: String,
    val timestamp: Long,
    val moveKM: Double,
    val useBattery: Double,
    val startBat: Int?,
    val endBat: Int?,
    val odometer: Double?,
    val durationMin: Int,
    val path: List<LatLngPoint>,
    var startDong: String = "조회중",
    var endDong: String = "조회중"
)

data class SentryStatusResponse(
    val success: Boolean,
    @SerializedName("sentry_mode") val sentryMode: Boolean?,
    val locked: Boolean?,
    @SerializedName("doors_open") val doorsOpen: DoorsOpen?,
    @SerializedName("trunks_open") val trunksOpen: TrunksOpen?,
    @SerializedName("sentry_mode_type") val sentryModeType: String?
)

data class DoorsOpen(val df: Boolean?, val dr: Boolean?, val pf: Boolean?, val pr: Boolean?)
data class TrunksOpen(val ft: Boolean?, val rt: Boolean?)
