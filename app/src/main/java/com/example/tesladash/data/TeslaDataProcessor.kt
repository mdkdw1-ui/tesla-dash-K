package com.example.tesladash.data

import com.example.tesladash.model.BatteryMetric
import com.example.tesladash.model.Trip
import com.example.tesladash.model.VehicleLog
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

object TeslaDataProcessor {

    // 1. 이달의 최장 주행 거리 TOP 5 추출 함수
    fun getMonthlyTop5(trips: List<Trip>): List<Trip> {
        return trips
            .sortedByDescending { it.moveKM }
            .take(5)
    }

    // 2. 배터리 열화율 및 100% 환산 거리 연산 함수 (과거 -> 최근 50개 기준)
    fun processBatteryMetrics(logs: List<VehicleLog>): List<BatteryMetric> {
        if (logs.isEmpty()) return emptyList()

        // 시계열 정렬 (과거 -> 최근 순으로 50개)
        val recentLogs = logs.take(50).reversed()

        return recentLogs.map { log ->
            // 마일 -> km 환산 (150 이상이면 이미 km 단위로 판단)
            val rangeKm = if (log.estBatteryRange > 150.0) {
                log.estBatteryRange
            } else {
                log.estBatteryRange * 1.60934
            }

            // 100% 환산 주행거리 계산 (잔여 배터리가 0 초과일 때)
            val fullRange = if (log.batteryLevel > 0) {
                (rangeKm / log.batteryLevel.toDouble()) * 100.0
            } else {
                430.0
            }

            // 열화율 계산 (신차 주행거리 기준 ~440km)
            val rawDegradation = (fullRange / 440.0) * 100.0
            val clampedDegradation = max(80.0, min(100.0, rawDegradation))

            // 날짜 표시용 포맷 (M/d)
            val dateLabel = try {
                val parsedDate = ZonedDateTime.parse(log.updatedAt)
                parsedDate.format(DateTimeFormatter.ofPattern("M/d"))
            } catch (e: Exception) {
                ""
            }

            BatteryMetric(
                dateLabel = dateLabel,
                fullRangeKm = fullRange.toInt(),
                degradation = String.format(Locale.US, "%.1f", clampedDegradation).toDouble()
            )
        }
    }
}
