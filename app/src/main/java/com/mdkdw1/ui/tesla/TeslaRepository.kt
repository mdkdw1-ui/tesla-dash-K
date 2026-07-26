package com.mdkdw1.ui.tesla

import android.content.Context
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TeslaRepository(private val context: Context) {
    private val settingsManager = EncryptedSettingsManager(context)
    private var supabaseClient: SupabaseClient? = null

    init {
        reinitializeSupabase()
    }

    fun reinitializeSupabase() {
        val settings = settingsManager.loadSettings()
        if (settings.supabaseUrl.isNotBlank() && settings.supabaseKey.isNotBlank()) {
            try {
                supabaseClient = createSupabaseClient(
                    supabaseUrl = settings.supabaseUrl,
                    supabaseKey = settings.supabaseKey
                ) {
                    install(Postgrest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                supabaseClient = null
            }
        } else {
            supabaseClient = null
        }
    }

    fun isConnectedToSupabase(): Boolean = supabaseClient != null

    suspend fun fetchVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        val client = supabaseClient ?: return@withContext getDummyVehicleState()
        try {
            // Supabase postgrest 연결 시도
            val response = client.postgrest["vehicle_state"]
                .select()
                .decodeList<VehicleState>()
            response.firstOrNull() ?: getDummyVehicleState()
        } catch (e: Exception) {
            e.printStackTrace()
            getDummyVehicleState()
        }
    }

    suspend fun fetchJournalLogs(): List<JournalLogItem> = withContext(Dispatchers.IO) {
        val client = supabaseClient ?: return@withContext getDummyJournalLogs()
        try {
            client.postgrest["journal_logs"]
                .select()
                .decodeList<JournalLogItem>()
        } catch (e: Exception) {
            e.printStackTrace()
            getDummyJournalLogs()
        }
    }

    suspend fun fetchBatteryRecords(): List<BatteryRecord> = withContext(Dispatchers.IO) {
        val client = supabaseClient ?: return@withContext getDummyBatteryRecords()
        try {
            client.postgrest["battery_records"]
                .select()
                .decodeList<BatteryRecord>()
        } catch (e: Exception) {
            e.printStackTrace()
            getDummyBatteryRecords()
        }
    }

    suspend fun fetchConsumables(): List<ConsumableItem> = withContext(Dispatchers.IO) {
        val client = supabaseClient ?: return@withContext getDummyConsumables()
        try {
            client.postgrest["consumables"]
                .select()
                .decodeList<ConsumableItem>()
        } catch (e: Exception) {
            e.printStackTrace()
            getDummyConsumables()
        }
    }

    // ==========================================
    // Supabase 미연결 시 표시용 더미 데이터
    // ==========================================
    private fun getDummyVehicleState(): VehicleState {
        return VehicleState(
            vehicleName = "Model Y Long Range",
            statusText = "주차 중 (더미)",
            batteryPercent = 82,
            estimatedRangeKm = 412,
            totalOdometer = 35240.0,
            isLocked = true,
            isClimateOn = false,
            insideTempC = 21.5,
            outsideTempC = 24.0,
            isTrunkOpen = false,
            isFrunkOpen = false,
            isSentryModeOn = true,
            speedKmh = 0,
            chargeStatus = "Discharging"
        )
    }

    private fun getDummyJournalLogs(): List<JournalLogItem> {
        return listOf(
            JournalLogItem("1", JournalType.DRIVE, "2026-07-26", "14:20", 42.5, 145, 85, 76, 0, 45, "서울 강남구 -> 경기 성남시"),
            JournalLogItem("2", JournalType.CHARGE, "2026-07-25", "22:10", 0.0, 0, 30, 80, 50, 35, "판교 슈퍼차저")
        )
    }

    private fun getDummyBatteryRecords(): List<BatteryRecord> {
        return listOf(
            BatteryRecord("2026-07-01", 100, 505.0, 1.2),
            BatteryRecord("2026-07-15", 100, 502.5, 1.7),
            BatteryRecord("2026-07-26", 100, 501.0, 2.0)
        )
    }

    private fun getDummyConsumables(): List<ConsumableItem> {
        return listOf(
            ConsumableItem("에어컨 필터", 12000, 15000, "2025-11-10"),
            ConsumableItem("와이퍼 블레이드", 8000, 20000, "2025-08-05"),
            ConsumableItem("타이어 위치 교환", 15000, 10000, "2025-01-15"),
            ConsumableItem("브레이크 오일", 35240, 40000, "2024-05-20")
        )
    }
}
