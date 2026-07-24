package com.example.tesladashk.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.tesladashk.network.ApiClient
import com.example.tesladashk.network.SentryRequest
import com.example.tesladashk.network.VehicleRequest
import kotlinx.coroutines.*

class GuardianService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var isMonitoring = false
    private val ntfyTopic = "MJYAz6ZyjXiujaTDpJ"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isMonitoring) {
            startForegroundService()
            isMonitoring = true
            startMonitoringLoop()
        }
        return START_STICKY
    }

    private fun startForegroundService() {
        val channelId = "guardian_channel"
        val channelName = "Guardian Service"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("가디언 모드 작동 중")
            .setContentText("차량 상태를 실시간으로 감시하고 있습니다.")
            .setSmallIcon(android.R.drawable.ic_secure)
            .build()

        startForeground(1, notification)
    }

    private fun startMonitoringLoop() {
        serviceScope.launch {
            while (isMonitoring) {
                try {
                    val response = ApiClient.teslaApi.checkSentryStatus(
                        SentryRequest("YOUR_TOKEN", "YOUR_VEHICLE_ID")
                    )
                    
                    if (response.isSuccessful && response.body()?.success == true) {
                        val data = response.body()!!
                        if (data.sentry_mode_type == "Panic" || data.sentry_mode_type == "Alarm") {
                            ApiClient.ntfyApi.sendNotification(
                                topic = ntfyTopic,
                                title = "‼️ 긴급 충격 경보",
                                priority = "urgent",
                                tags = "car,warning",
                                message = "Model Y Juniper: 차량 충격 또는 경보 이벤트가 감지되었습니다!"
                            )
                            ApiClient.teslaApi.flashHeadlights(VehicleRequest("YOUR_TOKEN", "YOUR_VEHICLE_ID"))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(5000L) // 5초 대기
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isMonitoring = false
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
