package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.*

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
