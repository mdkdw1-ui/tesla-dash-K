package com.example.tesladashk.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
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

class TeslaViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("tesla_dash_prefs", Context.MODE_PRIVATE)

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
        loadConfigFromPrefs()
        addLog("[시스템] TeslaViewModel 준비 완료")
    }

    private fun loadConfigFromPrefs() {
        val kakaoKey = prefs.getString("kakaoKey", "") ?: ""
        val supabaseUrl = prefs.getString("supabaseUrl", "") ?: ""
        val supabaseKey = prefs.getString("supabaseKey", "") ?: ""
        val vercelUrl = prefs.getString("vercelUrl", "https://my-tesla-app.vercel.app") ?: ""
        val vehicleId = prefs.getString("vehicleId", "") ?: ""
        val ntfyTopic = prefs.getString("ntfyTopic", "") ?: ""
        val ghToken = prefs.getString("ghToken", "") ?: ""

        _config.value = ConfigState(
            kakaoKey = kakaoKey,
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
            vercelUrl = vercelUrl,
            vehicleId = vehicleId,
            ntfyTopic = ntfyTopic,
            ghToken = ghToken
        )
        if (supabaseUrl.isNotBlank() && supabaseKey.isNotBlank()) {
            refreshData()
        }
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

            val cfg = _config.value
            if (cfg.vercelUrl.isNotBlank()) {
                addLog("[Vercel] api/sync 동기화 요청 중...")
                val vRes = ApiClient.triggerVercelSync(cfg.vercelUrl)
                if (vRes.isSuccess) {
                    addLog("[Vercel] 동기화 성공!")
                } else {
                    addLog("[Vercel] 동기화 응답: HTTP ${vRes.code}")
                }
            }

            addLog("[Supabase] 최신 데이터 조회 중...")
            syncWithSupabaseInternal()
            _isRefreshing.value = false
        }
    }

    fun saveConfig(
        kakaoKey: String,
        supabaseUrl: String,
        supabaseKey: String,
        vercelUrl: String,
        vehicleId: String,
        ntfyTopic: String,
        ghToken: String = ""
    ) {
        val cleanUrl = supabaseUrl.trim().removeSuffix("/")
        val cleanKey = supabaseKey.trim()
        val cleanKakao = kakaoKey.trim()
        val cleanVercel = vercelUrl.trim().removeSuffix("/")
        val cleanVid = vehicleId.trim()
        val cleanNtfy = ntfyTopic.trim()
        val cleanGh = ghToken.trim()

        prefs.edit().apply {
            putString("kakaoKey", cleanKakao)
            putString("supabaseUrl", cleanUrl)
            putString("supabaseKey", cleanKey)
            putString("vercelUrl", cleanVercel)
            putString("vehicleId", cleanVid)
            putString("ntfyTopic", cleanNtfy)
            putString("ghToken", cleanGh)
            apply()
        }

        _config.value = ConfigState(
            kakaoKey = cleanKakao,
            supabaseUrl = cleanUrl,
            supabaseKey = cleanKey,
            vercelUrl = cleanVercel,
            vehicleId = cleanVid,
            ntfyTopic = cleanNtfy,
            ghToken = cleanGh
        )
        addLog("[설정] 설정 정보 저장 완료")
        refreshData()
    }

    private fun parseToKst(rawStr: String): String {
        if (rawStr.isBlank()) return "-"
        val clean = rawStr.replace("Z", "+0000").replace("T", " ")
        val patterns = listOf(
            "yyyy-MM-dd HH:mm:ss.SSSSSS",
            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm"
        )
        for (pattern in patterns) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = sdf.parse(clean)
                if (date != null) {
                    val outSdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Seoul")
                    }
                    return outSdf.format(date)
                }
            } catch (_: Exception) {}
        }
        return rawStr.take(16).replace("T", " ")
    }

    private suspend fun syncWithSupabaseInternal() {
        val cfg = _config.value
        if (cfg.supabaseUrl.isBlank() || cfg.supabaseKey.isBlank()) {
            addLog("[경고] Supabase URL 또는 Key가 설정되지 않았습니다.")
            return
        }

        val baseUrl = cfg.supabaseUrl.split("/rest/v1")[0].removeSuffix("/")

        // 1. 차량 상태 정보
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
                addLog("[차량 상태 파싱 오류] ${e.localizedMessage}")
            }
        }

        val parsedList = mutableListOf<TripItem>()

        // 2. 주행 기록
        val drivingUrl = "$baseUrl/rest/v1/driving?select=*&order=created_at.desc&limit=1000"
        val drivingResp = ApiClient.executeSupabaseGet(drivingUrl, cfg.supabaseKey)
        var drivingCount = 0
        if (drivingResp.isSuccess) {
            try {
                val arr = JSONArray(drivingResp.body)
                drivingCount = arr.length()
                for (i in 0 until arr.length()) {
                    try {
                        val obj = arr.getJSONObject(i)
                        val rawCreated = obj.optString("created_at", obj.optString("start_time", ""))
                        val kstDateStr = parseToKst(rawCreated)

                        val sAddr = obj.optString("start_address", obj.optString("start_location", "출발지"))
                        val eAddr = obj.optString("end_address", obj.optString("end_location", "도착지"))
                        val mKm = obj.optDouble("move_km", obj.optDouble("distance", 0.0))
                        val uBat = obj.optDouble("use_battery", obj.optDouble("battery_used", 0.0))
                        val dMin = obj.optInt("driving_time", obj.optInt("duration_min", 0))

                        parsedList.add(
                            TripItem(
                                id = "drive_${obj.optString("id", i.toString())}",
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
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                addLog("[주행 파싱 오류] ${e.localizedMessage}")
            }
        }

        // 3. 충전 기록
        val chargingUrl = "$baseUrl/rest/v1/charging?select=*&order=created_at.desc&limit=500"
        val chargingResp = ApiClient.executeSupabaseGet(chargingUrl, cfg.supabaseKey)
        var chargingCount = 0
        if (chargingResp.isSuccess) {
            try {
                val arr = JSONArray(chargingResp.body)
                chargingCount = arr.length()
                for (i in 0 until arr.length()) {
                    try {
                        val obj = arr.getJSONObject(i)
                        val rawCreated = obj.optString("created_at", obj.optString("start_time", ""))
                        val kstDateStr = parseToKst(rawCreated)

                        val location = obj.optString("address", obj.optString("location", "충전 장소"))
                        val addedKwh = obj.optDouble("kwh", obj.optDouble("charge_energy_added", 0.0))
                        val duration = obj.optInt("charge_time_min", obj.optInt("duration_min", 0))

                        val chargeTitle = "⚡ [충전] $location"
                        val chargeDetail = "충전량: +${String.format(Locale.US, "%.1f", addedKwh)}kWh"

                        parsedList.add(
                            TripItem(
                                id = "charge_${obj.optString("id", i.toString())}",
                                timeStr = kstDateStr,
                                startDong = chargeTitle,
                                endDong = chargeDetail,
                                moveKm = 0.0,
                                durationMin = duration,
                                useBattery = -addedKwh,
                                date = kstDateStr,
                                startAddress = chargeTitle,
                                endAddress = chargeDetail,
                                distanceKm = 0.0,
                                batteryUsed = -addedKwh,
                                driveTimeMin = duration
                            )
                        )
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                addLog("[충전 파싱 오류] ${e.localizedMessage}")
            }
        }

        val sortedList = parsedList.sortedByDescending { it.timeStr }
        _trips.value = sortedList

        addLog("[동기화 완료] 주행 ${drivingCount}건, 충전 ${chargingCount}건 수신")
    }
}
