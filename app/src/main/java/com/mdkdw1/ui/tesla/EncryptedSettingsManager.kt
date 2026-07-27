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
        "encrypted_tesla_settings_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveSettings(settings: AppSettings) {
        sharedPreferences.edit().apply {
            putString("supabaseUrl", settings.supabaseUrl)
            putString("supabaseAnonKey", settings.supabaseAnonKey)
            putString("kakaoMapKey", settings.kakaoMapKey)
            putString("teslaRefreshToken", settings.teslaRefreshToken)
            putString("aesPassword", settings.aesPassword)
            putInt("targetSoc", settings.targetSoc)
            putInt("chargeAmps", settings.chargeAmps)
            putInt("updateIntervalSec", settings.updateIntervalSec)
            putBoolean("autoRefresh", settings.autoRefresh)
            putBoolean("pushNotification", settings.pushNotification)
            apply()
        }
    }

    fun loadSettings(): AppSettings {
        return AppSettings(
            supabaseUrl = sharedPreferences.getString("supabaseUrl", "") ?: "",
            supabaseAnonKey = sharedPreferences.getString("supabaseAnonKey", "") ?: "",
            kakaoMapKey = sharedPreferences.getString("kakaoMapKey", "") ?: "",
            teslaRefreshToken = sharedPreferences.getString("teslaRefreshToken", "") ?: "",
            aesPassword = sharedPreferences.getString("aesPassword", "") ?: "",
            targetSoc = sharedPreferences.getInt("targetSoc", 80),
            chargeAmps = sharedPreferences.getInt("chargeAmps", 32),
            updateIntervalSec = sharedPreferences.getInt("updateIntervalSec", 30),
            autoRefresh = sharedPreferences.getBoolean("autoRefresh", true),
            pushNotification = sharedPreferences.getBoolean("pushNotification", true)
        )
    }
}
