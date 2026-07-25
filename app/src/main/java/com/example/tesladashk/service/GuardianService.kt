package com.example.tesladashk.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.tesladashk.network.TeslaVercelApi
import com.example.tesladashk.network.NtfyApi
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GuardianService : Service() {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val token = intent?.getStringExtra("ACCESS_TOKEN") ?: ""
        val vehicleId = intent?.getStringExtra("VEHICLE_ID") ?: ""
        val intervalSec = intent?.getIntExtra("INTERVAL", 5) ?: 5
        val ntfyTopic = intent?.getStringExtra("NTFY_TOPIC") ?: "MJYAz6ZyjXiujaTDpJ"

        startForegroundServiceNotification()
        startGuardianMonitoring(token, vehicleId, intervalSec, ntfyTopic)

        return START_STICKY
    }

    private fun startForegroundServiceNotification() {
        val channelId = "TeslaGuardianChannel"
        val channelName = "Tesla Command Guardian"
        val manager = getSystemService(NotificationManager::class.java)
        if (manager?.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager?.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("🛡️ 테슬라 감시 가디언 가동 중")
            .setContentText("실시간 차량 침입 및 충격을 백그라운드에서 감시 중입니다.")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1001, notification)
    }

    private fun startGuardianMonitoring(token: String, vehicleId: String, intervalSec: Int, topic: String) {
        job?.cancel()
        job = serviceScope.launch {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://my-tesla-app-six.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val ntfyRetrofit = Retrofit.Builder()
                .baseUrl("https://ntfy.sh/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val vercelApi = retrofit.create(TeslaVercelApi::class.java)
            val ntfyApi = ntfyRetrofit.create(NtfyApi::class.java)

            while (isActive) {
                try {
                    val response = vercelApi.checkSentry(mapOf("token" to token, "vehicleId" to vehicleId))
                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        
                        if (data.sentryMode == false) {
                            stopSelf()
                            break
                        }

                        val isLocked = data.locked ?: true
                        val isDoorOpen = data.doorsOpen?.let { it.df == true || it.dr == true || it.pf == true || it.pr == true } ?: false
                        val isTrunkOpen = data.trunksOpen?.let { it.ft == true || it.rt == true } ?: false

                        if (isLocked && (isDoorOpen || isTrunkOpen)) {
                            val target = if (isDoorOpen && isTrunkOpen) "문/트렁크" else if (isDoorOpen) "도어" else "트렁크"
                            ntfyApi.sendNotification(
                                topic = topic,
                                title = "🚨 테슬라 $target 무단 열림",
                                priority = "high",
                                message = "차량이 잠긴 상태에서 $target가 열렸습니다!"
                            )
                        }

                        if (data.sentryModeType == "Panic" || data.sentryModeType == "Alarm") {
                            vercelApi.flashHeadlights(mapOf("token" to token, "vehicleId" to vehicleId))
                            ntfyApi.sendNotification(
                                topic = topic,
                                title = "‼️ 테슬라 긴급 충격 경보",
                                priority = "urgent",
                                message = "차량 충격 또는 경보 이벤트가 감지되었습니다!"
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(intervalSec * 1000L)
            }
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
