package com.example.tesladashk.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.tesladashk.network.ApiClient
import kotlinx.coroutines.*

class GuardianService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "GUARDIAN_CHANNEL")
            .setContentTitle("Tesla Guardian Active")
            .setContentText("Monitoring Sentry mode and battery levels...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1001, notification)

        if (!isRunning) {
            isRunning = true
            startMonitoring()
        }

        return START_STICKY
    }

    private fun startMonitoring() {
        serviceScope.launch {
            while (isRunning) {
                try {
                    // 예시: 백그라운드 상태 체크 및 ntfy 알림 로직
                    // ApiClient.ntfyApi.sendNotification("my-tesla-topic", "Guardian Check: Normal")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(60000) // 1분 주기로 감시
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "GUARDIAN_CHANNEL",
                "Tesla Guardian Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
