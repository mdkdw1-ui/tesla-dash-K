package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKey

class EncryptedSettingsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_tesla_settings",
        masterKey,
        PrefKeyEncryptionScheme.AES256_SKEY,
        PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSettings(settings: AppSettings) {
        sharedPreferences.edit()
            .putString("supabase_url", settings.supabaseUrl)
            .putString("supabase_key", settings.supabaseKey)
            .putString("kakao_key", settings.kakaoKey)
            .putString("tesla_token", settings.teslaToken)
            .putString("vehicle_id", settings.vehicleId)
            .apply()
    }

    fun loadSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = sharedPreferences.getString("supabase_url", "") ?: "",
            supabaseKey = sharedPreferences.getString("supabase_key", "") ?: "",
            kakaoKey = sharedPreferences.getString("kakao_key", "") ?: "",
            teslaToken = sharedPreferences.getString("tesla_token", "") ?: "",
            vehicleId = sharedPreferences.getString("vehicle_id", "") ?: ""
        )
    }

    fun clearSettings() {
        sharedPreferences.edit().clear().apply()
    }
}
