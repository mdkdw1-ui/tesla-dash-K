package com.example.tesladashk.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface NtfyApi {
    @POST("{topic}")
    suspend fun sendNotification(
        @Path("topic") topic: String,
        @Header("Title") title: String,
        @Header("Priority") priority: String,
        @Header("Tags") tags: String,
        @Body message: String
    ): Response<Unit>
}
