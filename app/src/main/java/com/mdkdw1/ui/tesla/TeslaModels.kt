package com.mdkdw1.ui.tesla

data class TeslaAuthToken(
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val tokenType: String? = null,
    val expiresIn: Long? = null
)

data class TeslaVehicleListResponse(
    val response: List<TeslaVehicle>? = null,
    val count: Int? = null
)

data class TeslaVehicle(
    val id: Long? = null,
    val vehicleId: Long? = null,
    val vin: String? = null,
    val displayName: String? = null,
    val state: String? = null,
    val inService: Boolean? = null
)

data class TeslaVehicleDataResponse(
    val response: TeslaVehicleData? = null
)

data class TeslaVehicleData(
    val id: Long? = null,
    val vehicleId: Long? = null,
    val vin: String? = null,
    val displayName: String? = null,
    val vehicleState: VehicleState? = null,
    val chargeState: ChargeState? = null,
    val driveState: DriveState? = null,
    val climateState: ClimateState? = null
)

data class VehicleState(
    val locked: Boolean? = null,
    val odometer: Double? = null,
    val driverFrontDoorOpen: Int? = null,
    val driverRearDoorOpen: Int? = null,
    val passengerFrontDoorOpen: Int? = null,
    val passengerRearDoorOpen: Int? = null,
    val frontTrunkOpen: Int? = null,
    val rearTrunkOpen: Int? = null,
    val sunRoofState: String? = null,
    val valetMode: Boolean? = null,
    val vehicleName: String? = null,
    val sentryMode: Boolean? = null
)

data class ChargeState(
    val chargingState: String? = null,
    val batteryLevel: Int? = null,
    val batteryRange: Double? = null,
    val chargerPower: Double? = null,
    val chargePortDoorOpen: Boolean? = null
)

data class DriveState(
    val speed: Double? = null,
    val power: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val shiftState: String? = null
)

data class ClimateState(
    val insideTemp: Double? = null,
    val outsideTemp: Double? = null,
    val isClimateOn: Boolean? = null
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
fun VehicleState.isAnyTrunkOpen(): Boolean = frontTrunkOpen == 1 || rearTrunkOpen == 1
