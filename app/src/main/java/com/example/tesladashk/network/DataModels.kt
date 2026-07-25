package com.example.tesladashk.network

data class ConfigState(
    val kakaoKey: String = "",
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val ghToken: String = "",
    val vehicleId: String = "",
    val ntfyTopic: String = ""
)

data class VehicleState(
    val statusText: String = "상태 수신 대기 중",
    val batteryLevel: Int = 0,
    val odometer: Int = 0,
    val outsideTemp: Float = 0.0f,
    val parkDurationStr: String = "-"
)

data class TripItem(
    val id: String = "",
    val date: String = "",
    val startAddress: String = "출발지 정보 없음",
    val endAddress: String = "도착지 정보 없음",
    val distanceKm: Double = 0.0,
    val batteryUsed: Double = 0.0,
    val driveTimeMin: Int = 0,
    val locationListJson: String = "[]"
)

data class ApiResponse(
    val isSuccess: Boolean,
    val statusCode: Int,
    val body: String,
    val errorMessage: String? = null
)
