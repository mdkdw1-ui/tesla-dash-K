package com.mdkdw1/ui/tesla

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedSettingsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "encrypted_tesla_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSettings(settings: AppSettings) {
        sharedPreferences.edit()
            .putString("supabase_url", settings.supabaseUrl)
            .putString("supabase_key", settings.supabaseKey)
            .putString("kakao_key", settings.kakaoKey)
            .putString("tesla_token", settings.teslaToken)
            .putString("vehicle_id", settings.vehicleId)
            .putString("github_key", settings.githubKey)
            .apply()
    }

    fun loadSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = sharedPreferences.getString("supabase_url", "") ?: "",
            supabaseKey = sharedPreferences.getString("supabase_key", "") ?: "",
            kakaoKey = sharedPreferences.getString("kakao_key", "") ?: "",
            teslaToken = sharedPreferences.getString("tesla_token", "") ?: "",
            vehicleId = sharedPreferences.getString("vehicle_id", "") ?: "",
            githubKey = sharedPreferences.getString("github_key", "") ?: ""
        )
    }

    fun clearSettings() {
        sharedPreferences.edit().clear().apply()
    }
}
