package com.example.tesladashk.network

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

object ApiClient {
    fun executeSupabaseGet(urlString: String, apiKey: String): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("apikey", apiKey)
        conn.setRequestProperty("Authorization", "Bearer $apiKey")
        conn.setRequestProperty("Accept", "application/json")
        conn.connectTimeout = 5000
        conn.readTimeout = 5000

        return if (conn.responseCode in 200..299) {
            BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
        } else {
            ""
        }
    }

    fun executeSupabasePost(urlString: String, apiKey: String, jsonBody: String) {
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
        conn.responseCode
    }
}
