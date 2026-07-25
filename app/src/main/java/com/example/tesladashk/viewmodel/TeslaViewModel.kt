package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.DrivingTrip
import com.example.tesladashk.network.SupabaseRepository
import com.example.tesladashk.network.VehicleRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeslaViewModel : ViewModel() {

    private val _vehicleRows = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleRows: StateFlow<List<VehicleRow>> = _vehicleRows.asStateFlow()

    private val _trips = MutableStateFlow<List<DrivingTrip>>(emptyList())
    val trips: StateFlow<List<DrivingTrip>> = _trips.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun fetchData(baseUrl: String, apiKey: String, userUid: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Vehicle 데이터 수신
            val vResult = SupabaseRepository.fetchVehicleData(baseUrl, apiKey, userUid)
            vResult.onSuccess { list ->
                _vehicleRows.value = list.map { dto ->
                    VehicleRow(
                        state = dto.state ?: "offline",
                        batteryLevel = dto.batteryLevel ?: 0,
                        odometer = dto.odometer ?: 0.0,
                        tpmsFl = dto.tpmsFl,
                        tpmsFr = dto.tpmsFr,
                        tpmsRl = dto.tpmsRl,
                        tpmsRr = dto.tpmsRr
                    )
                }
            }

            // 2. Driving 데이터 수신
            val dResult = SupabaseRepository.fetchDrivingData(baseUrl, apiKey, userUid)
            dResult.onSuccess { list ->
                val sortedTrips = list.map { dto ->
                    DrivingTrip(
                        startTime = dto.createdAt ?: "-",
                        moveKM = dto.moveKm ?: 0.0,
                        startDong = dto.startAddress ?: "알 수 없음",
                        endDong = dto.endAddress ?: "알 수 없음"
                    )
                }
                _trips.value = sortedTrips
            }

            _isLoading.value = false
        }
    }
}
