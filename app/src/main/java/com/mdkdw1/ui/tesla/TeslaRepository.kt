package com.mdkdw1.ui.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class TeslaRepository(
    private val encryptedSettingsManager: EncryptedSettingsManager
) {

    fun getSettings(): AppSettings = encryptedSettingsManager.getSettings()

    fun saveSettings(settings: AppSettings) {
        encryptedSettingsManager.saveSettings(settings)
    }

    // Supabase REST API를 호출하여 내 실제 차량 정보 동기화
    suspend fun fetchVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        val settings = getSettings()
        if (settings.supabaseUrl.isBlank() || settings.supabaseKey.isBlank()) {
            return@withContext VehicleState() // 미설정 시 기본 상태
        }

        try {
            val responseJson = executeSupabaseGet("${settings.supabaseUrl}/rest/v1/vehicle_state?select=*&limit=1", settings.supabaseKey)
            if (responseJson != null && responseJson.length() > 0) {
                val obj = responseJson.getJSONObject(0)
                return@withContext VehicleState(
                    vehicleName = obj.optString("vehicle_name", "Model Y Long Range"),
                    model = obj.optString("model", "Model Y"),
                    vin = obj.optString("vin", "5YJSA1E28MF******"),
                    odometerKm = obj.optDouble("odometer_km", 34520.0),
                    batteryPercent = obj.optInt("battery_percent", 78),
                    estimatedRangeKm = obj.optInt("estimated_range_km", 385),
                    insideTemp = obj.optDouble("inside_temp", 21.5),
                    outsideTemp = obj.optDouble("outside_temp", 18.0),
                    sentryModeOn = obj.optBoolean("sentry_mode_on", true),
                    isCharging = obj.optBoolean("is_charging", false),
                    chargePowerKw = obj.optDouble("charge_power_kw", 0.0),
                    flTire = obj.optDouble("fl_tire", 42.0),
                    frTire = obj.optDouble("fr_tire", 42.0),
                    rlTire = obj.optDouble("rl_tire", 41.5),
                    rrTire = obj.optDouble("rr_tire", 41.5),
                    statusText = obj.optString("status_text", "Online"),
                    carSoftwareVersion = obj.optString("software_version", "v12 (2024.14.9)"),
                    lastUpdatedTimestamp = System.currentTimeMillis()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext VehicleState()
    }

    // 주행 기록 동기화
    suspend fun fetchDriveLogs(): List<DriveLogItem> = withContext(Dispatchers.IO) {
        val settings = getSettings()
        if (settings.supabaseUrl.isNotBlank() && settings.supabaseKey.isNotBlank()) {
            try {
                val jsonArray = executeSupabaseGet("${settings.supabaseUrl}/rest/v1/drive_logs?select=*&order=date.desc", settings.supabaseKey)
                if (jsonArray != null && jsonArray.length() > 0) {
                    val list = mutableListOf<DriveLogItem>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(
                            DriveLogItem(
                                id = obj.optString("id", i.toString()),
                                date = obj.optString("date", ""),
                                startLocation = obj.optString("start_location", ""),
                                endLocation = obj.optString("end_location", ""),
                                distanceKm = obj.optDouble("distance_km", 0.0),
                                energyUsedKwh = obj.optDouble("energy_used_kwh", 0.0),
                                efficiencyWhKm = obj.optDouble("efficiency_wh_km", 0.0)
                            )
                        )
                    }
                    return@withContext list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // 기본 제공
        return@withContext listOf(
            DriveLogItem("1", "2026-07-27", "서울 강남구", "경기 성남시", 24.5, 3.8, 155.0),
            DriveLogItem("2", "2026-07-26", "경기 성남시", "인천 연수구", 58.2, 9.2, 158.0),
            DriveLogItem("3", "2026-07-25", "인천 연수구", "서울 강남구", 52.1, 8.1, 155.0)
        )
    }

    // 배터리 열화 데이터
    suspend fun fetchBatteryDegradation(): List<BatteryDegradationItem> = withContext(Dispatchers.IO) {
        val settings = getSettings()
        if (settings.supabaseUrl.isNotBlank() && settings.supabaseKey.isNotBlank()) {
            try {
                val jsonArray = executeSupabaseGet("${settings.supabaseUrl}/rest/v1/battery_degradation?select=*&order=date.asc", settings.supabaseKey)
                if (jsonArray != null && jsonArray.length() > 0) {
                    val list = mutableListOf<BatteryDegradationItem>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(
                            BatteryDegradationItem(
                                date = obj.optString("date", ""),
                                degradationPercent = obj.optDouble("degradation_percent", 0.0),
                                fullRangeKm = obj.optDouble("full_range_km", 0.0)
                            )
                        )
                    }
                    return@withContext list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return@withContext listOf(
            BatteryDegradationItem("2024-01", 100.0, 505.0),
            BatteryDegradationItem("2024-06", 98.8, 499.0),
            BatteryDegradationItem("2025-01", 97.5, 492.0),
            BatteryDegradationItem("2025-06", 96.8, 489.0),
            BatteryDegradationItem("2026-01", 96.2, 486.0),
            BatteryDegradationItem("2026-07", 95.8, 484.0)
        )
    }

    // 감시 이벤트 데이터
    suspend fun fetchSentryEvents(): List<SentryEventItem> = withContext(Dispatchers.IO) {
        val settings = getSettings()
        if (settings.supabaseUrl.isNotBlank() && settings.supabaseKey.isNotBlank()) {
            try {
                val jsonArray = executeSupabaseGet("${settings.supabaseUrl}/rest/v1/sentry_events?select=*&order=created_at.desc", settings.supabaseKey)
                if (jsonArray != null && jsonArray.length() > 0) {
                    val list = mutableListOf<SentryEventItem>()
                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        list.add(
                            SentryEventItem(
                                id = obj.optString("id", i.toString()),
                                timestamp = obj.optString("timestamp", ""),
                                location = obj.optString("location", ""),
                                eventType = obj.optString("event_type", "Motion Detected"),
                                videoUrl = obj.optString("video_url", null)
                            )
                        )
                    }
                    return@withContext list
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return@withContext listOf(
            SentryEventItem("s1", "2026-07-27 13:42", "강남역 지하주차장 B2", "움직임 감지"),
            SentryEventItem("s2", "2026-07-26 19:15", "판교 아파트 지하주차장", "차량 근접 감지"),
            SentryEventItem("s3", "2026-07-24 11:05", "송도 야외 주차장", "충격 감지 (경미)")
        )
    }

    private fun executeSupabaseGet(urlString: String, apiKey: String): JSONArray? {
        var connection: HttpURLConnection? = null
        return try {
            val url = URL(urlString)
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("apikey", apiKey)
            connection.setRequestProperty("Authorization", "Bearer $apiKey")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode in 200..299) {
                val stream = connection.inputStream
                val responseText = stream.bufferedReader().use { it.readText() }
                JSONArray(responseText)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}
