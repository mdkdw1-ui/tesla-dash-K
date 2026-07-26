package com.mdkdw1.ui.tesla

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ==========================================
// 1. 데이터 모델 (Tesla Data Models)
// ==========================================
data class VehicleStateData(
    val statusText: String = "주차됨 (감시 중)",
    val lastUpdated: String = "방금 전",
    val batteryLevel: Int = 82,
    val batteryRangeKm: Int = 385,
    val isLocked: Boolean = true,
    val isSentryOn: Boolean = true,
    val isClimateOn: Boolean = false,
    val insideTemp: Int = 21,
    val isDoorOpen: Boolean = false,
    val lat: Double = 37.5665,
    val lng: Double = 126.9780
)

data class BatteryRecord(
    val month: String,
    val healthPercent: Float
)

data class AppConfig(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "",
    val teslaToken: String = ""
)

// ==========================================
// 2. 보안 설정 저장소 (AES-256 하드웨어 암호화)
// ==========================================
class SecureSettingsManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "tesla_hub_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveConfig(supabaseUrl: String, supabaseKey: String, kakaoKey: String, teslaToken: String) {
        sharedPreferences.edit().apply {
            putString("KEY_SUPABASE_URL", supabaseUrl)
            putString("KEY_SUPABASE_KEY", supabaseKey)
            putString("KEY_KAKAO_KEY", kakaoKey)
            putString("KEY_TESLA_TOKEN", teslaToken)
            apply()
        }
    }

    fun getConfig(): AppConfig {
        return AppConfig(
            supabaseUrl = sharedPreferences.getString("KEY_SUPABASE_URL", "") ?: "",
            supabaseKey = sharedPreferences.getString("KEY_SUPABASE_KEY", "") ?: "",
            kakaoKey = sharedPreferences.getString("KEY_KAKAO_KEY", "") ?: "",
            teslaToken = sharedPreferences.getString("KEY_TESLA_TOKEN", "") ?: ""
        )
    }
}

// ==========================================
// 3. 비동기 테슬라 및 보안 데이터 Repository
// ==========================================
class TeslaHubRepository {
    private val _vehicleData = MutableStateFlow(VehicleStateData())
    val vehicleData: StateFlow<VehicleStateData> = _vehicleData.asStateFlow()

    private val _batteryRecords = MutableStateFlow(
        listOf(
            BatteryRecord("1월", 99.2f),
            BatteryRecord("2월", 98.8f),
            BatteryRecord("3월", 98.5f),
            BatteryRecord("4월", 98.1f),
            BatteryRecord("5월", 97.9f),
            BatteryRecord("6월", 97.6f)
        )
    )
    val batteryRecords: StateFlow<List<BatteryRecord>> = _batteryRecords.asStateFlow()

    fun toggleLock() {
        val current = _vehicleData.value
        _vehicleData.value = current.copy(isLocked = !current.isLocked)
    }

    fun toggleSentry() {
        val current = _vehicleData.value
        _vehicleData.value = current.copy(isSentryOn = !current.isSentryOn)
    }

    fun toggleClimate() {
        val current = _vehicleData.value
        _vehicleData.value = current.copy(isClimateOn = !current.isClimateOn)
    }

    fun triggerFrunk() {
        // 프렁크 열기 명령
    }

    fun triggerTrunk() {
        // 트렁크 열기 명령
    }

    fun triggerFlashLights() {
        // 비상등 점등 명령
    }

    fun checkGuardianSecurityAlert(): String? {
        val current = _vehicleData.value
        return if (!current.isLocked && current.isSentryOn) {
            "⚠️ 경고: 감시 모드가 활성화되어 있으나 도어가 잠기지 않았습니다!"
        } else null
    }
}
