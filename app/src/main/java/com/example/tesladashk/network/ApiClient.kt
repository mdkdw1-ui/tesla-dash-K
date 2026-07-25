package com.example.tesladashk.network

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiClient {
    suspend fun executeSupabaseGet(targetUrl: String, apiKey: String): ApiResponse = withContext(Dispatchers.IO) {
        try {
            val url = URL(targetUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", apiKey)
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

            val code = conn.responseCode
            if (code in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val body = reader.readText()
                reader.close()
                ApiResponse(true, code, body, null)
            } else {
                val reader = BufferedReader(InputStreamReader(conn.errorStream ?: conn.inputStream))
                val err = reader.readText()
                reader.close()
                ApiResponse(false, code, "", err)
            }
        } catch (e: Exception) {
            ApiResponse(false, -1, "", e.message ?: "Network error")
        }
    }
}
