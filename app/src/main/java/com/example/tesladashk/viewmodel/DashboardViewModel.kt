package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel : ViewModel() {

    private val _config = MutableStateFlow(ConfigState())
    val config: StateFlow<ConfigState> = _config.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _trips = MutableStateFlow<List<TripItem>>(emptyList())
    val trips: StateFlow<List<TripItem>> = _trips.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    init {
        addLog("[시스템] 앱이 성공적으로 시작되었습니다.")
    }

    fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logs.value = (listOf("[$time] $msg") + _logs.value).take(100)
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun saveConfig(
        kakaoKey: String,
        supabaseUrl: String,
        supabaseKey: String,
        ghToken: String,
        vehicleId: String,
        ntfyTopic: String
    ) {
        _config.value = ConfigState(
            kakaoKey = kakaoKey.trim(),
            supabaseUrl = supabaseUrl.trim().removeSuffix("/"),
            supabaseKey = supabaseKey.trim(),
            ghToken = ghToken.trim(),
            vehicleId = vehicleId.trim(),
            ntfyTopic = ntfyTopic.trim()
        )
        addLog("[설정] API 및 DB 설정 정보가 저장되었습니다.")
        syncWithSupabase()
    }

    fun syncWithSupabase() {
        viewModelScope.launch {
            val cfg = _config.value
            if (cfg.supabaseUrl.isBlank() || cfg.supabaseKey.isBlank()) {
                addLog("[동기화 실패] Supabase URL 또는 Key가 설정되지 않았습니다.")
                return@launch
            }

            addLog("[동기화] Supabase DB 데이터 동기화 시도...")
            
            val targetUrl = if (cfg.supabaseUrl.endsWith("/rest/v1")) {
                "${cfg.supabaseUrl}/vehicle_state?select=*"
            } else if (cfg.supabaseUrl.contains("/rest/v1")) {
                cfg.supabaseUrl
            } else {
                "${cfg.supabaseUrl}/rest/v1/vehicle_state?select=*"
            }

            val response = ApiClient.executeSupabaseGet(targetUrl, cfg.supabaseKey)

            if (response.isSuccess) {
                try {
                    val jsonArray = JSONArray(response.body)
                    if (jsonArray.length() > 0) {
                        val obj = jsonArray.getJSONObject(0)
                        val status = obj.optString("status_text", "현재: 주차 중")
                        val battery = obj.optInt("battery_level", 85)
                        val odo = obj.optInt("odometer", 45210)
                        val temp = obj.optDouble("outside_temp", 24.0).toFloat()
                        val parkDuration = obj.optString("park_duration_str", "주차 중")

                        _vehicleState.value = VehicleState(
                            statusText = status,
                            batteryLevel = battery,
                            odometer = odo,
                            outsideTemp = temp,
                            parkDurationStr = parkDuration
                        )
                        addLog("[동기화 성공] Supabase 최신 데이터 수신 완료")
                    } else {
                        addLog("[동기화] DB 테이블에 데이터가 비어 있습니다 (빈 배열).")
                    }
                } catch (e: Exception) {
                    addLog("[동기화 수신] Raw JSON 응답 받음 (파싱 규격 대기 중)")
                }
            } else {
                addLog("[동기화 실패] Code ${response.statusCode}: ${response.errorMessage}")
            }
        }
    }
}
