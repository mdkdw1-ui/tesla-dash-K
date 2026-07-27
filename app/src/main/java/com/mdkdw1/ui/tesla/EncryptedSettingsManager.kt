package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedSettingsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "tesla_dash_encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSettings(settings: AppSettings) {
        prefs.edit().apply {
            putString("supabase_url", settings.supabaseUrl)
            putString("supabase_key", settings.supabaseKey)
            putString("kakao_key", settings.kakaoKey)
            putString("tesla_token", settings.teslaToken)
            putString("vehicle_id", settings.vehicleId)
            apply()
        }
    }

    fun loadSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = prefs.getString("supabase_url", "") ?: "",
            supabaseKey = prefs.getString("supabase_key", "") ?: "",
            kakaoKey = prefs.getString("kakao_key", "") ?: "",
            teslaToken = prefs.getString("tesla_token", "") ?: "",
            vehicleId = prefs.getString("vehicle_id", "") ?: ""
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
