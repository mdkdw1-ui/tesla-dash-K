package com.example.tesladashk.network

import com.google.gson.annotations.SerializedName

data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "159c5d7588efc5939d431f005912f9f3",
    val vehicleId: String = "3744141651867089",
    val ntfyTopic: String = "MJYAz6ZyjXiujaTDpJ"
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
    val stateType: String = "주행", // 주행, 온라인, 감시
    val startTime: String = "",
    val endTime: String = "",
    val durationText: String = "",
    val moveKM: Double = 0.0,
    val batteryUsedPercent: Double = 0.0,
    val startBattery: Int = 0,
    val endBattery: Int = 0,
    val endOdometer: Double = 0.0,
    val dateGroup: String = ""
)
