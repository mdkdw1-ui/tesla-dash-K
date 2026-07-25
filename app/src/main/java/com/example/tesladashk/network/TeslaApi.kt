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

interface GitHubApi {
    @POST("repos/mdkdw1-ui/my-tesla-app/actions/workflows/sync.yml/dispatches")
    suspend fun triggerSync(
        @Header("Authorization") token: String,
        @Body body: Map<String, String> = mapOf("ref" to "main")
    ): Response<Unit>
}
