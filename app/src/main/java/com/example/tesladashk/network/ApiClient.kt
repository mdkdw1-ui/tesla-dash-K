package com.example.tesladashk.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val TESLA_BASE_URL = "https://owner-api.teslamotors.com/"
    private const val NTFY_BASE_URL = "https://ntfy.sh/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val teslaApi: TeslaApi by lazy {
        Retrofit.Builder()
            .baseUrl(TESLA_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TeslaApi::class.java)
    }

    val ntfyApi: NtfyApi by lazy {
        Retrofit.Builder()
            .baseUrl(NTFY_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NtfyApi::class.java)
    }
}
