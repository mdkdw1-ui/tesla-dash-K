package com.mdkdw1.ui.tesla

import android.content.Context
import android.content.SharedPreferences

class PrefsStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tesla_prefs", Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String? = null): String? = prefs.getString(key, defaultValue)
    
    fun putString(key: String, value: String?) {
        if (value != null) {
            prefs.edit().putString(key, value).apply()
        } else {
            prefs.edit().remove(key).apply()
        }
    }

    fun clear() = prefs.edit().clear().apply()

    // 호출부에서 Non-null String을 기대하는 메서드들 (빈 문자열 기본값 처리)
    fun getAccessToken(): String = getString("access_token") ?: ""
    fun getNtfyTopic(): String = getString("ntfy_topic") ?: ""

    // AppRepository에서 Long? 타입을 기대하는 차량 ID 처리
    fun getVehicleId(): Long? = getString("vehicle_id")?.toLongOrNull() ?: prefs.getLong("vehicle_id", 0L).takeIf { it != 0L }
    
    fun saveVehicleId(id: Long?) {
        if (id != null) putString("vehicle_id", id.toString()) else prefs.edit().remove("vehicle_id").apply()
    }
    
    fun saveVehicleId(id: String?) {
        if (id != null) putString("vehicle_id", id) else prefs.edit().remove("vehicle_id").apply()
    }

    // Nullable String 파라미터를 수용하는 저장 메서드들
    fun saveVin(vin: String?) = putString("vin", vin)
    fun saveDisplayName(name: String?) = putString("display_name", name)

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
