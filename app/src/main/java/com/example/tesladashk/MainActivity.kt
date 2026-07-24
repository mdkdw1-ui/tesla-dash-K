package com.example.tesladashk

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 아주 기초적인 순수 안드로이드 텍스트 뷰 (Compose/Service 미사용)
        val textView = TextView(this).apply {
            text = "✅ 앱이 정상적으로 실행되었습니다!\n\n화면 전환 성공"
            textSize = 20f
            setTextColor(Color.GREEN)
            setPadding(50, 100, 50, 50)
        }
        
        setContentView(textView)
    }
}
