package com.mdkdw1.ui.tesla

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.route.RouteLine
import com.kakao.vectormap.route.RouteLineLayer
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLineSegment
import com.mdkdw1.ui.tesla.databinding.ActivityKakaoRouteMapBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KakaoRouteMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKakaoRouteMapBinding
    private lateinit var repository: RouteHistoryRepository
    private var kakaoMap: KakaoMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoRouteMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = RouteHistoryRepository(SupabaseProvider.client)

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
                    loadAndDrawRoutes(RoutePeriod.DAY)
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
            drawRoutes(routes)
        }
    }

    private fun drawRoutes(routes: List<DrivingRow>) {
        val map = kakaoMap ?: return
        val points = routes.flatMap { it.location_list }
            .filter { it.latitude != 0.0 && it.longitude != 0.0 }

        if (points.size < 2) {
            toast("경로가 부족합니다")
            return
        }

        val latLngs = points.map { LatLng.from(it.latitude, it.longitude) }
        val segment = RouteLineSegment.from(latLngs)
        val options = RouteLineOptions.from(segment)
        val layer: RouteLineLayer? = map.routeLineManager?.layer
        val routeLine: RouteLine? = layer?.addRouteLine(options)

        routeLine?.show()
        toast("경로 ${routes.size}건")
    }

    private fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
