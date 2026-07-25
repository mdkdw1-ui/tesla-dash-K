package com.example.tesladashk.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.AppConfig
import com.example.tesladashk.network.DrivingTrip
import com.example.tesladashk.network.SupabaseRepository
import com.example.tesladashk.network.VehicleRow
import com.example.tesladashk.utils.ConfigStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogMessage(
    val timestamp: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
    val text: String,
    val isError: Boolean = false
)

class DashboardViewModel : ViewModel() {
    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config.asStateFlow()

    private val _vehicleRows = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleRows: StateFlow<List<VehicleRow>> = _vehicleRows.asStateFlow()

    private val _trips = MutableStateFlow<List<DrivingTrip>>(emptyList())
    val trips: StateFlow<List<DrivingTrip>> = _trips.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("-")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 실시간 감시모드 / 시스템 로그
    private val _logList = MutableStateFlow<List<LogMessage>>(emptyList())
    val logList: StateFlow<List<LogMessage>> = _logList.asStateFlow()

    fun addLog(msg: String, isError: Boolean = false) {
        val newLog = LogMessage(text = msg, isError = isError)
        _logList.value = listOf(newLog) + _logList.value.take(99) // 최대 100개 유지
    }

    fun clearLogs() {
        _logList.value = emptyList()
    }

    fun loadInitialConfig(context: Context) {
        val loaded = ConfigStorage.loadConfig(context)
        _config.value = loaded
        addLog("⚙️ 설정 정보를 로드했습니다.")
    }

    fun saveConfig(context: Context, newConfig: AppConfig) {
        _config.value = newConfig
        ConfigStorage.saveConfig(context, newConfig)
        addLog("💾 설정을 저장했습니다.")
        triggerSyncAndFetch()
    }

    fun triggerSyncAndFetch() {
        viewModelScope.launch {
            _isLoading.value = true
            val cfg = _config.value
            addLog("🔄 Supabase 데이터 갱신 요청 중...")

            if (cfg.supabaseUrl.isBlank() || cfg.supabaseKey.isBlank()) {
                addLog("⚠️ Supabase URL 또는 Key가 누락되었습니다.", isError = true)
                _isLoading.value = false
                return@launch
            }

            // 1. 차량 데이터 가져오기
            val vResult = SupabaseRepository.fetchVehicleData(cfg.supabaseUrl, cfg.supabaseKey, cfg.userUid)
            vResult.onSuccess { list ->
                addLog("✅ Vehicle 수신 성공: ${list.size}건")
                _vehicleRows.value = list.map { dto ->
                    VehicleRow(
                        state = dto.state ?: "offline",
                        batteryLevel = dto.batteryLevel ?: 0,
                        odometer = dto.odometer ?: 0.0,
                        tpmsFl = dto.tpmsFl,
                        tpmsFr = dto.tpmsFr,
                        tpmsRl = dto.tpmsRl,
                        tpmsRr = dto.tpmsRr
                    )
                }
            }.onFailure { err ->
                addLog("❌ Vehicle 수신 실패: ${err.message}", isError = true)
            }

            // 2. 주행 데이터 가져오기
            val dResult = SupabaseRepository.fetchDrivingData(cfg.supabaseUrl, cfg.supabaseKey, cfg.userUid)
            dResult.onSuccess { list ->
                addLog("✅ Driving 수신 성공: ${list.size}건")
                _trips.value = list.map { dto ->
                    DrivingTrip(
                        startTime = dto.createdAt ?: "-",
                        moveKM = dto.moveKm ?: 0.0,
                        startDong = dto.startAddress ?: "알 수 없음",
                        endDong = dto.endAddress ?: "알 수 없음"
                    )
                }
            }.onFailure { err ->
                addLog("❌ Driving 수신 실패: ${err.message}", isError = true)
            }

            // 3. 카카오 API 검증
            if (cfg.kakaoKey.isNotBlank()) {
                val kResult = SupabaseRepository.testKakaoMapApi(cfg.kakaoKey)
                kResult.onSuccess { msg ->
                    addLog("🗺️ 카카오맵 API: $msg")
                }.onFailure { err ->
                    addLog("⚠️ 카카오맵 API 오류: ${err.message}", isError = true)
                }
            } else {
                addLog("ℹ️ 카카오 API 키가 설정되지 않았습니다.")
            }

            _lastSyncTime.value = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            _isLoading.value = false
        }
    }
}
