package com.example.tesladashk.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object SupabaseRepository {

    private fun cleanUrl(baseUrl: String): String {
        var url = baseUrl.trim()
        if (url.endsWith("/")) url = url.substring(0, url.length - 1)
        if (!url.endsWith("/rest/v1")) {
            url = "$url/rest/v1"
        }
        return url
    }

    suspend fun fetchVehicleData(baseUrl: String, apiKey: String, userUid: String): Result<List<VehicleDto>> = withContext(Dispatchers.IO) {
        try {
            val endpoint = if (userUid.isNotBlank()) {
                "${cleanUrl(baseUrl)}/vehicle?select=*&user_uid=eq.$userUid&order=updated_at.desc&limit=20"
            } else {
                "${cleanUrl(baseUrl)}/vehicle?select=*&order=updated_at.desc&limit=20"
            }

            val conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", apiKey.trim())
            conn.setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val json = conn.inputStream.bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<VehicleDto>>() {}.type
                val list = Gson().fromJson<List<VehicleDto>>(json, type) ?: emptyList()
                Result.success(list)
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("Supabase HTTP Error $responseCode: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchDrivingData(baseUrl: String, apiKey: String, userUid: String): Result<List<DrivingDto>> = withContext(Dispatchers.IO) {
        try {
            val endpoint = if (userUid.isNotBlank()) {
                "${cleanUrl(baseUrl)}/driving?select=*&user_uid=eq.$userUid&order=created_at.desc&limit=50"
            } else {
                "${cleanUrl(baseUrl)}/driving?select=*&order=created_at.desc&limit=50"
            }

            val conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", apiKey.trim())
            conn.setRequestProperty("Authorization", "Bearer ${apiKey.trim()}")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.connectTimeout = 8000
            conn.readTimeout = 8000

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val json = conn.inputStream.bufferedReader().use { it.readText() }
                val type = object : TypeToken<List<DrivingDto>>() {}.type
                val list = Gson().fromJson<List<DrivingDto>>(json, type) ?: emptyList()
                Result.success(list)
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("Supabase HTTP Error $responseCode: $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testKakaoMapApi(restApiKey: String, longitude: Double = 126.9780, latitude: Double = 37.5665): Result<String> = withContext(Dispatchers.IO) {
        try {
            // 카카오 API는 x가 경도(Longitude), y가 위도(Latitude)입니다.
            val endpoint = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=$longitude&y=$latitude"
            val conn = URL(endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            // 핵심: 'KakaoAK ' 접두사 필수 (공백 1칸)
            conn.setRequestProperty("Authorization", "KakaoAK ${restApiKey.trim()}")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val json = conn.inputStream.bufferedReader().use { it.readText() }
                val res = Gson().fromJson(json, KakaoRegionResponse::class.java)
                val address = res.documents?.firstOrNull()?.addressName ?: "주소 정보 없음"
                Result.success("연결 성공: $address")
            } else {
                val err = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "HTTP $responseCode"
                Result.failure(Exception("카카오 API 실패 ($responseCode): REST API 키 및 등록된 앱 설정(REST Key)을 확인하세요. - $err"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
