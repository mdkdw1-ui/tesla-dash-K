package com.example.tesladashk

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "✅ 화면 진입 성공!\n\n앱이 정상 동작합니다."
            textSize = 22f
            setTextColor(Color.GREEN)
            setPadding(60, 120, 60, 60)
            setBackgroundColor(Color.BLACK)
        }

        setContentView(textView)
    }
}
