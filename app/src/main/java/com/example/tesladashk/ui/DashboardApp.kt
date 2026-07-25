package com.example.tesladashk.ui

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

// 기존 viewmodel 패키지의 모델 클래스 import
import com.example.tesladashk.viewmodel.TeslaViewModel
import com.example.tesladashk.viewmodel.StatusHistoryItem
import com.example.tesladashk.viewmodel.VehicleStatusType

// 주소 정제 함수 (서울특별시, 경기도, 고양시 제거)
fun formatAddress(address: String?): String {
    if (address.isNullOrBlank()) return ""
    return address
        .replace("서울특별시 ", "")
        .replace("서울특별시", "")
        .replace("경기도 ", "")
        .replace("경기도", "")
        .replace("고양시 ", "")
        .replace("고양시", "")
        .trim()
}

@Composable
fun DashboardApp(viewModel: TeslaViewModel = viewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        KakaoMapView(
            latitude = 37.6581,
            longitude = 126.8320,
            pathPointsJson = "[{\"lat\": 37.6581, \"lng\": 126.8320}, {\"lat\": 37.6600, \"lng\": 126.8350}]",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        CompactHistoryList(
            historyItems = viewModel.historyList,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun KakaoMapView(
    appKey: String = "YOUR_KAKAO_APP_KEY",
    latitude: Double,
    longitude: Double,
    pathPointsJson: String = "[]",
    modifier: Modifier = Modifier
) {
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script type="text/javascript" src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=$appKey"></script>
            <style>
                html, body, #map { width: 100%; height: 100%; margin: 0; padding: 0; background-color: #121212; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var container = document.getElementById('map');
                var options = {
                    center: new kakao.maps.LatLng($latitude, $longitude),
                    level: 4
                };
                var mapInstance = new kakao.maps.Map(container, options);

                var markerPosition = new kakao.maps.LatLng($latitude, $longitude);
                var marker = new kakao.maps.Marker({ position: markerPosition });
                marker.setMap(mapInstance);

                var rawPath = $pathPointsJson;
                if (rawPath && rawPath.length > 0) {
                    var linePath = rawPath.map(function(pt) {
                        return new kakao.maps.LatLng(pt.lat, pt.lng);
                    });
                    var polyline = new kakao.maps.Polyline({
                        path: linePath,
                        strokeWeight: 5,
                        strokeColor: '#FF0055',
                        strokeOpacity: 0.8,
                        strokeStyle: 'solid'
                    });
                    polyline.setMap(mapInstance);
                }
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                loadDataWithBaseURL("https://dapi.kakao.com", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://dapi.kakao.com", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

@Composable
fun CompactHistoryList(
    historyItems: List<StatusHistoryItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(historyItems) { item ->
            CompactHistoryRow(item)
        }
    }
}

@Composable
fun CompactHistoryRow(item: StatusHistoryItem) {
    val badgeColor = when (item.type) {
        VehicleStatusType.CHARGING -> Color(0xFF00E676)
        VehicleStatusType.DRIVING -> Color(0xFF29B6F6)
        VehicleStatusType.PARKED -> Color(0xFFFFB74D)
        else -> Color.Gray
    }

    val badgeText = when (item.type) {
        VehicleStatusType.CHARGING -> "충전"
        VehicleStatusType.DRIVING -> "주행"
        VehicleStatusType.PARKED -> "주차"
        else -> "대기"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.timestamp,
            color = Color.LightGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(42.dp)
        )

        Box(
            modifier = Modifier
                .background(badgeColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(
                text = badgeText,
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = item.address,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "${item.batteryLevel}% ${item.detailText}".trim(),
            color = Color(0xFFFFD54F),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
