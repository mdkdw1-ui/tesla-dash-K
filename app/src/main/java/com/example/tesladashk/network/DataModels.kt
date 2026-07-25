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
    val parkDurationStr: String = "-",
    val tpmsFl: Float = 0.0f,
    val tpmsFr: Float = 0.0f,
    val tpmsRl: Float = 0.0f,
    val tpmsRr: Float = 0.0f
)

data class TripItem(
    val id: String = "",
    val date: String = "",
    val startAddress: String = "출발지 미기재",
    val endAddress: String = "도착지 미기재",
    val distanceKm: Double = 0.0,
    val batteryUsed: Double = 0.0,
    val driveTimeMin: Int = 0,
    val locationListJson: String = "[]",
    val startDong: String = startAddress,
    val endDong: String = endAddress,
    val moveKm: Double = distanceKm,
    val durationMin: Int = driveTimeMin,
    val useBattery: Double = batteryUsed,
    val timeStr: String = date,
    val startBat: Int = 0,
    val endBat: Int = 0,
    val odometer: Int = 0
)

data class ApiResponse(
    val isSuccess: Boolean,
    val statusCode: Int,
    val body: String,
    val errorMessage: String? = null
)
