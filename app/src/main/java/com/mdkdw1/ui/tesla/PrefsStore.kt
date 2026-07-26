package com.mdkdw1.ui.tesla

import android.content.Context
import android.content.SharedPreferences

class PrefsStore(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tesla_prefs", Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String? = null): String? = prefs.getString(key, defaultValue)
    fun putString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    fun clear() = prefs.edit().clear().apply()
}
