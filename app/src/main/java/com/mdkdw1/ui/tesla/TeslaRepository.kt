package com.mdkdw1.ui.tesla

import kotlinx.coroutines.dispatchers.Dispatchers
import kotlinx.coroutines.withContext

class TeslaRepository(private val settingsManager: EncryptedSettingsManager) {

    suspend fun getVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        // 실제 API/Supabase 연동 기본 구조 및 가상 데이터 바인딩
        VehicleState()
    }

    suspend fun getDegradationHistory(): List<DegradationRecord> = withContext(Dispatchers.IO) {
        listOf(
            DegradationRecord("2023-01-15", 5000.0, 488, 0.4),
            DegradationRecord("2023-06-20", 12000.0, 485, 1.0),
            DegradationRecord("2023-12-10", 21000.0, 481, 1.8),
            DegradationRecord("2024-05-18", 32450.0, 478, 2.4)
        )
    }

    suspend fun getChargeRecords(): List<ChargeRecord> = withContext(Dispatchers.IO) {
        listOf(
            ChargeRecord("chg_01", "2024-05-18 14:20", "판교 슈퍼차저", 42.5, 14800, 20, 80, "Supercharger"),
            ChargeRecord("chg_02", "2024-05-15 22:00", "자택 완속 충전", 31.0, 6200, 35, 80, "AC Home"),
            ChargeRecord("chg_03", "2024-05-10 11:15", "신세계백화점 강남점", 18.2, 5400, 50, 75, "AC Public")
        )
    }

    suspend fun getConsumableItems(): List<ConsumableItem> = withContext(Dispatchers.IO) {
        listOf(
            ConsumableItem("c1", "에어컨 캐빈 필터", 20000, 12, 18000.0, "2023-08-10", "air"),
            ConsumableItem("c2", "와이퍼 블레이드", 15000, 12, 22000.0, "2023-05-01", "build"),
            ConsumableItem("c3", "브레이크 오일", 40000, 24, 10000.0, "2022-11-15", "opacity"),
            ConsumableItem("c4", "타이어 위치 교환", 10000, 6, 25000.0, "2023-10-05", "refresh")
        )
    }

    fun saveAppSettings(settings: AppSettings) {
        settingsManager.saveSettings(settings)
    }

    fun loadAppSettings(): AppSettings {
        return settingsManager.loadSettings()
    }
}
