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

    fun getVehicles(callback: (List<TeslaVehicle>?, String?) -> Unit) {
        callback(emptyList(), null)
    }

    fun saveVehicleMeta(vehicle: TeslaVehicle) {
        prefsStore.saveVehicleId(vehicle.vehicleId ?: vehicle.id)
        prefsStore.saveVin(vehicle.vin)
        prefsStore.saveDisplayName(vehicle.displayName)
    }

    fun getVehicleData(vehicleId: Long, callback: (TeslaVehicleData?, String?) -> Unit) {
        callback(null, "not implemented")
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
