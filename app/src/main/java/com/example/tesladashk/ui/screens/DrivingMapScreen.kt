package com.example.tesladashk.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tesladashk.viewmodel.DashboardViewModel

@Composable
fun DrivingMapScreen(viewModel: DashboardViewModel) {
    val config by viewModel.config.collectAsState()

    val mapHtml = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=${config.kakaoKey}"></script>
            <style>
                html, body, #map { width:100%; height:100%; margin:0; padding:0; background-color:#0D0E12; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var container = document.getElementById('map');
                var options = {
                    center: new kakao.maps.LatLng(37.5665, 126.9780),
                    level: 5
                };
                var map = new kakao.maps.Map(container, options);
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadDataWithBaseURL("https://dapi.kakao.com", mapHtml, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
