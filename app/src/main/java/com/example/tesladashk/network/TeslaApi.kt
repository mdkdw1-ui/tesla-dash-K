package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.*

data class NtfyRequest(
    val topic: String,
    val title: String,
    val message: String
)

interface TeslaApi {
    @POST("send")
    suspend fun sendNtfyNotification(@Body request: NtfyRequest): Response<Unit>
}
