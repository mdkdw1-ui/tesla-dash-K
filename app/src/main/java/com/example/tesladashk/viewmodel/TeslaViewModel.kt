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
import java.util.TimeZone

class TeslaViewModel : ViewModel() {

    private val _config = MutableStateFlow(ConfigState())
    val config: StateFlow<ConfigState> = _config.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _trips = MutableStateFlow<List<TripItem>>(emptyList())
    val trips: StateFlow<List<TripItem>> = _trips.asStateFlow()

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs.asStateFlow()

    private val _isGuardianActive = MutableStateFlow(false)
    val isGuardianActive: StateFlow<Boolean> = _isGuardianActive.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        addLog("[시스템] TeslaViewModel 준비 완료")
    }

    fun addLog(msg: String) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val time = sdf.format(Date())
        _logs.value = (listOf("[$time] $msg") + _logs.value).take(100)
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun toggleGuardian(active: Boolean = !_isGuardianActive.value) {
        _isGuardianActive.value = active
        addLog("[가디언] 감시 모드 ${if (_isGuardianActive.value) "활성화" else "비활성화"}")
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            addLog("[갱신 요청] Supabase 데이터 수신 시작...")

            val cfg = _config.value
            if (cfg.ghToken.isNotBlank()) {
                ApiClient.triggerGitHubSync(cfg.ghToken)
            }

            syncWithSupabaseInternal()
            _isRefreshing.value = false
        }
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
        refreshData()
    }

    private suspend fun syncWithSupabaseInternal() {
        val cfg = _config.value
        if (cfg.supabaseUrl.isBlank() || cfg.supabaseKey.isBlank()) return

        val baseUrl = cfg.supabaseUrl.split("/rest/v1")[0].removeSuffix("/")

        // 1. 차량 정보 수신
        val vehicleUrl = "$baseUrl/rest/v1/vehicle?select=*&order=updated_at.desc&limit=1"
        val vehicleResp = ApiClient.executeSupabaseGet(vehicleUrl, cfg.supabaseKey)
        if (vehicleResp.isSuccess) {
            try {
                val arr = JSONArray(vehicleResp.body)
                if (arr.length() > 0) {
                    val obj = arr.getJSONObject(0)
                    _vehicleState.value = VehicleState(
                        statusText = "현재: " + obj.optString("state", "online"),
                        batteryLevel = obj.optInt("battery_level", 0),
                        odometer = obj.optInt("odometer", 0),
                        outsideTemp = obj.optDouble("outside_temp", 0.0).toFloat(),
                        parkDurationStr = obj.optString("park_duration_str", "상태 수신 완료"),
                        tpmsFl = obj.optDouble("tpms_fl", 0.0).toFloat(),
                        tpmsFr = obj.optDouble("tpms_fr", 0.0).toFloat(),
                        tpmsRl = obj.optDouble("tpms_rl", 0.0).toFloat(),
                        tpmsRr = obj.optDouble("tpms_rr", 0.0).toFloat()
                    )
                }
            } catch (e: Exception) {
                addLog("[차량 파싱 오류] ${e.localizedMessage}")
            }
        }

        // 2. 주행 기록 수신 (300개 제한 및 KST 시차 변환)
        val drivingUrl = "$baseUrl/rest/v1/driving?select=*&order=created_at.desc&limit=300"
        val drivingResp = ApiClient.executeSupabaseGet(drivingUrl, cfg.supabaseKey)
        if (drivingResp.isSuccess) {
            try {
                val arr = JSONArray(drivingResp.body)
                val parsed = mutableListOf<TripItem>()

                val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val kstFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("Asia/Seoul")
                }

                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    val rawCreated = obj.optString("created_at", "")

                    val kstDateStr = try {
                        val parsedUtc = utcFormat.parse(rawCreated.take(19))
                        parsedUtc?.let { kstFormat.format(it) } ?: rawCreated.replace("T", " ").take(16)
                    } catch (e: Exception) {
                        rawCreated.replace("T", " ").take(16)
                    }

                    val sAddr = obj.optString("start_address", "출발지")
                    val eAddr = obj.optString("end_address", "도착지")
                    val mKm = obj.optDouble("move_km", 0.0)
                    val uBat = obj.optDouble("use_battery", 0.0)
                    val dMin = obj.optInt("driving_time", 0)

                    parsed.add(
                        TripItem(
                            id = obj.optString("id", "$i"),
                            timeStr = kstDateStr,
                            startDong = sAddr,
                            endDong = eAddr,
                            moveKm = mKm,
                            durationMin = dMin,
                            useBattery = uBat,
                            date = kstDateStr,
                            startAddress = sAddr,
                            endAddress = eAddr,
                            distanceKm = mKm,
                            batteryUsed = uBat,
                            driveTimeMin = dMin
                        )
                    )
                }
                _trips.value = parsed
                addLog("[주행 동기화 완료] 최신 ${parsed.size}건 수신 (KST 시차 적용)")
            } catch (e: Exception) {
                addLog("[주행 파싱 오류] ${e.localizedMessage}")
            }
        }
    }
}
