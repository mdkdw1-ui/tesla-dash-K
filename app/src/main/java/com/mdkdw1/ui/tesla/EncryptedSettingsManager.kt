package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class EncryptedSettingsManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "tesla_dash_k_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSettings(settings: AppSettings) {
        sharedPreferences.edit()
            .putString("supabase_url", settings.supabaseUrl)
            .putString("supabase_key", settings.supabaseKey)
            .putString("kakao_map_key", settings.kakaoMapKey)
            .putString("github_token", settings.githubToken)
            .putBoolean("auto_sync", settings.isAutoSync)
            .putInt("sync_interval", settings.syncIntervalMinutes)
            .apply()
    }

    fun getSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = sharedPreferences.getString("supabase_url", "") ?: "",
            supabaseKey = sharedPreferences.getString("supabase_key", "") ?: "",
            kakaoMapKey = sharedPreferences.getString("kakao_map_key", "") ?: "",
            githubToken = sharedPreferences.getString("github_token", "") ?: "",
            isAutoSync = sharedPreferences.getBoolean("auto_sync", true),
            syncIntervalMinutes = sharedPreferences.getInt("sync_interval", 15)
        )
    }

    fun clearSettings() {
        sharedPreferences.edit().clear().apply()
    }
}
