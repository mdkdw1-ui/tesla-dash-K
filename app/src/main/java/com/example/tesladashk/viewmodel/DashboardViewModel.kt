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
import kotlin.math.roundToInt

class DashboardViewModel : ViewModel() {

    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config

    private val _vehicleRows = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleRows: StateFlow<List<VehicleRow>> = _vehicleRows

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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

    fun triggerVercelSync() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://my-tesla-app-git-main-glenn-team.vercel.app/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val api = retrofit.create(VercelSyncApi::class.java)
                val resp = api.triggerSync()
                if (resp.isSuccessful) {
                    fetchSupabaseData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
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
                    _vehicleRows.value = resp.body()!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toPsi(v: Double?): String {
        if (v == null || v <= 0) return "0.0"
        val psi = if (v < 10) v * 14.5038 else v
        return String.format(Locale.getDefault(), "%.1f", psi)
    }
}
