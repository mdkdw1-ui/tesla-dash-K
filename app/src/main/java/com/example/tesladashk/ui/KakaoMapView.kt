package com.example.tesladashk.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun KakaoMapView(
    kakaoAppKey: String,
    locationListJson: String,
    modifier: Modifier = Modifier
) {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                html, body { width: 100%; height: 100%; margin: 0; padding: 0; }
                #map { width: 100%; height: 100%; }
            </style>
            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=$kakaoAppKey&autoload=false"></script>
        </head>
        <body>
            <div id="map"></div>
            <script>
                document.addEventListener("DOMContentLoaded", function() {
                    if (typeof kakao === 'undefined' || !kakao.maps) {
                        document.getElementById('map').innerHTML = '<div style="color:white;padding:20px;text-align:center;">카카오맵 API Key를 확인해주세요.</div>';
                        return;
                    }
                    
                    kakao.maps.load(function() {
                        var mapContainer = document.getElementById('map');
                        var rawLocations = $locationListJson;
                        var linePath = [];

                        if (Array.isArray(rawLocations) && rawLocations.length > 0) {
                            for (var i = 0; i < rawLocations.length; i++) {
                                var item = rawLocations[i];
                                var lat = item.latitude || item.lat;
                                var lng = item.longitude || item.lng || item.lon;
                                if (lat && lng) {
                                    linePath.push(new kakao.maps.LatLng(lat, lng));
                                }
                            }
                        }

                        var centerPos = linePath.length > 0 ? linePath[Math.floor(linePath.length / 2)] : new kakao.maps.LatLng(37.5665, 126.9780);
                        
                        var mapOptions = {
                            center: centerPos,
                            level: linePath.length > 0 ? 6 : 5
                        };

                        var map = new kakao.maps.Map(mapContainer, mapOptions);

                        if (linePath.length > 0) {
                            var polyline = new kakao.maps.Polyline({
                                path: linePath,
                                strokeWeight: 5,
                                strokeColor: '#3B82F6',
                                strokeOpacity: 0.9,
                                strokeStyle: 'solid'
                            });
                            polyline.setMap(map);

                            // 출발/도착 마커 표시
                            new kakao.maps.Marker({ position: linePath[0], map: map });
                            new kakao.maps.Marker({ position: linePath[linePath.length - 1], map: map });
                            
                            var bounds = new kakao.maps.LatLngBounds();
                            for (var j = 0; j < linePath.length; j++) {
                                bounds.extend(linePath[j]);
                            }
                            map.setBounds(bounds);
                        }
                    });
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://map.kakao.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://map.kakao.com", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}
