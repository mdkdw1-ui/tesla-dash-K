package com.mdkdw1.ui.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Tesla Dash K - 데이터 저장소 (Supabase / Tesla API 연동 및 Fallback 데이터 관리)
 */
class TeslaRepository(
    private val encryptedSettingsManager: EncryptedSettingsManager
) {
    /**
     * 차량 상태 데이터 조회
     */
    suspend fun getVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        val settings = encryptedSettingsManager.getSettings()

        // Supabase / Tesla API 연동 준비 및 Mock Fallback 데이터 반환
        VehicleState(
            vehicleName = "Tesla Model Y",
            batteryPercent = 78,
            range = 345.0,
            estimatedRangeKm = 320.0,
            totalOdometer = 24500.0,
            cabinTemp = 21.5,
            insideTempC = 21.5,
            outsideTempC = 18.0,
            isClimateOn = false,
            chargeStatus = "Disconnected",
            isTrunkOpen = false,
            isFrunkOpen = false,
            isSentryModeOn = true,
            speedKmh = 0.0,
            tpmsFrontLeft = 2.9,
            tpmsFrontRight = 2.9,
            tpmsRearLeft = 2.8,
            tpmsRearRight = 2.8,
            isLocked = true,
            isSteeringHeaterOn = false,
            seatHeaterDriver = 0,
            seatHeaterPassenger = 0,
            parkedTimeMinutes = 120,
            guardianAlertsCount = 3,
            latitude = 37.5665,
            longitude = 126.9780,
            locationName = "서울특별시 중구 세종대로 110"
        )
    }

    /**
     * 주행 및 충전 일지 목록 조회
     */
    suspend fun getJournalLogs(): List<JournalLogItem> = withContext(Dispatchers.IO) {
        listOf(
            JournalLogItem(
                id = "1",
                timestamp = "2026-07-27 09:30",
                type = JournalType.DRIVE,
                title = "서울 -> 판교 출근 주행",
                startSoc = 85,
                endSoc = 78,
                distanceKm = 24.5,
                energyUsedKwh = 3.8,
                efficiencyWhKm = 155.1,
                costWon = 1200,
                location = "판교 테크노밸리"
            ),
            JournalLogItem(
                id = "2",
                timestamp = "2026-07-26 22:00",
                type = JournalType.CHARGE,
                title = "슈퍼차저 완속 충전",
                startSoc = 30,
                endSoc = 85,
                distanceKm = 0.0,
                energyUsedKwh = 42.0,
                efficiencyWhKm = 0.0,
                costWon = 14200,
                location = "강남 슈퍼차저"
            ),
            JournalLogItem(
                id = "3",
                timestamp = "2026-07-25 18:15",
                type = JournalType.DRIVE,
                title = "주말 근교 드라이브",
                startSoc = 90,
                endSoc = 65,
                distanceKm = 88.2,
                energyUsedKwh = 14.1,
                efficiencyWhKm = 159.8,
                costWon = 4800,
                location = "남양주 카페거리"
            )
        )
    }

    suspend fun getDriveLogs(): List<JournalLogItem> = getJournalLogs()

    /**
     * 배터리 열화 및 100% 환산 주행거리 기록 조회
     */
    suspend fun getBatteryRecords(): List<BatteryRecord> = withContext(Dispatchers.IO) {
        listOf(
            BatteryRecord("1", "2026-01", 99.2, 425.0, 75.0),
            BatteryRecord("2", "2026-02", 98.9, 423.5, 74.8),
            BatteryRecord("3", "2026-03", 98.7, 422.8, 74.6),
            BatteryRecord("4", "2026-04", 98.5, 421.0, 74.5),
            BatteryRecord("5", "2026-05", 98.2, 420.2, 74.3),
            BatteryRecord("6", "2026-06", 98.0, 419.5, 74.1),
            BatteryRecord("7", "2026-07", 97.8, 418.8, 74.0)
        )
    }

    suspend fun getBatteryHistory(): List<BatteryRecord> = getBatteryRecords()

    /**
     * 소모품 교체 주기 및 현황 조회
     */
    suspend fun getConsumables(): List<ConsumableItem> = withContext(Dispatchers.IO) {
        listOf(
            ConsumableItem("1", "캐빈 에어컨 필터", 15000, 12000, 24500, "2025-10-15"),
            ConsumableItem("2", "타이어 교체 및 위치 이동", 0, 40000, 24500, "2024-05-01"),
            ConsumableItem("3", "브레이크 오일", 0, 40000, 24500, "2024-05-01"),
            ConsumableItem("4", "와이퍼 블레이드", 18000, 10000, 24500, "2026-01-10")
        )
    }

    // --- 원격 제어 명령 메소드 ---

    suspend fun toggleClimate(currentState: Boolean): Boolean = withContext(Dispatchers.IO) {
        !currentState
    }

    suspend fun toggleDoorLock(currentState: Boolean): Boolean = withContext(Dispatchers.IO) {
        !currentState
    }

    suspend fun toggleTrunk(currentState: Boolean): Boolean = withContext(Dispatchers.IO) {
        !currentState
    }

    suspend fun toggleFrunk(currentState: Boolean): Boolean = withContext(Dispatchers.IO) {
        !currentState
    }

    suspend fun toggleSentryMode(currentState: Boolean): Boolean = withContext(Dispatchers.IO) {
        !currentState
    }

    suspend fun toggleSteeringHeater(currentState: Boolean): Boolean = withContext(Dispatchers.IO) {
        !currentState
    }

    suspend fun setSeatHeater(seat: String, level: Int): Int = withContext(Dispatchers.IO) {
        level
    }

    suspend fun toggleCharging(isCharging: Boolean): Boolean = withContext(Dispatchers.IO) {
        !isCharging
    }

    suspend fun saveSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
        encryptedSettingsManager.saveSettings(settings)
    }
}
