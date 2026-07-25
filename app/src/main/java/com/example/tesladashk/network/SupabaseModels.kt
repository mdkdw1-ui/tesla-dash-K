package com.example.tesladashk.network

import com.google.gson.annotations.SerializedName

data class VehicleDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("user_uid") val userUid: String? = null,
    @SerializedName("vehicle_id") val vehicleId: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("battery_level") val batteryLevel: Int? = null,
    @SerializedName("odometer") val odometer: Double? = null,
    @SerializedName("tpms_fl") val tpmsFl: Double? = null,
    @SerializedName("tpms_fr") val tpmsFr: Double? = null,
    @SerializedName("tpms_rl") val tpmsRl: Double? = null,
    @SerializedName("tpms_rr") val tpmsRr: Double? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class DrivingDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("user_uid") val userUid: String? = null,
    @SerializedName("vehicle_id") val vehicleId: String? = null,
    @SerializedName("move_km") val moveKm: Double? = null,
    @SerializedName("use_battery") val useBattery: Double? = null,
    @SerializedName("driving_time") val drivingTime: Int? = null,
    @SerializedName("start_address") val startAddress: String? = null,
    @SerializedName("end_address") val endAddress: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class KakaoRegionResponse(
    @SerializedName("documents") val documents: List<KakaoDocument>? = null
)

data class KakaoDocument(
    @SerializedName("address_name") val addressName: String? = null,
    @SerializedName("region_1depth_name") val region1Depth: String? = null,
    @SerializedName("region_2depth_name") val region2Depth: String? = null,
    @SerializedName("region_3depth_name") val region3Depth: String? = null
)
