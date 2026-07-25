package com.yourpackage.tesla

import com.google.gson.annotations.SerializedName

data class TeslaAuthToken(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("expires_in") val expiresIn: Long? = null,
    @SerializedName("token_type") val tokenType: String? = null,
    @SerializedName("scope") val scope: String? = null
)

data class TeslaVehicle(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("vehicle_id") val vehicleId: Long? = null,
    @SerializedName("vin") val vin: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("in_service") val inService: Boolean? = null,
    @SerializedName("last_seen") val lastSeen: String? = null
)

data class TeslaVehicleListResponse(
    @SerializedName("response") val response: List<TeslaVehicle>? = null,
    @SerializedName("count") val count: Int? = null
)

data class TeslaVehicleDataResponse(
    @SerializedName("response") val response: TeslaVehicleData? = null
)

data class TeslaVehicleData(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("vehicle_id") val vehicleId: Long? = null,
    @SerializedName("vin") val vin: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("vehicle_state") val vehicleState: VehicleState? = null,
    @SerializedName("charge_state") val chargeState: ChargeState? = null,
    @SerializedName("drive_state") val driveState: DriveState? = null,
    @SerializedName("climate_state") val climateState: ClimateState? = null,
    @SerializedName("gui_settings") val guiSettings: GuiSettings? = null,
    @SerializedName("vehicle_config") val vehicleConfig: VehicleConfig? = null
)

data class VehicleState(
    @SerializedName("locked") val locked: Boolean? = null,
    @SerializedName("sentry_mode") val sentryMode: Boolean? = null,
    @SerializedName("df") val driverFrontDoor: Int? = null,
    @SerializedName("dr") val driverRearDoor: Int? = null,
    @SerializedName("pf") val passengerFrontDoor: Int? = null,
    @SerializedName("pr") val passengerRearDoor: Int? = null,
    @SerializedName("ft") val frontTrunk: Int? = null,
    @SerializedName("rt") val rearTrunk: Int? = null,
    @SerializedName("odometer") val odometer: Double? = null,
    @SerializedName("vehicle_name") val vehicleName: String? = null,
    @SerializedName("timestamp") val timestamp: Long? = null,
    @SerializedName("valet_mode") val valetMode: Boolean? = null
) {
    fun isAnyDoorOpen(): Boolean {
        return (driverFrontDoor ?: 0) > 0 ||
            (driverRearDoor ?: 0) > 0 ||
            (passengerFrontDoor ?: 0) > 0 ||
            (passengerRearDoor ?: 0) > 0
    }

    fun isAnyTrunkOpen(): Boolean {
        return (frontTrunk ?: 0) > 0 || (rearTrunk ?: 0) > 0
    }

    fun isSentryActive(): Boolean = sentryMode == true
    fun isLocked(): Boolean = locked == true
}

data class ChargeState(
    @SerializedName("battery_level") val batteryLevel: Int? = null,
    @SerializedName("battery_range") val batteryRange: Double? = null,
    @SerializedName("charger_power") val chargerPower: Double? = null,
    @SerializedName("charging_state") val chargingState: String? = null,
    @SerializedName("charge_energy_added") val chargeEnergyAdded: Double? = null,
    @SerializedName("charge_miles_added_rated") val chargeMilesAddedRated: Double? = null,
    @SerializedName("charge_miles_added_ideal") val chargeMilesAddedIdeal: Double? = null
) {
    fun isCharging(): Boolean {
        return chargingState.equals("Charging", ignoreCase = true) || (chargerPower ?: 0.0) > 0.0
    }
}

data class DriveState(
    @SerializedName("shift_state") val shiftState: String? = null,
    @SerializedName("speed") val speed: Double? = null,
    @SerializedName("power") val power: Double? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("heading") val heading: Int? = null
) {
    fun isDriving(): Boolean = (speed ?: 0.0) > 0.0 || !shiftState.isNullOrBlank()
}

data class ClimateState(
    @SerializedName("inside_temp") val insideTemp: Double? = null,
    @SerializedName("outside_temp") val outsideTemp: Double? = null,
    @SerializedName("driver_temp_setting") val driverTempSetting: Double? = null,
    @SerializedName("passenger_temp_setting") val passengerTempSetting: Double? = null,
    @SerializedName("is_auto_conditioning_on") val isAutoConditioningOn: Boolean? = null,
    @SerializedName("fan_status") val fanStatus: Int? = null
)

data class GuiSettings(
    @SerializedName("gui_24_hour_time") val is24HourTime: Boolean? = null,
    @SerializedName("gui_distance_units") val distanceUnits: String? = null,
    @SerializedName("gui_range_display") val rangeDisplay: String? = null,
    @SerializedName("gui_charge_rate_units") val chargeRateUnits: String? = null,
    @SerializedName("gui_temperature_units") val temperatureUnits: String? = null
)

data class VehicleConfig(
    @SerializedName("car_type") val carType: String? = null,
    @SerializedName("charge_port_type") val chargePortType: String? = null,
    @SerializedName("exterior_color") val exteriorColor: String? = null,
    @SerializedName("has_seat_heaters") val hasSeatHeaters: Boolean? = null,
    @SerializedName("wheel_type") val wheelType: String? = null
)

data class SentryStatus(
    val isActive: Boolean,
    val isLocked: Boolean,
    val isDoorOpen: Boolean,
    val isTrunkOpen: Boolean,
    val label: String
)

data class VehicleSnapshot(
    val vehicleId: Long?,
    val vin: String?,
    val displayName: String?,
    val batteryLevel: Int?,
    val batteryRange: Double?,
    val chargerPower: Double?,
    val sentryMode: Boolean,
    val locked: Boolean,
    val doorOpen: Boolean,
    val trunkOpen: Boolean,
    val speed: Double?,
    val odometer: Double?,
    val outsideTemp: Double?,
    val chargingState: String?,
    val shiftState: String?,
    val updatedAt: Long
)

data class TripRecord(
    val id: String,
    val timestamp: Long,
    val distanceKm: Double,
    val batteryUsedPct: Double,
    val startAddress: String,
    val endAddress: String,
    val durationMin: Int,
    val path: List<RoutePoint> = emptyList(),
    val odometer: Double? = null
)

data class RoutePoint(
    val lat: Double,
    val lng: Double
)

data class NtfyMessage(
    val title: String,
    val message: String,
    val priority: String = "high",
    val tags: String = "car,warning"
)

data class GuardianConfig(
    val pollIntervalSec: Int = 5,
    val ntfyTopic: String = "",
    val vehicleId: Long? = null
)
