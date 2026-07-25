package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PrefsStore(context: Context) {
    private val appContext = context.applicationContext
    private val masterKey by lazy { MasterKey.Builder(appContext).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build() }
    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            appContext, PREF_FILE, masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveClientId(value: String?) = prefs.edit().putString(KEY_CLIENT_ID, value.orEmpty()).apply()
    fun getClientId(): String = prefs.getString(KEY_CLIENT_ID, "").orEmpty()
    fun saveBackendUrl(value: String?) = prefs.edit().putString(KEY_BACKEND_URL, value.orEmpty()).apply()
    fun getBackendUrl(): String = prefs.getString(KEY_BACKEND_URL, "").orEmpty()
    fun saveKakaoNativeAppKey(value: String?) = prefs.edit().putString(KEY_KAKAO_NATIVE_APP_KEY, value.orEmpty()).apply()
    fun getKakaoNativeAppKey(): String = prefs.getString(KEY_KAKAO_NATIVE_APP_KEY, "").orEmpty()
    fun saveNtfyTopic(value: String?) = prefs.edit().putString(KEY_NTFY_TOPIC, value.orEmpty()).apply()
    fun getNtfyTopic(): String = prefs.getString(KEY_NTFY_TOPIC, "").orEmpty()
    fun saveVehicleId(value: Long?) { if (value == null) prefs.edit().remove(KEY_VEHICLE_ID).apply() else prefs.edit().putLong(KEY_VEHICLE_ID, value).apply() }
    fun getVehicleId(): Long? = if (prefs.contains(KEY_VEHICLE_ID)) prefs.getLong(KEY_VEHICLE_ID, -1L).takeIf { it > 0 } else null
    fun saveVin(value: String?) = prefs.edit().putString(KEY_VIN, value.orEmpty()).apply()
    fun getVin(): String = prefs.getString(KEY_VIN, "").orEmpty()
    fun saveDisplayName(value: String?) = prefs.edit().putString(KEY_DISPLAY_NAME, value.orEmpty()).apply()
    fun getDisplayName(): String = prefs.getString(KEY_DISPLAY_NAME, "").orEmpty()

    companion object {
        private const val PREF_FILE = "tesla_dashboard_secure_prefs"
        private const val KEY_CLIENT_ID = "client_id"
        private const val KEY_BACKEND_URL = "backend_url"
        private const val KEY_KAKAO_NATIVE_APP_KEY = "kakao_native_app_key"
        private const val KEY_NTFY_TOPIC = "ntfy_topic"
        private const val KEY_VEHICLE_ID = "vehicle_id"
        private const val KEY_VIN = "vin"
        private const val KEY_DISPLAY_NAME = "display_name"
    }
}
