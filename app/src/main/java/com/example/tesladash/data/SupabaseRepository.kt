package com.example.tesladash.data

import com.example.tesladash.model.Trip
import com.example.tesladash.model.VehicleLog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SupabaseRepository {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    // 💡 Vercel API 엔드포인트 URL (서버 주소 입력 시 자동 연동)
    private val vercelApiBaseUrl = "https://my-tesla-app.vercel.app/api"

    suspend fun fetchTrips(): List<Trip> = withContext(Dispatchers.IO) {
        try {
            client.get("$vercelApiBaseUrl/trips").body<List<Trip>>()
        } catch (e: Exception) {
            // 통신 전이거나 백엔드 연결 전일 경우 Fallback 목업 데이터 반환
            getMockTrips()
        }
    }

    suspend fun fetchVehicleLogs(): List<VehicleLog> = withContext(Dispatchers.IO) {
        try {
            client.get("$vercelApiBaseUrl/vehicle-logs").body<List<VehicleLog>>()
        } catch (e: Exception) {
            // 통신 전이거나 백엔드 연결 전일 경우 Fallback 목업 데이터 반환
            getMockLogs()
        }
    }

    // 기본 테스트용 샘플 주행 데이터
    private fun getMockTrips(): List<Trip> {
        return listOf(
            Trip("2026-07-24T08:00:00Z", 185.4, "서울 강남구", "대전 유성구", 125),
            Trip("2026-07-22T14:30:00Z", 92.1, "서울 강남구", "경기 용인시", 65),
            Trip("2026-07-20T19:10:00Z", 45.8, "서울 서초구", "서울 송파구", 40),
            Trip("2026-07-18T11:00:00Z", 34.2, "서울 마포구", "경기 고양시", 35),
            Trip("2026-07-15T09:20:00Z", 12.0, "서울 강남구", "서울 성동구", 20),
            Trip("2026-07-10T08:00:00Z", 5.4, "서울 강남구", "서울 강남구", 10)
        )
    }

    // 기본 테스트용 샘플 배터리 데이터
    private fun getMockLogs(): List<VehicleLog> {
        return listOf(
            VehicleLog("2026-07-20T00:00:00Z", 80, 335.0),
            VehicleLog("2026-07-21T00:00:00Z", 82, 342.0),
            VehicleLog("2026-07-22T00:00:00Z", 85, 355.0),
            VehicleLog("2026-07-23T00:00:00Z", 88, 368.0),
            VehicleLog("2026-07-24T00:00:00Z", 90, 376.0)
        )
    }
}
