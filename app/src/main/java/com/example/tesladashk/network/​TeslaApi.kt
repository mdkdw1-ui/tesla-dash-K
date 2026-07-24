package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TeslaApi {
    @POST("api/sentry")
    suspend fun checkSentryStatus(@Body request: SentryRequest): Response<SentryResponse>

    @POST("api/headlights")
    suspend fun flashHeadlights(@Body request: VehicleRequest): Response<ActionResponse>
}

data class SentryRequest(val token: String, val vehicleId: String)
data class VehicleRequest(val token: String, val vehicleId: String)
data class SentryResponse(val success: Boolean, val sentry_mode: Boolean, val sentry_mode_type: String?)
data class ActionResponse(val success: Boolean)
