package com.example.tesladashk.network

data class ConfigState(
    val kakaoKey: String = "",
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val vercelUrl: String = "",
    val vehicleId: String = "",
    val ntfyTopic: String = "",
    val ghToken: String = ""
)

data class VehicleState(
    val statusText: String = "상태 미수신",
    val batteryLevel: Int = 0,
    val odometer: Int = 0,
    val outsideTemp: Float = 0f,
    val parkDurationStr: String = "",
    val tpmsFl: Float = 0f,
    val tpmsFr: Float = 0f,
    val tpmsRl: Float = 0f,
    val tpmsRr: Float = 0f
)

data class TripItem(
    val id: String = "",
    val timeStr: String = "",
    val startDong: String = "",
    val endDong: String = "",
    val moveKm: Double = 0.0,
    val durationMin: Int = 0,
    val useBattery: Double = 0.0,
    val date: String = "",
    val startAddress: String = "",
    val endAddress: String = "",
    val distanceKm: Double = 0.0,
    val batteryUsed: Double = 0.0,
    val driveTimeMin: Int = 0
)

data class ApiResponse(
    val isSuccess: Boolean = false,
    val code: Int = 200,
    val body: String = ""
) {
    val statusCode: Int get() = code
    val errorMessage: String get() = body
}
