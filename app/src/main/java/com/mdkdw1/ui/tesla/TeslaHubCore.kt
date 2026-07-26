package com.mdkdw1.ui.tesla

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TeslaHubCoreManager(
    private val repository: TeslaRepository
) {
    private val _vehicleState = MutableStateFlow(VehicleState())
    val vehicleState: StateFlow<VehicleState> = _vehicleState.asStateFlow()

    private val _dailyTrip = MutableStateFlow(DailyTrip())
    val dailyTrip: StateFlow<DailyTrip> = _dailyTrip.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    suspend fun refreshData() {
        _isLoading.value = true
        val stateResult = repository.fetchVehicleState()
        stateResult.getOrNull()?.let {
            _vehicleState.value = it
        }

        val tripResult = repository.fetchLatestDailyTrip()
        tripResult.getOrNull()?.let {
            _dailyTrip.value = it
        }
        _isLoading.value = false
    }
}
