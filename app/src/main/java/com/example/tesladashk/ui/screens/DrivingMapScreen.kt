package com.example.tesladashk.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tesladashk.ui.theme.*
import com.example.tesladashk.viewmodel.TeslaViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DrivingMapScreen(viewModel: TeslaViewModel) {
    val config by viewModel.config.collectAsState()
    val trips by viewModel.trips.collectAsState()

    val kakaoKey = config.kakaoKey

    if (kakaoKey.isBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg),
            contentAlignment = Alignment.Center
        ) {
            Text("⚙️ 상단 설정 버튼을 눌러 Kakao JavaScript Key를 등록해 주세요.", color = TextGray, fontSize = 13.sp)
        }
        return
    }

    val htmlContent = remember(kakaoKey, trips) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no"/>
            <style>
                html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background-color: #0f172a; }
            </style>
            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=$kakaoKey&autoload=false&libraries=services"></script>
        </head>
        <body>
            <div id="map"></div>
            <script>
                kakao.maps.load(function() {
                    var container = document.getElementById('map');
                    var options = {
                        center: new kakao.maps.LatLng(37.5665, 126.9780),
                        level: 7
                    };
                    var map = new kakao.maps.Map(container, options);
                    
                    var zoomControl = new kakao.maps.ZoomControl();
                    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            allowFileAccess = true
                            databaseEnabled = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        }
                        // 카카오 콘솔에 등록한 도메인과 100% 일치시켜 인증 통과
                        loadDataWithBaseURL(
                            "https://mdkdw1-ui.github.io",
                            htmlContent,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                update = { webView ->
                    webView.loadDataWithBaseURL(
                        "https://mdkdw1-ui.github.io",
                        htmlContent,
                        "text/html",
                        "UTF-8",
                        null
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
