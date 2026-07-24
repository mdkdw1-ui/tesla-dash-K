package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.ApiClient
import com.example.tesladashk.network.VehicleRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeslaViewModel : ViewModel() {
    private val _isGuardianActive = MutableStateFlow(false)
    val isGuardianActive: StateFlow<Boolean> = _isGuardianActive

    fun setGuardianActive(active: Boolean) {
        _isGuardianActive.value = active
    }

    fun flashHeadlights() {
        viewModelScope.launch {
            try {
                ApiClient.teslaApi.flashHeadlights(VehicleRequest("YOUR_TOKEN", "YOUR_VEHICLE_ID"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
