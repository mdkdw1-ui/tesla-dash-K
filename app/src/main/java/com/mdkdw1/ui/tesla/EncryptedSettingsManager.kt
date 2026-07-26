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
        "tesla_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun loadSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = prefs.getString("supabase_url", "") ?: "",
            supabaseKey = prefs.getString("supabase_key", "") ?: "",
            kakaoMapKey = prefs.getString("kakao_map_key", "") ?: "",
            githubKey = prefs.getString("github_key", "") ?: "",
            teslaAccessToken = prefs.getString("tesla_access_token", "") ?: "",
            githubToken = prefs.getString("github_token", "") ?: "",
            isAutoSync = prefs.getBoolean("is_auto_sync", true)
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString("supabase_url", settings.supabaseUrl)
            .putString("supabase_key", settings.supabaseKey)
            .putString("kakao_map_key", settings.kakaoMapKey)
            .putString("github_key", settings.githubKey)
            .putString("tesla_access_token", settings.teslaAccessToken)
            .putString("github_token", settings.githubToken)
            .putBoolean("is_auto_sync", settings.isAutoSync)
            .apply()
    }
}
