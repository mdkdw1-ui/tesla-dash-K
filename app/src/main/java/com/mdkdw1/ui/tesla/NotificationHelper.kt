package com.mdkdw1.ui.tesla

import android.content.Context

class NotificationHelper(private val prefsStore: PrefsStore) {

    private val context: Context
        get() = throw UnsupportedOperationException("Implement notification context if needed")

    fun shouldNotify(): Boolean = true
}
