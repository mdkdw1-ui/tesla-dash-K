package com.mdkdw1.ui.tesla

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class TeslaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val appKey = runCatching { BuildConfig::class.java.getField("KAKAO_APP_KEY").get(null) as String }.getOrDefault("")
        if (appKey.isNotBlank()) {
            KakaoMapSdk.init(this, appKey)
        }
    }
}
