package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.github.jan-tennert.supabase.SupabaseClient
import io.github.jan-tennert.supabase.createSupabaseClient
import io.github.jan-tennert.supabase.postgrest.Postgrest
import io.github.jan-tennert.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EncryptedSettingsManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "tesla_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveConfig(config: AppConfig) {
        prefs.edit().apply {
            putString("supabase_url", config.supabaseUrl)
            putString("supabase_key", config.supabaseKey)
            putString("kakao_map_key", config.kakaoMapKey)
            putString("github_token", config.githubToken)
            apply()
        }
    }

    fun getConfig(): AppConfig {
        return AppConfig(
            supabaseUrl = prefs.getString("supabase_url", "") ?: "",
            supabaseKey = prefs.getString("supabase_key", "") ?: "",
            kakaoMapKey = prefs.getString("kakao_map_key", "") ?: "",
            githubToken = prefs.getString("github_token", "") ?: ""
        )
    }
}

class TeslaRepository(private val settingsManager: EncryptedSettingsManager) {
    private var supabaseClient: SupabaseClient? = null

    private fun initSupabase() {
        val config = settingsManager.getConfig()
        if (config.supabaseUrl.isNotEmpty() && config.supabaseKey.isNotEmpty()) {
            supabaseClient = createSupabaseClient(
                supabaseUrl = config.supabaseUrl,
                supabaseKey = config.supabaseKey
            ) {
                install(Postgrest)
            }
        }
    }

    suspend fun fetchVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        if (supabaseClient == null) initSupabase()
        return@withContext try {
            supabaseClient?.from("vehicle_state")
                ?.select()
                ?.decodeList<VehicleState>()
                ?.firstOrNull() ?: VehicleState()
        } catch (e: Exception) {
            VehicleState()
        }
    }

    suspend fun fetchLatestDailyTrip(): DailyTripRecord = withContext(Dispatchers.IO) {
        if (supabaseClient == null) initSupabase()
        return@withContext try {
            supabaseClient?.from("daily_trips")
                ?.select()
                ?.decodeList<DailyTripRecord>()
                ?.firstOrNull() ?: DailyTripRecord()
        } catch (e: Exception) {
            DailyTripRecord()
        }
    }
}
