package com.mdkdw1.ui.tesla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TeslaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TeslaRepository(application.applicationContext)

    private val _settings = MutableStateFlow(AppConfig())
    val settings: StateFlow<AppConfig> = _settings.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _consumables = MutableStateFlow<List<ConsumableItem>>(emptyList())
    val consumables: StateFlow<List<ConsumableItem>> = _consumables.asStateFlow()

    private val _dailyTrips = MutableStateFlow<List<DailyTrip>>(emptyList())
    val dailyTrips: StateFlow<List<DailyTrip>> = _dailyTrips.asStateFlow()

    private val _chargeRecords = MutableStateFlow<List<ChargeRecord>>(emptyList())
    val chargeRecords: StateFlow<List<ChargeRecord>> = _chargeRecords.asStateFlow()

    private val _degradationData = MutableStateFlow<List<BatteryDegradationPoint>>(emptyList())
    val degradationData: StateFlow<List<BatteryDegradationPoint>> = _degradationData.asStateFlow()

    private val _commandLog = MutableStateFlow<List<String>>(emptyList())
    val commandLog: StateFlow<List<String>> = _commandLog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadSettings()
        refreshAllData()
    }

    fun loadSettings() {
        _settings.value = repository.getSettings()
    }

    fun saveSettings(supabaseUrl: String, supabaseKey: String, kakaoMapKey: String, teslaAccessToken: String) {
        val newConfig = AppConfig(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
            kakaoMapKey = kakaoMapKey,
            teslaAccessToken = teslaAccessToken
        )
        repository.saveSettings(newConfig)
        _settings.value = newConfig
        addLog("보안 설정이 AES-256으로 암호화되어 저장되었습니다.")
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _vehicleState.value = repository.fetchVehicleData()
                _consumables.value = repository.fetchConsumableItems()
                _dailyTrips.value = repository.fetchDailyTrips()
                _chargeRecords.value = repository.fetchChargeRecords()
                _degradationData.value = repository.fetchDegradationData()
                addLog("모든 데이터가 정상 동기화되었습니다.")
            } catch (e: Exception) {
                addLog("데이터 동기화 오류: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendCommand(command: String) {
        viewModelScope.launch {
            val success = repository.sendCommand(command)
            if (success) {
                val current = _vehicleState.value
                when (command) {
                    "LOCK" -> _vehicleState.value = current.copy(isLocked = true)
                    "UNLOCK" -> _vehicleState.value = current.copy(isLocked = false)
                    "CLIMATE_TOGGLE" -> _vehicleState.value = current.copy(isClimateOn = !current.isClimateOn)
                    "TRUNK_TOGGLE" -> _vehicleState.value = current.copy(isTrunkOpen = !current.isTrunkOpen)
                    "FRUNK_TOGGLE" -> _vehicleState.value = current.copy(isFrunkOpen = !current.isFrunkOpen)
                    "SENTRY_TOGGLE" -> _vehicleState.value = current.copy(isSentryModeOn = !current.isSentryModeOn)
                }
                addLog("명령 실행 완료: $command")
            } else {
                addLog("명령 실행 실패: $command")
            }
        }
    }

    fun resetConsumable(itemName: String) {
        viewModelScope.launch {
            repository.resetConsumable(itemName)
            _consumables.value = repository.fetchConsumableItems()
            addLog("소모품 주기 리셋 완료: $itemName")
        }
    }

    private fun addLog(msg: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _commandLog.value = listOf("[$time] $msg") + _commandLog.value.take(19)
    }
}
