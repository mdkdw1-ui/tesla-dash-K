package com.mdkdw1.ui.tesla

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsManager = EncryptedSettingsManager(application)
    private val repository = TeslaRepository(settingsManager)

    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _degradationList = MutableStateFlow<List<DegradationRecord>>(emptyList())
    val degradationList: StateFlow<List<DegradationRecord>> = _degradationList.asStateFlow()

    private val _chargeRecords = MutableStateFlow<List<ChargeRecord>>(emptyList())
    val chargeRecords: StateFlow<List<ChargeRecord>> = _chargeRecords.asStateFlow()

    private val _consumables = MutableStateFlow<List<ConsumableItem>>(emptyList())
    val consumables: StateFlow<List<ConsumableItem>> = _consumables.asStateFlow()

    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    private val _currentMainTab = MutableStateFlow(0)
    val currentMainTab: StateFlow<Int> = _currentMainTab.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _appSettings.value = repository.loadAppSettings()
            _vehicleState.value = repository.getVehicleState()
            _degradationList.value = repository.getDegradationHistory()
            _chargeRecords.value = repository.getChargeRecords()
            _consumables.value = repository.getConsumableItems()
        }
    }

    fun setMainTab(index: Int) {
        _currentMainTab.value = index
    }

    fun saveSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.saveAppSettings(newSettings)
            _appSettings.value = newSettings
        }
    }

    fun toggleLock() {
        _vehicleState.value = _vehicleState.value.copy(locked = !_vehicleState.value.locked)
    }

    fun toggleClimate() {
        _vehicleState.value = _vehicleState.value.copy(climateOn = !_vehicleState.value.climateOn)
    }

    fun toggleSentry() {
        _vehicleState.value = _vehicleState.value.copy(sentryMode = !_vehicleState.value.sentryMode)
    }

    fun toggleFrunk() {
        _vehicleState.value = _vehicleState.value.copy(frunkOpen = !_vehicleState.value.frunkOpen)
    }

    fun toggleTrunk() {
        _vehicleState.value = _vehicleState.value.copy(trunkOpen = !_vehicleState.value.trunkOpen)
    }
}
