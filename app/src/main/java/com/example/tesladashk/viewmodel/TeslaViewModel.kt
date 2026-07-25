package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeslaViewModel : ViewModel() {
    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _tripsState = MutableStateFlow<List<TripItem>>(emptyList())
    val tripsState: StateFlow<List<TripItem>> = _tripsState.asStateFlow()

    private val _logsState = MutableStateFlow<List<String>>(
        listOf("[시스템] Supabase 연동 모듈 준비 완료")
    )
    val logsState: StateFlow<List<String>> = _logsState.asStateFlow()

    private val _configState = MutableStateFlow(ConfigState())
    val configState: StateFlow<ConfigState> = _configState.asStateFlow()

    private val _isGuardianActive = MutableStateFlow(false)
    val isGuardianActive: StateFlow<Boolean> = _isGuardianActive.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    val accessToken: String get() = TeslaApi.DEFAULT_ACCESS_TOKEN

    init {
        refreshData()
    }

    fun updateConfig(newConfig: ConfigState) {
        _configState.value = newConfig
        addLog("[설정] API 및 DB 설정 정보가 저장되었습니다")
        refreshData()
    }

    fun toggleGuardian(active: Boolean) {
        _isGuardianActive.value = active
        val statusStr = if (active) "가동 시작" else "중지됨"
        addLog("🛡️ [가디언] 감시 모드가 $statusStr")
        insertGuardianLogToSupabase("감시 모드 상태 변경: $statusStr")
    }

    fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logsState.value = _logsState.value + "[$time] $msg"
    }

    fun clearLogs() {
        _logsState.value = listOf("[시스템] 로그 초기화됨")
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            addLog("[동기화] Supabase DB 데이터 동기화 시도...")
            fetchVehicleStateFromSupabase()
            fetchTripsFromSupabase()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchVehicleStateFromSupabase() = withContext(Dispatchers.IO) {
        val config = _configState.value
        if (config.supabaseUrl.isBlank() || config.supabaseKey.isBlank()) {
            addLog("[안내] 우측 상단 '⚙️ 설정' 버튼을 눌러 Supabase URL과 Key를 입력해주세요.")
            return@withContext
        }

        try {
            val endpoint = "${config.supabaseUrl.trimEnd('/')}/rest/v1/vehicle_state?select=*&order=updated_at.desc&limit=1"
            val responseJson = ApiClient.executeSupabaseGet(endpoint, config.supabaseKey)

            if (responseJson.isNotBlank()) {
                val array = JSONArray(responseJson)
                if (array.length() > 0) {
                    val obj = array.getJSONObject(0)
                    val fetchedState = VehicleState(
                        statusText = obj.optString("status_text", "현재: 정보 수신됨"),
                        batteryLevel = obj.optInt("battery_level", 85),
                        odometer = obj.optInt("odometer", 45210),
                        outsideTemp = obj.optDouble("outside_temp", 24.0).toFloat(),
                        parkDurationStr = obj.optString("park_duration_str", "주차 중"),
                        tpmsFl = obj.optInt("tpms_fl", 42),
                        tpmsFr = obj.optInt("tpms_fr", 42),
                        tpmsRl = obj.optInt("tpms_rl", 41),
                        tpmsRr = obj.optInt("tpms_rr", 41)
                    )
                    _vehicleState.value = fetchedState
                    addLog("[성공] 차량 상태 수신 (배터리: ${fetchedState.batteryLevel}%)")
                }
            }
        } catch (e: Exception) {
            addLog("[오류] 차량 상태 Supabase 수신 실패: ${e.localizedMessage}")
        }
    }

    private suspend fun fetchTripsFromSupabase() = withContext(Dispatchers.IO) {
        val config = _configState.value
        if (config.supabaseUrl.isBlank() || config.supabaseKey.isBlank()) return@withContext

        try {
            val endpoint = "${config.supabaseUrl.trimEnd('/')}/rest/v1/trips?select=*&order=created_at.desc&limit=10"
            val responseJson = ApiClient.executeSupabaseGet(endpoint, config.supabaseKey)

            if (responseJson.isNotBlank()) {
                val array = JSONArray(responseJson)
                val list = mutableListOf<TripItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        TripItem(
                            id = obj.optString("id", i.toString()),
                            timeStr = obj.optString("time_str", "--:--"),
                            startDong = obj.optString("start_dong", "출발지"),
                            endDong = obj.optString("end_dong", "도착지"),
                            moveKm = obj.optDouble("move_km", 0.0),
                            durationMin = obj.optInt("duration_min", 0),
                            useBattery = obj.optDouble("use_battery", 0.0),
                            startBat = obj.optInt("start_bat", 0),
                            endBat = obj.optInt("end_bat", 0),
                            odometer = obj.optInt("odometer", 0)
                        )
                    )
                }
                if (list.isNotEmpty()) {
                    _tripsState.value = list
                    addLog("[성공] 주행 기록 ${list.size}건 수신 완료")
                }
            }
        } catch (e: Exception) {
            addLog("[오류] 주행 목록 수신 실패: ${e.localizedMessage}")
        }
    }

    private fun insertGuardianLogToSupabase(logMessage: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val config = _configState.value
            if (config.supabaseUrl.isBlank() || config.supabaseKey.isBlank()) return@launch

            try {
                val endpoint = "${config.supabaseUrl.trimEnd('/')}/rest/v1/guardian_logs"
                val json = JSONObject().apply {
                    put("message", logMessage)
                    put("created_at", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date()))
                }
                ApiClient.executeSupabasePost(endpoint, config.supabaseKey, json.toString())
                addLog("[Supabase] 가디언 로그 저장 성공")
            } catch (e: Exception) {
                addLog("[오류] DB 로그 저장 실패: ${e.localizedMessage}")
            }
        }
    }
}
