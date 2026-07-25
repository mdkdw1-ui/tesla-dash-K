package com.mdkdw1.ui.tesla

import android.content.Context
import android.util.Log

class NotificationHelper(private val prefsStore: PrefsStore) {

    fun shouldNotify(): Boolean = prefsStore.getNtfyTopic().isNotBlank()

    fun sendGuardianAlert(title: String, message: String, priority: String = "high") {
        if (!shouldNotify()) return
        Log.d("NotificationHelper", "guardian alert: $title / $message / $priority")
    }
}
