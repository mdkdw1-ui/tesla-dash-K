package com.example.tesladashk.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
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

        // Android 14 (API 34) 포그라운드 서비스 타입 명시
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1001,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1001, notification)
        }

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
                    // 백그라운드 상태 체크 및 ntfy 알림 로직
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(60000)
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
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
