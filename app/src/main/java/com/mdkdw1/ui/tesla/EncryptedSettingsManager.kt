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
        "tesla_encrypted_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = sharedPreferences.getString("supabase_url", "") ?: "",
            supabaseKey = sharedPreferences.getString("supabase_key", "") ?: "",
            kakaoMapKey = sharedPreferences.getString("kakao_map_key", "") ?: "",
            teslaClientId = sharedPreferences.getString("tesla_client_id", "") ?: "",
            teslaClientSecret = sharedPreferences.getString("tesla_client_secret", "") ?: "",
            githubKey = sharedPreferences.getString("github_key", "") ?: "",
            githubToken = sharedPreferences.getString("github_token", "") ?: "",
            isAutoSync = sharedPreferences.getBoolean("is_auto_sync", true)
        )
    }

    fun loadSettings(): AppSettings = getSettings()

    fun saveSettings(settings: AppSettings) {
        sharedPreferences.edit().apply {
            putString("supabase_url", settings.supabaseUrl)
            putString("supabase_key", settings.supabaseKey)
            putString("kakao_map_key", settings.kakaoMapKey)
            putString("tesla_client_id", settings.teslaClientId)
            putString("tesla_client_secret", settings.teslaClientSecret)
            putString("github_key", settings.githubKey)
            putString("github_token", settings.githubToken)
            putBoolean("is_auto_sync", settings.isAutoSync)
            apply()
        }
    }
}
