package com.mdkdw1.ui.tesla

class TeslaApiClient(
    private val prefsStore: PrefsStore
) {
    fun buildAuthorizeUrl(clientId: String, redirectUri: String): String {
        return "https://example.com/oauth/authorize?client_id=$clientId&redirect_uri=$redirectUri"
    }

    fun exchangeCodeViaBackend(backendCallbackUrl: String, code: String, callback: (Boolean, String?) -> Unit) {
        callback(true, null)
    }

    fun flashLights(vehicleId: Long, callback: (Boolean, String?) -> Unit) {
        callback(true, null)
    }

    fun startSentryMode(vehicleId: Long, callback: (Boolean, String?) -> Unit) {
        callback(true, null)
    }

    fun stopSentryMode(vehicleId: Long, callback: (Boolean, String?) -> Unit) {
        callback(true, null)
    }
}
