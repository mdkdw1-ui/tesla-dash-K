package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tesladashk.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UiState(
    val vehicleName: String = "Tesla Model Y",
    val batteryLevel: Int = 85,
    val isLocked: Boolean = true,
    val isSentryActive: Boolean = true,
    val isLoading: Boolean = false,
    val logs: List<String> = emptyList()
)

class TeslaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun refreshState() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // API 호출 연동 처리 부분
            addLog("Refreshed vehicle data")
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun toggleLock() {
        val newStatus = !_uiState.value.isLocked
        _uiState.value = _uiState.value.copy(isLocked = newStatus)
        addLog(if (newStatus) "Vehicle Locked" else "Vehicle Unlocked")
    }

    fun sendNtfyAlert(topic: String, message: String) {
        viewModelScope.launch {
            try {
                ApiClient.ntfyApi.sendNotification(topic, message)
                addLog("Ntfy Alert Sent: $message")
            } catch (e: Exception) {
                addLog("Failed to send alert: ${e.message}")
            }
        }
    }

    private fun addLog(message: String) {
        val currentLogs = _uiState.value.logs.toMutableList()
        currentLogs.add(0, "[${System.currentTimeMillis() % 100000}] $message")
        _uiState.value = _uiState.value.copy(logs = currentLogs)
    }
}
