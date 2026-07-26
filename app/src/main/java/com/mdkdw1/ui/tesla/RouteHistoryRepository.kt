package com.mdkdw1.ui.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class RouteHistory(
    val id: String,
    val timestamp: Long,
    val data: String
)

class RouteHistoryRepository {
    suspend fun getHistory(): List<RouteHistory> = withContext(Dispatchers.IO) {
        val baseUrl = SupabaseProvider.url
        val apiKey = SupabaseProvider.key
        if (baseUrl.isBlank() || apiKey.isBlank()) return@withContext emptyList()

        val endpoint = "$baseUrl/rest/v1/route_history?select=*&order=timestamp.desc"
        val list = mutableListOf<RouteHistory>()
        try {
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("apikey", apiKey)
            conn.setRequestProperty("Authorization", "Bearer $apiKey")

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(response)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    list.add(
                        RouteHistory(
                            id = obj.optString("id", ""),
                            timestamp = obj.optLong("timestamp", 0L),
                            data = obj.optString("data", "")
                        )
                    )
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        list
    }
}
