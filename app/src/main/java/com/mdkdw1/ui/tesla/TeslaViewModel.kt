package com.mdkdw1/ui/tesla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = EncryptedSettingsManager(application)
    private val repository = TeslaRepository()

    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _batteryDegradation = MutableStateFlow<List<DegradationRecord>>(emptyList())
    val batteryDegradation: StateFlow<List<DegradationRecord>> = _batteryDegradation.asStateFlow()

    private val _chargingHistory = MutableStateFlow<List<ChargeRecord>>(emptyList())
    val chargingHistory: StateFlow<List<ChargeRecord>> = _chargingHistory.asStateFlow()

    private val _consumables = MutableStateFlow<List<ConsumableItem>>(emptyList())
    val consumables: StateFlow<List<ConsumableItem>> = _consumables.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadSettings()
        refreshAllData()
    }

    fun loadSettings() {
        val loaded = settingsManager.loadSettings()
        _settings.value = loaded
    }

    fun saveSettings(newSettings: AppSettings) {
        settingsManager.saveSettings(newSettings)
        _settings.value = newSettings
        refreshAllData()
    }

    fun refreshAllData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                _vehicleState.value = repository.fetchVehicleState(_settings.value)
                _batteryDegradation.value = repository.fetchBatteryDegradation()
                _chargingHistory.value = repository.fetchChargingHistory()
                _consumables.value = repository.fetchConsumables()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun toggleDoorLock() {
        viewModelScope.launch {
            _vehicleState.value = repository.toggleDoorLock(_vehicleState.value)
        }
    }

    fun toggleClimate() {
        viewModelScope.launch {
            _vehicleState.value = repository.toggleClimate(_vehicleState.value)
        }
    }

    fun toggleSentry() {
        viewModelScope.launch {
            _vehicleState.value = repository.toggleSentry(_vehicleState.value)
        }
    }

    fun toggleTrunk() {
        viewModelScope.launch {
            _vehicleState.value = repository.toggleTrunk(_vehicleState.value)
        }
    }

    fun toggleFrunk() {
        viewModelScope.launch {
            _vehicleState.value = repository.toggleFrunk(_vehicleState.value)
        }
    }

    fun addChargeRecord(record: ChargeRecord) {
        viewModelScope.launch {
            _chargingHistory.value = repository.addChargeRecord(record)
        }
    }

    fun updateConsumable(item: ConsumableItem) {
        viewModelScope.launch {
            _consumables.value = repository.updateConsumable(item)
        }
    }
}
