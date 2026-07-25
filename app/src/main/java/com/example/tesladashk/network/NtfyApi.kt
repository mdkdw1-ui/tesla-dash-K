package com.example.tesladashk.network

import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object NtfyApi {
    fun sendNotification(topic: String, title: String, message: String) {
        if (topic.isBlank()) return
        try {
            val url = URL("https://ntfy.sh/$topic")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Title", title)
            conn.doOutput = true
            conn.connectTimeout = 5000

            OutputStreamWriter(conn.outputStream).use { it.write(message) }
            conn.responseCode
        } catch (_: Exception) {}
    }
}
