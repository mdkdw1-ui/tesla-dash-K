package com.mdkdw1.ui.tesla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel(
    private val settingsManager: EncryptedSettingsManager,
    private val repository: TeslaRepository
) : ViewModel() {

    val vehicleState: StateFlow<VehicleState> = repository.vehicleState

    private val _settings = MutableStateFlow(settingsManager.loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _currentMainTab = MutableStateFlow(0)
    val currentMainTab: StateFlow<Int> = _currentMainTab.asStateFlow()

    val degradationRecords: StateFlow<List<DegradationRecord>> = repository.degradationRecords
    val chargeRecords: StateFlow<List<ChargeRecord>> = repository.chargeRecords
    val consumableItems: StateFlow<List<ConsumableItem>> = repository.consumableItems

    fun selectTab(tabIndex: Int) {
        _currentMainTab.value = tabIndex
    }

    fun saveSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            settingsManager.saveSettings(newSettings)
            _settings.value = newSettings
            repository.updateConfig(newSettings)
        }
    }

    fun refreshState() {
        viewModelScope.launch {
            repository.refreshVehicleState()
        }
    }

    fun toggleLock() {
        viewModelScope.launch {
            val isLocked = vehicleState.value.isLocked
            repository.setLock(!isLocked)
        }
    }

    fun toggleClimate() {
        viewModelScope.launch {
            val climateOn = vehicleState.value.climateOn
            repository.setClimate(!climateOn)
        }
    }

    fun toggleSentry() {
        viewModelScope.launch {
            val sentry = vehicleState.value.sentryMode
            repository.setSentry(!sentry)
        }
    }

    fun toggleTrunk() {
        viewModelScope.launch {
            repository.toggleTrunk()
        }
    }

    fun toggleFrunk() {
        viewModelScope.launch {
            repository.toggleFrunk()
        }
    }

    fun addChargeRecord(date: String, location: String, kwh: Double, cost: Int, duration: Int, type: String) {
        viewModelScope.launch {
            val record = ChargeRecord(
                id = System.currentTimeMillis().toString(),
                date = date,
                location = location,
                addedKwh = kwh,
                costKrw = cost,
                durationMinutes = duration,
                chargeType = type
            )
            repository.addChargeRecord(record)
        }
    }

    fun resetConsumableItem(id: String) {
        viewModelScope.launch {
            repository.resetConsumable(id)
        }
    }
}

class TeslaViewModelFactory(
    private val settingsManager: EncryptedSettingsManager,
    private val repository: TeslaRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeslaViewModel::class.java)) {
            return TeslaViewModel(settingsManager, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
