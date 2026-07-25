package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.*

interface TeslaVercelApi {
    @POST("api/exchange")
    suspend fun exchangeToken(@Body body: Map<String, String>): Response<Map<String, Any>>

    @POST("api/sentry")
    suspend fun checkSentry(@Body body: Map<String, String>): Response<SentryStatusResponse>

    @POST("api/headlights")
    suspend fun flashHeadlights(@Body body: Map<String, String>): Response<Map<String, Any>>
}

interface NtfyApi {
    @POST("{topic}")
    suspend fun sendNotification(
        @Path("topic") topic: String,
        @Header("Title") title: String,
        @Header("Priority") priority: String,
        @Header("Tags") tags: String = "car,warning",
        @Body message: String
    ): Response<Unit>
}

interface VercelSyncApi {
    @POST("api/sync")
    suspend fun triggerSync(
        @Body body: Map<String, String> = emptyMap()
    ): Response<Map<String, Any>>
}

interface SupabaseApi {
    @GET("rest/v1/vehicle_states")
    suspend fun getVehicleStates(
        @Header("apikey") apiKey: String,
        @Header("Authorization") bearerToken: String,
        @Query("select") select: String = "*",
        @Query("order") order: String = "updated_at.desc",
        @Query("limit") limit: Int = 50
    ): Response<List<VehicleRow>>
}
