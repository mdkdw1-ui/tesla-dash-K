package com.mdkdw1.ui.tesla

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedSettingsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "tesla_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getConfig(): AppConfig {
        return AppConfig(
            supabaseUrl = prefs.getString("supabase_url", "") ?: "",
            supabaseKey = prefs.getString("supabase_key", "") ?: "",
            kakaoMapKey = prefs.getString("kakao_map_key", "") ?: "",
            vehicleId = prefs.getString("vehicle_id", "") ?: ""
        )
    }

    fun saveConfig(config: AppConfig) {
        prefs.edit().apply {
            putString("supabase_url", config.supabaseUrl)
            putString("supabase_key", config.supabaseKey)
            putString("kakao_map_key", config.kakaoMapKey)
            putString("vehicle_id", config.vehicleId)
            apply()
        }
    }

    fun loadConfig(): AppConfig = getConfig()
    fun getAppConfig(): AppConfig = getConfig()
    fun saveAppConfig(config: AppConfig) = saveConfig(config)

    fun getSupabaseUrl(): String = prefs.getString("supabase_url", "") ?: ""
    fun saveSupabaseUrl(url: String) = prefs.edit().putString("supabase_url", url).apply()

    fun getSupabaseKey(): String = prefs.getString("supabase_key", "") ?: ""
    fun saveSupabaseKey(key: String) = prefs.edit().putString("supabase_key", key).apply()

    fun getKakaoMapKey(): String = prefs.getString("kakao_map_key", "") ?: ""
    fun saveKakaoMapKey(key: String) = prefs.edit().putString("kakao_map_key", key).apply()

    fun getVehicleId(): String = prefs.getString("vehicle_id", "") ?: ""
    fun saveVehicleId(id: String) = prefs.edit().putString("vehicle_id", id).apply()
}

// TeslaHubUI 및 기타 UI 호환성을 위한 타입 별칭
typealias SecureSettingsManager = EncryptedSettingsManager
