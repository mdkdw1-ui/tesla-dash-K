package com.mdkdw1.ui.tesla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(
    private val repository: TeslaRepository
) : ViewModel() {

    private val _vehicleData = MutableStateFlow(VehicleData())
    val vehicleData: StateFlow<VehicleData> = _vehicleData.asStateFlow()

    private val _batteryDegradation = MutableStateFlow<List<BatteryDegradation>>(emptyList())
    val batteryDegradation: StateFlow<List<BatteryDegradation>> = _batteryDegradation.asStateFlow()

    private val _chargeRecords = MutableStateFlow<List<ChargeRecord>>(emptyList())
    val chargeRecords: StateFlow<List<ChargeRecord>> = _chargeRecords.asStateFlow()

    private val _consumableItems = MutableStateFlow<List<ConsumableItem>>(emptyList())
    val consumableItems: StateFlow<List<ConsumableItem>> = _consumableItems.asStateFlow()

    private val _dailyTrips = MutableStateFlow<List<DailyTrip>>(emptyList())
    val dailyTrips: StateFlow<List<DailyTrip>> = _dailyTrips.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refreshAllData()
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _vehicleData.value = repository.fetchVehicleData()
                _batteryDegradation.value = repository.fetchBatteryDegradation()
                _chargeRecords.value = repository.fetchChargeRecords()
                _consumableItems.value = repository.fetchConsumableItems()
                _dailyTrips.value = repository.fetchDailyTrips()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchConsumableItems() {
        viewModelScope.launch {
            try {
                _consumableItems.value = repository.fetchConsumableItems()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchDailyTrips() {
        viewModelScope.launch {
            try {
                _dailyTrips.value = repository.fetchDailyTrips()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendVehicleCommand(command: String) {
        viewModelScope.launch {
            try {
                repository.sendCommand(command)
                _vehicleData.value = repository.fetchVehicleData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
