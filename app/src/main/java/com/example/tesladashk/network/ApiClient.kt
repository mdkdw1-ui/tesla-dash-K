package com.example.tesladashk.network

import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiClient {
    suspend fun executeSupabaseGet(urlStr: String, apiKey: String): ApiResponse = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlStr)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", apiKey)
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: ""
            ApiResponse(code in 200..299, code, body)
        } catch (e: Exception) {
            ApiResponse(false, 500, "", e.localizedMessage)
        }
    }

    suspend fun triggerGitHubSync(ghToken: String): ApiResponse = withContext(Dispatchers.IO) {
        if (ghToken.isBlank()) return@withContext ApiResponse(false, 400, "", "GitHub 토큰 미설정")
        try {
            val url = URL("https://api.github.com/repos/mdkdw1-ui/tesla-dash-K/dispatches")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Authorization", "Bearer $ghToken")
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            val payload = "{\"event_type\": \"sync_trigger\"}"
            conn.outputStream.use { it.write(payload.toByteArray()) }

            val code = conn.responseCode
            ApiResponse(code in 200..299, code, "GitHub Sync Trigger Success")
        } catch (e: Exception) {
            ApiResponse(false, 500, "", e.localizedMessage)
        }
    }
}
