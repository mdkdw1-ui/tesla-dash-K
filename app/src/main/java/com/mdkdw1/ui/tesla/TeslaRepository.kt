package com.mdkdw1.ui.tesla

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TeslaRepository(initialSettings: AppSettings) {

    private var currentSettings: AppSettings = initialSettings

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _degradationRecords = MutableStateFlow(
        listOf(
            DegradationRecord("1", "2024-01-15", 5000.0, 528.0, 1.2),
            DegradationRecord("2", "2024-03-20", 12000.0, 524.0, 1.9),
            DegradationRecord("3", "2024-06-10", 21000.0, 520.0, 2.7),
            DegradationRecord("4", "2024-09-05", 29000.0, 517.0, 3.2),
            DegradationRecord("5", "2024-12-01", 34520.0, 515.0, 3.6)
        )
    )
    val degradationRecords: StateFlow<List<DegradationRecord>> = _degradationRecords.asStateFlow()

    private val _chargeRecords = MutableStateFlow(
        listOf(
            ChargeRecord("1", "2024-12-20", "수원 슈퍼차저", 45.2, 16200, 35, "Supercharger"),
            ChargeRecord("2", "2024-12-18", "집 완속 충전기", 32.0, 6400, 360, "Home AC"),
            ChargeRecord("3", "2024-12-15", "강남 슈퍼차저", 50.1, 18000, 40, "Supercharger"),
            ChargeRecord("4", "2024-12-10", "판교 공공충전소", 28.5, 9100, 120, "Public DC")
        )
    )
    val chargeRecords: StateFlow<List<ChargeRecord>> = _chargeRecords.asStateFlow()

    private val _consumableItems = MutableStateFlow(
        listOf(
            ConsumableItem("1", "에어컨 캐빈 필터", "filter", "2024-05-10", 20000.0, 20000.0, 34520.0),
            ConsumableItem("2", "타이어 위치 교환", "tire", "2024-08-01", 25000.0, 15000.0, 34520.0),
            ConsumableItem("3", "브레이크 오일 테스트", "fluid", "2023-11-15", 10000.0, 40000.0, 34520.0),
            ConsumableItem("4", "와이퍼 블레이드", "wiper", "2024-04-01", 18000.0, 20000.0, 34520.0)
        )
    )
    val consumableItems: StateFlow<List<ConsumableItem>> = _consumableItems.asStateFlow()

    fun updateConfig(settings: AppSettings) {
        this.currentSettings = settings
    }

    suspend fun refreshVehicleState() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(lastUpdated = "방금 전")
    }

    suspend fun setLock(locked: Boolean) {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(isLocked = locked)
    }

    suspend fun setClimate(enabled: Boolean) {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(climateOn = enabled)
    }

    suspend fun setSentry(enabled: Boolean) {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(sentryMode = enabled)
    }

    suspend fun toggleTrunk() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(trunkOpen = !current.trunkOpen)
    }

    suspend fun toggleFrunk() {
        val current = _vehicleState.value
        _vehicleState.value = current.copy(frunkOpen = !current.frunkOpen)
    }

    suspend fun addChargeRecord(record: ChargeRecord) {
        _chargeRecords.value = listOf(record) + _chargeRecords.value
    }

    suspend fun resetConsumable(id: String) {
        val currentOdo = _vehicleState.value.odometerKm
        _consumableItems.value = _consumableItems.value.map { item ->
            if (item.id == id) {
                item.copy(
                    lastReplacedOdoKm = currentOdo,
                    lastReplacedDate = "2024-12-25"
                )
            } else {
                item
            }
        }
    }
}
