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
        "tesla_hub_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString("KEY_SUPABASE_URL", settings.supabaseUrl)
            .putString("KEY_SUPABASE_KEY", settings.supabaseKey)
            .putString("KEY_KAKAO_MAP_KEY", settings.kakaoMapKey)
            .putString("KEY_TESLA_TOKEN", settings.teslaAccessToken)
            .putString("KEY_GITHUB_TOKEN", settings.githubToken)
            .apply()
    }

    fun loadSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = prefs.getString("KEY_SUPABASE_URL", "") ?: "",
            supabaseKey = prefs.getString("KEY_SUPABASE_KEY", "") ?: "",
            kakaoMapKey = prefs.getString("KEY_KAKAO_MAP_KEY", "") ?: "",
            teslaAccessToken = prefs.getString("KEY_TESLA_TOKEN", "") ?: "",
            githubToken = prefs.getString("KEY_GITHUB_TOKEN", "") ?: ""
        )
    }
}
