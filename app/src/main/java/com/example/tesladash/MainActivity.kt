package com.example.tesladash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.tesladash.data.SupabaseRepository
import com.example.tesladash.model.Trip
import com.example.tesladash.model.VehicleLog
import com.example.tesladash.ui.screen.TeslaDashboardScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val repository = SupabaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
            var vehicleLogs by remember { mutableStateOf<List<VehicleLog>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }

            val coroutineScope = rememberCoroutineScope()

            // 앱 실행 시 Supabase/Vercel 데이터 비동기 조회
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    try {
                        trips = repository.fetchTrips()
                        vehicleLogs = repository.fetchVehicleLogs()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        isLoading = false
                    }
                }
            }

            TeslaDashboardScreen(
                trips = trips,
                vehicleLogs = vehicleLogs,
                isLoading = isLoading
            )
        }
    }
}
