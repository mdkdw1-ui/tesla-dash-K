package com.mdkdw1.ui.tesla

import android.content.Context
import android.content.SharedPreferences

class PrefsStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tesla_prefs", Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String? = null): String? = prefs.getString(key, defaultValue)
    fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    fun clear() = prefs.edit().clear().apply()

    // AppRepository, TeslaApiClient, NotificationHelper 호환 함수
    fun getAccessToken(): String? = getString("access_token")
    fun getVehicleId(): String? = getString("vehicle_id")
    fun saveVehicleId(id: String) = putString("vehicle_id", id)
    fun saveVin(vin: String) = putString("vin", vin)
    fun saveDisplayName(name: String) = putString("display_name", name)
    fun getNtfyTopic(): String? = getString("ntfy_topic")

    fun getLastSentryState(): Boolean = prefs.getBoolean("last_sentry_state", false)
    fun saveLastSentryState(state: Boolean) = prefs.edit().putBoolean("last_sentry_state", state).apply()

    fun clearAuth() {
        prefs.edit()
            .remove("access_token")
            .remove("refresh_token")
            .remove("vehicle_id")
            .apply()
    }
}
