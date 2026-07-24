package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface NtfyApi {
    @POST("{topic}")
    suspend fun sendNotification(
        @Path("topic") topic: String,
        @Body message: String
    ): Response<Unit>
}
