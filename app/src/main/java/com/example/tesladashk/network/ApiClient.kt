package com.example.tesladashk.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val TESLA_BASE_URL = "https://your-vercel-backend-url.com/"
    private const val NTFY_BASE_URL = "https://ntfy.sh/"

    val teslaApi: TeslaApi by lazy {
        Retrofit.Builder()
            .baseUrl(TESLA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TeslaApi::class.java)
    }

    val ntfyApi: NtfyApi by lazy {
        Retrofit.Builder()
            .baseUrl(NTFY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NtfyApi::class.java)
    }
}
