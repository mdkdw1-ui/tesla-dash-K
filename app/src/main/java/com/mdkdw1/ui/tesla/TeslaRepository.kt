package com.mdkdw1.ui.tesla

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TeslaRepository(private val settingsManager: EncryptedSettingsManager) {

    // 차량 상태 조회
    suspend fun getVehicleState(): VehicleState = withContext(Dispatchers.IO) {
        // 실제 구현 시 settingsManager의 URL/Key로 Supabase REST/GraphQL 호출
        // 임시 실데이터 구조 반영
        VehicleState(
            statusText = "주차 중",
            batteryPercent = 78,
            totalOdometer = 42850.5,
            lastUpdated = Date(System.currentTimeMillis() - (3 * 3600 * 1000 + 24 * 60 * 1000)), // 3시간 24분 전
            frontLeftTire = 2.9,
            frontRightTire = 2.9,
            rearLeftTire = 2.8,
            rearRightTire = 2.8
        )
    }

    // 운행 및 충전 일지 조회 (1km 미만 제외 및 1% 이상 충전 자동 분류)
    suspend fun getJournalLogs(): List<JournalLogItem> = withContext(Dispatchers.IO) {
        val rawLogs = listOf(
            JournalLogItem("1", JournalType.DRIVE, "07.26", "18:20", 32.4, 142, 85, 78, 0, 38, "서울 강남구 -> 경기 성남시"),
            JournalLogItem("2", JournalType.CHARGE, "07.26", "12:10", 0.0, 0, 45, 85, 40, 45, "수원 테슬라 슈퍼차저"),
            JournalLogItem("3", JournalType.DRIVE, "07.26", "08:15", 0.4, 180, 86, 85, 0, 3, "단거리 이동 (필터링 대상)"), // 1km 미만
            JournalLogItem("4", JournalType.DRIVE, "07.25", "19:40", 18.2, 138, 72, 65, 0, 25, "경기 용인시 -> 수원시"),
            JournalLogItem("5", JournalType.DRIVE, "07.25", "08:00", 22.1, 145, 82, 72, 0, 30, "수원시 -> 서울 서초구"),
            JournalLogItem("6", JournalType.CHARGE, "07.24", "22:00", 0.0, 0, 20, 90, 70, 360, "자택 완속 충전기"),
            JournalLogItem("7", JournalType.DRIVE, "07.24", "17:30", 45.0, 155, 35, 20, 0, 52, "인천 연수구 -> 경기 수원시")
        )

        // 로직: 1km 미만 주행 기록은 제외
        rawLogs.filter { log ->
            if (log.type == JournalType.DRIVE) {
                log.distanceKm >= 1.0
            } else {
                true // 충전 기록은 유지
            }
        }
    }

    // 최근 운행일 전체 기록 요약 계산
    suspend fun getRecentDriveSummary(): DailyDriveSummary = withContext(Dispatchers.IO) {
        val logs = getJournalLogs().filter { it.type == JournalType.DRIVE }
        if (logs.isEmpty()) return@withContext DailyDriveSummary()

        val latestDateText = logs.first().dateText
        val sameDayLogs = logs.filter { it.dateText == latestDateText }

        val totalDist = sameDayLogs.sumOf { it.distanceKm }
        val totalDur = sameDayLogs.sumOf { it.durationMinutes }
        val avgEff = if (sameDayLogs.isNotEmpty()) sameDayLogs.map { it.efficiencyWhKm }.average().toInt() else 0

        DailyDriveSummary(
            dateText = latestDateText,
            totalDistanceKm = totalDist,
            totalDurationMinutes = totalDur,
            avgEfficiencyWhKm = avgEff,
            driveCount = sameDayLogs.size
        )
    }

    // 월간 리포트 (Top 5 운전시간/주행거리 분석 포함)
    suspend fun getMonthlyReport(): MonthlyReport = withContext(Dispatchers.IO) {
        MonthlyReport(
            monthYear = "2026년 7월",
            totalDistanceKm = 1240.8,
            totalDriveMinutes = 1580,
            avgEfficiencyWhKm = 144,
            totalChargePercent = 380,
            topDriveTimeDays = listOf(
                Pair("07월 15일", 185),
                Pair("07월 22일", 142),
                Pair("07월 08일", 130),
                Pair("07월 26일", 115),
                Pair("07월 03일", 98)
            ),
            topDriveDistanceDays = listOf(
                Pair("07월 15일", 168.5),
                Pair("07월 22일", 124.0),
                Pair("07월 08일", 105.2),
                Pair("07월 26일", 88.4),
                Pair("07월 03일", 72.1)
            )
        )
    }

    // 배터리 열화 및 주행가능 거리 (최근 50개 자료)
    suspend fun getRecent50BatteryRecords(): List<BatteryRecord> = withContext(Dispatchers.IO) {
        val list = mutableListOf<BatteryRecord>()
        val dateFormat = SimpleDateFormat("MM.dd", Locale.getDefault())
        val baseTime = System.currentTimeMillis()

        // 50개의 샘플 트렌드 데이터 생성
        for (i in 49 downTo 0) {
            val date = Date(baseTime - (i * 86400000L))
            val dateStr = dateFormat.format(date)
            // 슬로우 degradation 계산 시뮬레이션
            val range = 495.0 - (49 - i) * 0.15 + (Math.random() * 2.0 - 1.0)
            val degRate = ((505.0 - range) / 505.0) * 100.0

            list.add(
                BatteryRecord(
                    dateText = dateStr,
                    batteryPercent = 100,
                    calculated100Km = String.format("%.1f", range).toDouble(),
                    degradationRate = String.format("%.2f", degRate).toDouble()
                )
            )
        }
        list
    }

    // GitHub/Supabase 데이터 동기화 API 호출
    suspend fun triggerDataSync(): Boolean = withContext(Dispatchers.IO) {
        val settings = settingsManager.loadSettings()
        if (settings.supabaseUrl.isEmpty() || settings.supabaseKey.isEmpty()) {
            return@withContext false
        }
        // my-tesla-app repo의 api/sync.js 연동 트리거 (예시 delay)
        kotlinx.coroutines.delay(1200)
        true
    }
}
