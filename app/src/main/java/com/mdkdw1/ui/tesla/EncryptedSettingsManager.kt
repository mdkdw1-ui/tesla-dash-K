package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme
import androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme
import androidx.security.crypto.MasterKeys

class EncryptedSettingsManager(context: Context) {

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val prefs = EncryptedSharedPreferences.create(
        "tesla_secure_settings",
        masterKeyAlias,
        context,
        PrefKeyEncryptionScheme.AES256_SKEY,
        PrefValueEncryptionScheme.AES256_GCM
    )

    fun getSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = prefs.getString("supabaseUrl", "") ?: "",
            supabaseKey = prefs.getString("supabaseKey", "") ?: "",
            kakaoKey = prefs.getString("kakaoKey", "") ?: "",
            teslaToken = prefs.getString("teslaToken", "") ?: "",
            vehicleId = prefs.getString("vehicleId", "") ?: ""
        )
    }

    fun saveSettings(settings: AppSettings) {
        prefs.edit()
            .putString("supabaseUrl", settings.supabaseUrl)
            .putString("supabaseKey", settings.supabaseKey)
            .putString("kakaoKey", settings.kakaoKey)
            .putString("teslaToken", settings.teslaToken)
            .putString("vehicleId", settings.vehicleId)
            .apply()
    }

    fun clearSettings() {
        prefs.edit().clear().apply()
    }
}
