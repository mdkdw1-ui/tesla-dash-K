package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class VehicleResponse(val response: List<VehicleData>)
data class VehicleData(val id_str: String, val display_name: String, val state: String)

data class VehicleStateData(val response: VehicleDataState)
data class VehicleDataState(
    val charge_state: ChargeState,
    val vehicle_state: VehicleStateDetails
)
data class ChargeState(val battery_level: Int, val battery_range: Double)
data class VehicleStateDetails(val sentry_mode: Boolean, val locked: Boolean)

interface TeslaApi {
    @GET("api/1/vehicles")
    suspend fun getVehicles(@Header("Authorization") token: String): Response<VehicleResponse>

    @GET("api/1/vehicles/{id}/vehicle_data")
    suspend fun getVehicleData(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<VehicleStateData>

    @POST("api/1/vehicles/{id}/command/honk_horn")
    suspend fun honkHorn(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}
