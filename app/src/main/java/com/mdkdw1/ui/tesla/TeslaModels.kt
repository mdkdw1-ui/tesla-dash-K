package com.mdkdw1.ui.tesla

import androidx.compose.ui.graphics.Color

// ==========================================
// 1. 차량 상태 (Vehicle State)
// ==========================================
data class VehicleState(
    val batteryLevel: Int = 78,
    val batteryRangeKm: Int = 385,
    val maxRangeKm: Int = 490,
    val speed: Int = 0,
    val isCharging: Boolean = false,
    val chargePowerKw: Double = 0.0,
    val odometerKm: Double = 32450.0,
    val insideTempC: Double = 21.5,
    val outsideTempC: Double = 18.0,
    val locked: Boolean = true,
    val climateOn: Boolean = false,
    val sentryMode: Boolean = true,
    val trunkOpen: Boolean = false,
    val frunkOpen: Boolean = false,
    val lastUpdated: String = "방금 전"
)

// ==========================================
// 2. 배터리 열화 기록 (Battery Degradation)
// ==========================================
data class DegradationRecord(
    val date: String,
    val odometerKm: Double,
    val maxRangeKm: Int,
    val degradationPercent: Double
)
typealias BatteryDegradation = DegradationRecord

// ==========================================
// 3. 충전 히스토리 (Charge History)
// ==========================================
data class ChargeRecord(
    val id: String,
    val date: String,
    val location: String,
    val addedKwh: Double,
    val costKrw: Int,
    val startSoc: Int,
    val endSoc: Int,
    val chargeType: String // "Supercharger", "AC Home", "AC Public"
)

// ==========================================
// 4. 소모품 관리 (Consumable Item)
// ==========================================
data class ConsumableItem(
    val id: String,
    val name: String,
    val replacementIntervalKm: Int,
    val replacementIntervalMonths: Int,
    val lastReplacedOdoKm: Double,
    val lastReplacedDate: String,
    val iconName: String = "build"
)

// ==========================================
// 5. 앱 암호화 설정 (AppSettings)
// ==========================================
data class AppSettings(
    val supabaseUrl: String = "",
    val supabaseKey: String = "",
    val kakaoKey: String = "",
    val teslaToken: String = "",
    val vehicleId: String = ""
)

// ==========================================
// 6. UI 테마 색상 상수
// ==========================================
object TeslaColors {
    val DarkBackground = Color(0xFF0D0E12)
    val DarkCard = Color(0xFF161820)
    val DarkBorder = Color(0xFF262936)
    val TextPrimary = Color(0xFFF3F4F6)
    val TextSecondary = Color(0xFF9CA3AF)
    val AccentAmber = Color(0xFFD97706)
    val AccentBlue = Color(0xFF3B82F6)
    val AccentGreen = Color(0xFF10B981)
    val AccentRed = Color(0xFFEF4444)
}
