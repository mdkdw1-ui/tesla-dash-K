package com.example.tesladashk.ui.screens

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun DrivingMapScreen(viewModel: DashboardViewModel) {
    val config by viewModel.config.collectAsState()
    val kakaoKey = config.kakaoKey.ifBlank { "159c5d7588efc5939d431f005912f9f3" }

    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background-color: #0D0E12; }
            </style>
            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=${kakaoKey}"></script>
        </head>
        <body>
            <div id="map"></div>
            <script>
                window.onload = function() {
                    if (typeof kakao !== 'undefined' && kakao.maps) {
                        var container = document.getElementById('map');
                        var options = {
                            center: new kakao.maps.LatLng(37.5665, 126.9780),
                            level: 4
                        };
                        var map = new kakao.maps.Map(container, options);
                    } else {
                        document.body.innerHTML = "<div style='color:white;padding:20px;text-align:center;'>카카오 지도 SDK 로드 실패. API Key를 확인해주세요.</div>";
                    }
                };
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                loadDataWithBaseURL("https://dapi.kakao.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://dapi.kakao.com", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = Modifier.fillMaxSize()
    )
}
