package com.example.tesladashk.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class VehicleState(
    val statusText: String = "현재: 주차 중",
    val batteryLevel: Int = 85,
    val odometer: Int = 45210,
    val outsideTemp: Float = 24.0f,
    val parkDurationStr: String = "주차 중 2시간 15분",
    val tpmsFl: Int = 42,
    val tpmsFr: Int = 42,
    val tpmsRl: Int = 41,
    val tpmsRr: Int = 41
)

data class TripItem(
    val id: String,
    val timeStr: String,
    val startDong: String,
    val endDong: String,
    val moveKm: Double,
    val durationMin: Int,
    val useBattery: Double,
    val startBat: Int,
    val endBat: Int,
    val odometer: Int
)

data class ConfigState(
    val kakaoKey: String = "",
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val ghToken: String = "",
    val vehicleId: String = "",
    val ntfyTopic: String = ""
)

data class ApiResponse(
    val isSuccess: Boolean,
    val statusCode: Int,
    val body: String,
    val errorMessage: String = ""
)

object ApiClient {
    suspend fun executeSupabaseGet(urlString: String, apiKey: String): ApiResponse = withContext(Dispatchers.IO) {
        if (urlString.isBlank() || apiKey.isBlank()) {
            return@withContext ApiResponse(false, 400, "", "Supabase URL 또는 Key가 설정되지 않았습니다.")
        }
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", apiKey)
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("Accept", "application/json")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val code = conn.responseCode
            val isSuccess = code in 200..299
            val stream = if (isSuccess) conn.inputStream else conn.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: ""

            if (isSuccess) {
                ApiResponse(true, code, body)
            } else {
                ApiResponse(false, code, body, "HTTP 오류 코드: $code")
            }
        } catch (e: Exception) {
            ApiResponse(false, 500, "", e.localizedMessage ?: "네트워크 연결 예외 발생")
        }
    }

    suspend fun executeSupabasePost(urlString: String, apiKey: String, jsonBody: String): Boolean = withContext(Dispatchers.IO) {
        if (urlString.isBlank() || apiKey.isBlank()) return@withContext false
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("apikey", apiKey)
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Prefer", "return=minimal")
            conn.doOutput = true
            conn.connectTimeout = 5000

            OutputStreamWriter(conn.outputStream).use { it.write(jsonBody) }
            conn.responseCode in 200..299
        } catch (_: Exception) {
            false
        }
    }
}
