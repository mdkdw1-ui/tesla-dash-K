package com.mdkdw1.ui.tesla

import com.google.gson.annotations.SerializedName

data class TeslaAuthToken(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("token_type") val tokenType: String? = null,
    @SerializedName("expires_in") val expiresIn: Long? = null
)

data class TeslaVehicleListResponse(
    val response: List<TeslaVehicle>? = null,
    val count: Int? = null
)

data class TeslaVehicle(
    val id: Long? = null,
    @SerializedName("vehicle_id") val vehicleId: Long? = null,
    val vin: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("in_service") val inService: Boolean? = null
)

data class TeslaVehicleDataResponse(
    val response: TeslaVehicleData? = null
)

data class TeslaVehicleData(
    val id: Long? = null,
    @SerializedName("vehicle_id") val vehicleId: Long? = null,
    val vin: String? = null,
    @SerializedName("display_name") val displayName: String? = null,
    @SerializedName("vehicle_state") val vehicleState: VehicleState? = null,
    @SerializedName("charge_state") val chargeState: ChargeState? = null,
    @SerializedName("drive_state") val driveState: DriveState? = null,
    @SerializedName("climate_state") val climateState: ClimateState? = null
)

data class VehicleState(
    val locked: Boolean? = null,
    val odometer: Double? = null,
    @SerializedName("df") val driverFrontDoorOpen: Int? = null,
    @SerializedName("dr") val driverRearDoorOpen: Int? = null,
    @SerializedName("pf") val passengerFrontDoorOpen: Int? = null,
    @SerializedName("pr") val passengerRearDoorOpen: Int? = null,
    @SerializedName("ft") val frontTrunkOpen: Int? = null,
    @SerializedName("rt") val rearTrunkOpen: Int? = null,
    @SerializedName("sun_roof_state") val sunRoofState: String? = null,
    @SerializedName("valet_mode") val valetMode: Boolean? = null,
    @SerializedName("vehicle_name") val vehicleName: String? = null,
    @SerializedName("sentry_mode") val sentryMode: Boolean? = null
)

data class ChargeState(
    @SerializedName("charging_state") val chargingState: String? = null,
    @SerializedName("battery_level") val batteryLevel: Int? = null,
    @SerializedName("battery_range") val batteryRange: Double? = null,
    @SerializedName("charger_power") val chargerPower: Double? = null,
    @SerializedName("charge_port_door_open") val chargePortDoorOpen: Boolean? = null
)

data class DriveState(
    val speed: Double? = null,
    val power: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerializedName("shift_state") val shiftState: String? = null
)

data class ClimateState(
    @SerializedName("inside_temp") val insideTemp: Double? = null,
    @SerializedName("outside_temp") val outsideTemp: Double? = null,
    @SerializedName("is_climate_on") val isClimateOn: Boolean? = null
)

data class VehicleSnapshot(
    val vehicleId: Long? = null,
    val vin: String? = null,
    val displayName: String? = null,
    val batteryLevel: Int? = null,
    val batteryRange: Double? = null,
    val chargerPower: Double? = null,
    val sentryMode: Boolean = false,
    val locked: Boolean = false,
    val doorOpen: Boolean = false,
    val trunkOpen: Boolean = false,
    val speed: Double? = null,
    val odometer: Double? = null,
    val outsideTemp: Double? = null,
    val chargingState: String? = null,
    val shiftState: String? = null,
    val updatedAt: Long = 0L
)

fun VehicleState.isSentryActive(): Boolean = sentryMode == true
fun VehicleState.isLocked(): Boolean = locked == true
fun VehicleState.isAnyDoorOpen(): Boolean {
    return driverFrontDoorOpen == 1 ||
        driverRearDoorOpen == 1 ||
        passengerFrontDoorOpen == 1 ||
        passengerRearDoorOpen == 1
}
fun VehicleState.isAnyTrunkOpen(): Boolean {
    return frontTrunkOpen == 1 || rearTrunkOpen == 1
}
