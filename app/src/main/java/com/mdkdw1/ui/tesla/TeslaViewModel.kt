package com.mdkdw1.ui.tesla

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mdkdw1.data.EncryptedSettings
import com.mdkdw1.data.TeslaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoMapKey: String = "",
    val isAutoSync: Boolean = false
)

data class VehicleState(
    val speed: Int = 0,
    val batteryLevel: Int = 82,
    val range: Int = 412,
    val isLocked: Boolean = true,
    val climateOn: Boolean = false,
    val odometer: Double = 14230.5,
    val tirePressureFrontLeft: Double = 2.9,
    val tirePressureFrontRight: Double = 2.9,
    val tirePressureRearLeft: Double = 2.8,
    val tirePressureRearRight: Double = 2.8,
    val cabinTemp: Double = 21.5,
    val isCharging: Boolean = false,
    val chargeLimit: Int = 90
)

class TeslaViewModel(application: Application) : AndroidViewModel(application) {
    private val encryptedSettings = EncryptedSettings(application)
    private val repository = TeslaRepository()

    var settings by mutableStateOf(AppSettings())
        private set

    var vehicleState by mutableStateOf(VehicleState())
        private set

    private val _uiState = MutableStateFlow<String>("Ready")
    val uiState: StateFlow<String> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun loadSettings() {
        val url = encryptedSettings.getSupabaseUrl() ?: ""
        val key = encryptedSettings.getSupabaseKey() ?: ""
        val kakaoKey = encryptedSettings.getKakaoMapKey() ?: ""
        val autoSync = encryptedSettings.isAutoSync()
        settings = AppSettings(supabaseUrl = url, supabaseKey = key, kakaoMapKey = kakaoKey, isAutoSync = autoSync)
    }

    fun saveSettings(newSettings: AppSettings) {
        settings = newSettings
        encryptedSettings.saveSupabaseUrl(newSettings.supabaseUrl)
        encryptedSettings.saveSupabaseKey(newSettings.supabaseKey)
        encryptedSettings.saveKakaoMapKey(newSettings.kakaoMapKey)
        encryptedSettings.setAutoSync(newSettings.isAutoSync)
        _uiState.value = "설정이 암호화되어 저장되었습니다."
    }

    fun toggleLock() {
        vehicleState = vehicleState.copy(isLocked = !vehicleState.isLocked)
        _uiState.value = if (vehicleState.isLocked) "차량이 잠겼습니다." : "차량 잠금이 해제되었습니다."
    }

    fun toggleClimate() {
        vehicleState = vehicleState.copy(climateOn = !vehicleState.climateOn)
        _uiState.value = if (vehicleState.climateOn) "공조 장치가 켜졌습니다." : "공조 장치가 꺼졌습니다."
    }

    fun toggleCharging() {
        vehicleState = vehicleState.copy(isCharging = !vehicleState.isCharging)
        _uiState.value = if (vehicleState.isCharging) "충전이 시작되었습니다." : "충전이 중지되었습니다."
    }
}
