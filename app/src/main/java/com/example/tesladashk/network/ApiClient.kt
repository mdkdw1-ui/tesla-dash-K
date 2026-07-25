package com.example.tesladashk.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

object ApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun triggerVercelSync(vercelUrl: String): ApiResponse {
        if (vercelUrl.isBlank()) return ApiResponse(false, 400, "Vercel URL 미설정")
        return withContext(Dispatchers.IO) {
            try {
                val formattedUrl = when {
                    !vercelUrl.startsWith("http://") && !vercelUrl.startsWith("https://") -> "https://$vercelUrl"
                    else -> vercelUrl
                }
                val targetUrl = if (!formattedUrl.contains("/api/sync")) {
                    "${formattedUrl.removeSuffix("/")}/api/sync"
                } else formattedUrl

                val request = Request.Builder()
                    .url(targetUrl)
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    ApiResponse(response.isSuccessful, response.code, response.body?.string() ?: "")
                }
            } catch (e: Exception) {
                ApiResponse(false, 500, e.localizedMessage ?: "네트워크 오류")
            }
        }
    }

    suspend fun executeSupabaseGet(url: String, apiKey: String): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url(url)
                    .addHeader("apikey", apiKey)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .get()
                    .build()

                client.newCall(request).execute().use { response ->
                    ApiResponse(response.isSuccessful, response.code, response.body?.string() ?: "")
                }
            } catch (e: Exception) {
                ApiResponse(false, 500, e.localizedMessage ?: "네트워크 오류")
            }
        }
    }
}
