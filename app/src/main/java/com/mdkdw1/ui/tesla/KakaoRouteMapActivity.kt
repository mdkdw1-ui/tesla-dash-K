package com.mdkdw1.ui.tesla

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.shape.RouteLineOptions
import com.kakao.vectormap.shape.RouteLineSegment
import com.kakao.vectormap.shape.RouteLineStyle
import com.kakao.vectormap.shape.RouteLineStyles
import com.mdkdw1.ui.tesla.databinding.ActivityKakaoRouteMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KakaoRouteMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKakaoRouteMapBinding
    private lateinit var repository: RouteHistoryRepository
    private var kakaoMap: KakaoMap? = null
    private var currentPeriod: RoutePeriod = RoutePeriod.DAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoRouteMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = RouteHistoryRepository(SupabaseProvider.client)
        currentPeriod = intent.getStringExtra(EXTRA_PERIOD)?.toRoutePeriod() ?: RoutePeriod.DAY

        binding.mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(error: Exception?) {
                    toast(error?.message ?: "map error")
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(map: KakaoMap) {
                    kakaoMap = map
                    loadAndDrawRoutes(currentPeriod)
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        binding.mapView.pause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.mapView.finish()
        super.onDestroy()
    }

    private fun loadAndDrawRoutes(period: RoutePeriod) {
        lifecycleScope.launch {
            val range = repository.buildRange(period)
            val routes = withContext(Dispatchers.IO) {
                repository.fetchRoutes(range)
            }
            drawRoutes(routes, period)
        }
    }

    private fun drawRoutes(routes: List<DrivingRow>, period: RoutePeriod) {
        val map = kakaoMap ?: return
        val points = routes.flatMap { it.location_list }
            .filter { it.latitude != 0.0 && it.longitude != 0.0 }

        if (points.size < 2) {
            toast("경로가 부족합니다")
            return
        }

        val latLngs = points.map { LatLng.from(it.latitude, it.longitude) }
        val styles = RouteLineStyles.from(
            RouteLineStyle.from(6f, 0xFFEAEAEA.toInt())
        )
        val segment = RouteLineSegment.from(latLngs).setStyles(styles)
        val options = RouteLineOptions.from(segment)

        map.routeLineManager?.layer?.addRouteLine(options)?.show()
        toast("${label(period)} 경로 ${routes.size}건")
    }

    private fun label(period: RoutePeriod): String {
        return when (period) {
            RoutePeriod.DAY -> "일"
            RoutePeriod.WEEK -> "주"
            RoutePeriod.MONTH -> "월"
            RoutePeriod.QUARTER -> "분기"
            RoutePeriod.HALF_YEAR -> "반기"
            RoutePeriod.YEAR -> "년"
            RoutePeriod.CUSTOM -> "직접"
        }
    }

    private fun String.toRoutePeriod(): RoutePeriod {
        return when (uppercase()) {
            "DAY" -> RoutePeriod.DAY
            "WEEK" -> RoutePeriod.WEEK
            "MONTH" -> RoutePeriod.MONTH
            "QUARTER" -> RoutePeriod.QUARTER
            "HALF_YEAR" -> RoutePeriod.HALF_YEAR
            "YEAR" -> RoutePeriod.YEAR
            else -> RoutePeriod.DAY
        }
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_PERIOD = "extra_period"
    }
}
