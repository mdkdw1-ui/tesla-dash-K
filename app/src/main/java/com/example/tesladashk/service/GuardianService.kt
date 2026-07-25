package com.example.tesladashk.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.tesladashk.network.NtfyRequest
import com.example.tesladashk.network.TeslaApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GuardianService : Service() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val topic = intent?.getStringExtra("ntfyTopic") ?: ""
        if (topic.isNotBlank()) {
            sendAlert(topic, "🛡️ 감시모드 가디언", "가디언 서비스가 활성화되었습니다.")
        }
        return START_STICKY
    }

    private fun sendAlert(topic: String, title: String, message: String) {
        scope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://ntfy.sh/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(TeslaApi::class.java)
                api.sendNtfyNotification(NtfyRequest(topic = topic, title = title, message = message))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
