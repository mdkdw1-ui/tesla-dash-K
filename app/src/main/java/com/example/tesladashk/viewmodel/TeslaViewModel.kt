package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeslaViewModel : ViewModel() {

    private val _config = MutableStateFlow(ConfigState())
    val config: StateFlow<ConfigState> = _config.asStateFlow()
    val configState: StateFlow<ConfigState> = _config.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _trips = MutableStateFlow<List<TripItem>>(emptyList())
    val trips: StateFlow<List<TripItem>> = _trips.asStateFlow()
    val tripsState: StateFlow<List<TripItem>> = _trips.asStateFlow()

    private val _selectedTrip = MutableStateFlow<TripItem?>(null)
    val selectedTrip: StateFlow<TripItem?> = _selectedTrip.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()
    val logsState: StateFlow<List<String>> = _logs.asStateFlow()

    private val _isGuardianActive = MutableStateFlow(false)
    val isGuardianActive: StateFlow<Boolean> = _isGuardianActive.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _accessToken = MutableStateFlow(TeslaApi.DEFAULT_ACCESS_TOKEN)
    val accessToken: String get() = _accessToken.value
    val accessTokenState: StateFlow<String> = _accessToken.asStateFlow()

    init {
        addLog("[시스템] TeslaViewModel 준비 완료")
    }

    fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logs.value = (listOf("[$time] $msg") + _logs.value).take(100)
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun toggleGuardian(active: Boolean = !_isGuardianActive.value) {
        _isGuardianActive.value = active
        addLog("[가디언] 감시 모드 가디언 ${if (_isGuardianActive.value) "활성화" else "비활성화"}")
    }

    fun selectTrip(trip: TripItem) {
        _selectedTrip.value = trip
    }

    fun refreshData() {
        syncWithSupabase()
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

    fun updateConfig(
        kakaoKey: String = "",
        supabaseUrl: String = "",
        supabaseKey: String = "",
        ghToken: String = "",
        vehicleId: String = "",
        ntfyTopic: String = ""
    ) {
        saveConfig(kakaoKey, supabaseUrl, supabaseKey, ghToken, vehicleId, ntfyTopic)
    }

    fun updateConfig(newConfig: ConfigState) {
        _config.value = newConfig
        addLog("[설정] API 및 DB 설정 정보가 저장되었습니다.")
        syncWithSupabase()
    }

    fun syncWithSupabase() {
        viewModelScope.launch {
            _isRefreshing.value = true
            val cfg = _config.value
            if (cfg.supabaseUrl.isBlank() || cfg.supabaseKey.isBlank()) {
                addLog("[동기화 실패] Supabase URL 또는 Key가 설정되지 않았습니다.")
                _isRefreshing.value = false
                return@launch
            }

            val baseUrl = cfg.supabaseUrl.split("/rest/v1")[0].removeSuffix("/")
            addLog("[동기화] Supabase vehicle 및 driving 데이터 수신 중...")

            // 1. vehicle 테이블 (차량 상태)
            val vehicleUrl = "$baseUrl/rest/v1/vehicle?select=*&order=updated_at.desc&limit=1"
            val vehicleResp = ApiClient.executeSupabaseGet(vehicleUrl, cfg.supabaseKey)

            if (vehicleResp.isSuccess) {
                try {
                    val arr = JSONArray(vehicleResp.body)
                    if (arr.length() > 0) {
                        val obj = arr.getJSONObject(0)
                        val stateStr = obj.optString("state", "online")
                        val formattedStatus = when (stateStr.lowercase()) {
                            "online" -> "현재: 대기 중 (Online)"
                            "driving" -> "현재: 주행 중 (Driving)"
                            "charging" -> "현재: 충전 중 (Charging)"
                            "offline" -> "현재: 오프라인 (Offline)"
                            else -> "현재: $stateStr"
                        }
                        _vehicleState.value = VehicleState(
                            statusText = formattedStatus,
                            batteryLevel = obj.optInt("battery_level", 0),
                            odometer = obj.optInt("odometer", 0),
                            outsideTemp = obj.optDouble("outside_temp", 22.0).toFloat(),
                            parkDurationStr = obj.optString("park_duration_str", "상태 수신 완료")
                        )
                    }
                } catch (e: Exception) {
                    addLog("[차량상태 파싱 오류] ${e.localizedMessage}")
                }
            }

            // 2. driving 테이블 (주행 기록 최신 30건)
            val drivingUrl = "$baseUrl/rest/v1/driving?select=*&order=created_at.desc&limit=30"
            val drivingResp = ApiClient.executeSupabaseGet(drivingUrl, cfg.supabaseKey)

            if (drivingResp.isSuccess) {
                try {
                    val arr = JSONArray(drivingResp.body)
                    val parsedTrips = mutableListOf<TripItem>()

                    for (i in 0 until arr.length()) {
                        val item = arr.getJSONObject(i)
                        
                        // 날짜 포맷 변환 (2025-12-18T10:44:44 -> 2025.12.18 10:44)
                        val rawCreated = item.optString("created_at", "")
                        val formattedDate = rawCreated.replace("T", " ").take(16).ifEmpty { "날짜 미상" }

                        parsedTrips.add(
                            TripItem(
                                id = item.optString("id", "$i"),
                                date = formattedDate,
                                startAddress = item.optString("start_address", "출발지 미기재"),
                                endAddress = item.optString("end_address", "도착지 미기재"),
                                distanceKm = item.optDouble("move_km", 0.0),
                                batteryUsed = item.optDouble("use_battery", 0.0),
                                driveTimeMin = item.optInt("driving_time", 0),
                                locationListJson = item.optString("location_list", "[]")
                            )
                        )
                    }

                    _trips.value = parsedTrips
                    if (parsedTrips.isNotEmpty() && _selectedTrip.value == null) {
                        _selectedTrip.value = parsedTrips[0]
                    }
                    addLog("[주행기록 동기화 성공] 최신 ${parsedTrips.size}건 수신 완료")
                } catch (e: Exception) {
                    addLog("[주행기록 파싱 오류] ${e.localizedMessage}")
                }
            } else {
                addLog("[주행기록 수신 실패] Code ${drivingResp.statusCode}")
            }

            _isRefreshing.value = false
        }
    }
}
