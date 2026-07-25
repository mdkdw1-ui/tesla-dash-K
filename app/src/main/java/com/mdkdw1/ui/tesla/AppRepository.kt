package com.mdkdw1.ui.tesla

import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean

class AppRepository(
    private val prefsStore: PrefsStore,
    private val apiClient: TeslaApiClient,
    private val notificationHelper: NotificationHelper
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val isRefreshing = AtomicBoolean(false)

    @Volatile
    var latestVehicleData: TeslaVehicleData? = null

    @Volatile
    var latestSnapshot: VehicleSnapshot? = null

    @Volatile
    var latestVehicles: List<TeslaVehicle> = emptyList()

    fun hasToken(): Boolean = prefsStore.getAccessToken().isNotBlank()

    fun hasVehicleId(): Boolean = prefsStore.getVehicleId() != null

    fun clearAuth() {
        prefsStore.clearAuth()
        latestVehicleData = null
        latestSnapshot = null
        latestVehicles = emptyList()
    }

    fun loadVehicles(callback: (List<TeslaVehicle>?, String?) -> Unit) {
        apiClient.getVehicles { vehicles, error ->
            if (!vehicles.isNullOrEmpty()) {
                latestVehicles = vehicles
                vehicles.firstOrNull()?.let { apiClient.saveVehicleMeta(it) }
            }
            callback(vehicles, error)
        }
    }

    fun refreshLatestVehicleData(callback: (VehicleSnapshot?, String?) -> Unit) {
        val vehicleId = resolveVehicleId()
        if (vehicleId == null) {
            callback(null, "vehicle id missing")
            return
        }

        if (!isRefreshing.compareAndSet(false, true)) {
            callback(latestSnapshot, "refresh already running")
            return
        }

        apiClient.getVehicleData(vehicleId) { data, error ->
            isRefreshing.set(false)

            if (data != null) {
                latestVehicleData = data
                latestSnapshot = toSnapshot(data)
                callback(latestSnapshot, null)
            } else {
                callback(null, error)
            }
        }
    }

    fun isSentryActive(): Boolean {
        val data = latestVehicleData ?: return prefsStore.getLastSentryState()
        return data.vehicleState?.isSentryActive() == true
    }

    fun updateSentryStateCache(isActive: Boolean) {
        prefsStore.saveLastSentryState(isActive)
    }

    fun triggerFlashLights(callback: (Boolean, String?) -> Unit) {
        val vehicleId = resolveVehicleId()
        if (vehicleId == null) {
            callback(false, "vehicle id missing")
            return
        }
        apiClient.flashLights(vehicleId, callback)
    }

    fun runGuardChecks() {
        val snapshot = latestSnapshot ?: return
        val locked = snapshot.locked
        val open = snapshot.doorOpen || snapshot.trunkOpen
        if (locked && open) {
            notificationHelper.sendGuardianAlert(
                title = "Tesla 무단 열림 감지",
                message = "차량이 잠긴 상태에서 도어 또는 트렁크가 열렸습니다.",
                priority = "high"
            )
        }
    }

    fun postToMain(block: () -> Unit) {
        mainHandler.post(block)
    }

    private fun resolveVehicleId(): Long? {
        return prefsStore.getVehicleId()
            ?: latestVehicles.firstOrNull()?.vehicleId
            ?: latestVehicles.firstOrNull()?.id
    }

    private fun toSnapshot(data: TeslaVehicleData): VehicleSnapshot {
        val vehicleState = data.vehicleState
        val chargeState = data.chargeState
        val driveState = data.driveState
        val climateState = data.climateState

        return VehicleSnapshot(
            vehicleId = data.vehicleId ?: data.id,
            vin = data.vin,
            displayName = data.displayName,
            batteryLevel = chargeState?.batteryLevel,
            batteryRange = chargeState?.batteryRange,
            chargerPower = chargeState?.chargerPower,
            sentryMode = vehicleState?.isSentryActive() == true,
            locked = vehicleState?.isLocked() == true,
            doorOpen = vehicleState?.isAnyDoorOpen() == true,
            trunkOpen = vehicleState?.isAnyTrunkOpen() == true,
            speed = driveState?.speed,
            odometer = vehicleState?.odometer,
            outsideTemp = climateState?.outsideTemp,
            chargingState = chargeState?.chargingState,
            shiftState = driveState?.shiftState,
            updatedAt = System.currentTimeMillis()
        )
    }
}
