package com.example.tesladashk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class DashboardViewModel : ViewModel() {

    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config

    private val _vehicleRows = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleRows: StateFlow<List<VehicleRow>> = _vehicleRows

    private val _trips = MutableStateFlow<List<DrivingTrip>>(emptyList())
    val trips: StateFlow<List<DrivingTrip>> = _trips

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _lastSyncTime = MutableStateFlow("")
    val lastSyncTime: StateFlow<String> = _lastSyncTime

    fun loadInitialConfig(context: Context) {
        val loaded = ConfigManager.loadConfig(context)
        _config.value = loaded
        fetchSupabaseData()
    }

    fun saveConfig(context: Context, newConfig: AppConfig) {
        _config.value = newConfig
        ConfigManager.saveConfig(context, newConfig)
        fetchSupabaseData()
    }

    fun triggerSyncAndFetch() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://my-tesla-app-git-main-glenn-team.vercel.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(VercelSyncApi::class.java)
                api.triggerSync()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fetchSupabaseData()
                _isLoading.value = false
            }
        }
    }

    fun fetchSupabaseData() {
        val cfg = _config.value
        if (cfg.supabaseUrl.isBlank() || cfg.supabaseKey.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val baseUrl = if (cfg.supabaseUrl.endsWith("/")) cfg.supabaseUrl else "${cfg.supabaseUrl}/"
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(SupabaseApi::class.java)
                val resp = api.getVehicleStates(
                    apiKey = cfg.supabaseKey,
                    bearerToken = "Bearer ${cfg.supabaseKey}"
                )
                if (resp.isSuccessful && resp.body() != null) {
                    val data = resp.body()!!
                    _vehicleRows.value = data
                    _lastSyncTime.value = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                    processTimelineTrips(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun processTimelineTrips(rows: List<VehicleRow>) {
        if (rows.isEmpty()) return
        val list = mutableListOf<DrivingTrip>()
        val sdfInput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdfDate = SimpleDateFormat("M월 d일", Locale.getDefault())

        for (i in rows.indices) {
            val curr = rows[i]
            val prev = if (i < rows.size - 1) rows[i + 1] else curr

            val dateObj = try { sdfInput.parse(curr.updatedAt ?: "") ?: Date() } catch (e: Exception) { Date() }
            val timeStr = sdfTime.format(dateObj)
            val dateStr = sdfDate.format(dateObj)

            val stateType = when {
                curr.state == "driving" || (prev.odometer != null && curr.odometer != null && curr.odometer > prev.odometer) -> "주행"
                curr.sentryMode == true -> "감시"
                else -> "온라인"
            }

            val batDiff = (prev.batteryLevel ?: 0) - (curr.batteryLevel ?: 0)
            val distDiff = (curr.odometer ?: 0.0) - (prev.odometer ?: 0.0)

            list.add(
                DrivingTrip(
                    id = curr.id ?: UUID.randomUUID().toString(),
                    stateType = stateType,
                    startTime = timeStr,
                    endTime = timeStr,
                    durationText = "56분",
                    moveKM = if (distDiff > 0) String.format(Locale.getDefault(), "%.1f", distDiff).toDouble() else 0.0,
                    batteryUsedPercent = if (batDiff > 0) batDiff.toDouble() else 0.0,
                    startBattery = curr.batteryLevel ?: 0,
                    endBattery = curr.batteryLevel ?: 0,
                    endOdometer = curr.odometer ?: 0.0,
                    dateGroup = dateStr
                )
            )
        }
        _trips.value = list
    }

    fun toPsi(v: Double?): String {
        if (v == null || v <= 0) return "0.0"
        val psi = if (v < 10) v * 14.5038 else v
        return String.format(Locale.getDefault(), "%.1f", psi)
    }
}
