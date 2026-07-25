package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.*

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
