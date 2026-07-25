package com.mdkdw1.ui.tesla

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk

class TeslaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val key = PrefsStore(this).getKakaoNativeAppKey().trim()
        if (key.isNotBlank()) KakaoMapSdk.init(this, key)
    }
}
