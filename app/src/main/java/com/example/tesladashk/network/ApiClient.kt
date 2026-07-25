package com.example.tesladashk.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class ConfigState(
    val kakaoKey: String = "",
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val vercelUrl: String = "",
    val vehicleId: String = "",
    val ntfyTopic: String = ""
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

data class ApiResponse(val isSuccess: Boolean, val code: Int, val body: String)

object ApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // 💡 Vercel api/sync 자동 호출 API
    suspend fun triggerVercelSync(vercelUrl: String): ApiResponse {
        if (vercelUrl.isBlank()) return ApiResponse(false, 400, "Vercel URL 미설정")
        return withContext(Dispatchers.IO) {
            try {
                val formattedUrl = when {
                    !vercelUrl.startsWith("http://") && !vercelUrl.startsWith("https://") -> "https://$vercelUrl"
                    else -> vercelUrl
                }
                val targetUrl = if (!formattedUrl.contains("/api/sync")) {
                    "${formattedUrl.removeSuffix("/")}/api/sync"
                } else formattedUrl

                val request = Request.Builder()
                    .url(targetUrl)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    ApiResponse(response.isSuccessful, response.code, response.body?.string() ?: "")
                }
            } catch (e: Exception) {
                ApiResponse(false, 500, e.localizedMessage ?: "네트워크 오류")
            }
        }
    }

    suspend fun executeSupabaseGet(url: String, apiKey: String): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    ApiResponse(response.isSuccessful, response.code, response.body?.string() ?: "")
                }
            } catch (e: Exception) {
                ApiResponse(false, 500, e.localizedMessage ?: "네트워크 오류")
            }
        }
    }
}
