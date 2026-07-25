package com.example.tesladashk.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tesladashk.network.AppConfig
import com.example.tesladashk.network.DrivingTrip
import com.example.tesladashk.network.VehicleRow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object ApiClient {
    // Retrofit ApiClient 헬퍼 참조용
}

class DashboardViewModel : ViewModel() {

    private val _config = MutableStateFlow(AppConfig())
    val config: StateFlow<AppConfig> = _config

    private val _trips = MutableStateFlow<List<DrivingTrip>>(emptyList())
    val trips: StateFlow<List<DrivingTrip>> = _trips
    val allTrips: StateFlow<List<DrivingTrip>> = _trips

    private val _vehicleRows = MutableStateFlow<List<VehicleRow>>(emptyList())
    val vehicleRows: StateFlow<List<VehicleRow>> = _vehicleRows

    fun saveConfig(newConfig: AppConfig) {
        _config.value = newConfig
    }

    fun toPsi(v: Double): Int {
        return if (v > 0 && v < 10) (v * 14.5038).roundToInt() else v.roundToInt()
    }

    fun parseDrivingTime(rawVal: Any?): Int {
        if (rawVal is Number) return rawVal.toInt().coerceAtLeast(1)
        if (rawVal is String && rawVal.isNotBlank() && rawVal.uppercase() != "NULL") {
            val str = rawVal.trim()
            var hours = 0
            var mins = 0
            val hMatch = Regex("(\\d+)\\s*시간").find(str)
            if (hMatch != null) hours = hMatch.groupValues[1].toInt()
            val mMatch = Regex("(\\d+)\\s*분").find(str)
            if (mMatch != null) mins = mMatch.groupValues[1].toInt()
            if (hMatch != null || mMatch != null) return hours * 60 + mins
            val numOnly = str.toDoubleOrNull()
            if (numOnly != null) return numOnly.toInt().coerceAtLeast(1)
        }
        return 1
    }

    fun calculateMonthlyChargingStats(selYear: Int, selMonth: Int): Pair<Int, Double> {
        val monthLogs = _vehicleRows.value.filter {
            val cal = Calendar.getInstance().apply { time = parseIsoDate(it.updatedAt) }
            cal.get(Calendar.YEAR) == selYear && (cal.get(Calendar.MONTH) + 1) == selMonth
        }.sortedBy { parseIsoDate(it.updatedAt) }

        var chargeCount = 0
        var totalChargedPct = 0.0
        var inChargingSession = false

        for (i in 1 until monthLogs.size) {
            val prev = monthLogs[i - 1]
            val cur = monthLogs[i]
            val pBat = prev.batteryLevel ?: 0
            val cBat = cur.batteryLevel ?: 0

            if (cBat > pBat) {
                if (!inChargingSession) {
                    inChargingSession = true
                    chargeCount++
                }
                totalChargedPct += (cBat - pBat)
            } else if (cBat < pBat) {
                inChargingSession = false
            }
        }
        val totalChargedKwh = (totalChargedPct / 100.0) * 60.0
        return Pair(chargeCount, totalChargedKwh)
    }

    fun buildSequentialRouteHtml(tripsList: List<DrivingTrip>): String {
        if (tripsList.isEmpty()) return ""
        val sorted = tripsList.sortedBy { it.timestamp }
        val points = mutableListOf<String>()
        sorted.forEachIndexed { idx, trip ->
            if (idx == 0) points.add(trip.startDong)
            points.add(trip.endDong)
        }
        val uniquePoints = mutableListOf<String>()
        points.forEachIndexed { i, pt ->
            if (i == 0 || pt != points[i - 1]) uniquePoints.add(pt)
        }
        return uniquePoints.joinToString(" → ")
    }

    private fun parseIsoDate(dateStr: String?): Date {
        if (dateStr == null) return Date()
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(dateStr) ?: Date()
        } catch (e: Exception) { Date() }
    }
}
