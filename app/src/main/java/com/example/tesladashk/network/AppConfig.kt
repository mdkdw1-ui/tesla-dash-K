package com.example.tesladashk.network

data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "",          // REST API Key (주소 변환용)
    val nativeKakaoKey: String = "",    // Native App Key (지도 SDK용)
    val userUid: String = "",
    val vehicleId: String = "",
    val ntfyTopic: String = "",
    val accessToken: String = ""
)

data class VehicleRow(
    val state: String = "",
    val batteryLevel: Int = 0,
    val odometer: Double = 0.0,
    val tpmsFl: Double? = null,
    val tpmsFr: Double? = null,
    val tpmsRl: Double? = null,
    val tpmsRr: Double? = null
)

data class DrivingTrip(
    val startTime: String = "",
    val moveKM: Double = 0.0,
    val startDong: String = "",
    val endDong: String = ""
)
