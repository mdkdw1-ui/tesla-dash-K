package com.mdkdw1.ui.tesla

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.ZoneId

@Serializable
data class DrivingRow(
    val id: String,
    val user_uid: String? = null,
    val vehicle_id: String? = null,
    val move_km: Double? = null,
    val use_battery: Double? = null,
    val driving_time: Long? = null,
    val start_address: String? = null,
    val end_address: String? = null,
    val location_list: List<LocationPoint> = emptyList(),
    val created_at: String? = null
)

@Serializable
data class LocationPoint(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

enum class RoutePeriod { DAY, WEEK, MONTH, QUARTER, HALF_YEAR, YEAR, CUSTOM }

data class RouteRange(val startIso: String, val endIso: String)

class RouteHistoryRepository(private val supabase: SupabaseClient) {

    suspend fun fetchRoutes(range: RouteRange): List<DrivingRow> {
        return supabase.from("driving").select(
            columns = Columns.raw("""
                id,
                user_uid,
                vehicle_id,
                move_km,
                use_battery,
                driving_time,
                start_address,
                end_address,
                location_list,
                created_at
            """.trimIndent())
        ) {
            filter {
                gte("created_at", range.startIso)
                lte("created_at", range.endIso)
            }
            order(column = "created_at", order = Order.ASCENDING)
        }.decodeList<DrivingRow>()
    }

    fun buildRange(period: RoutePeriod, anchor: Instant = Instant.now()): RouteRange {
        val zone = ZoneId.systemDefault()
        val zdt = anchor.atZone(zone)
        val start = when (period) {
            RoutePeriod.DAY -> zdt.toLocalDate().atStartOfDay(zone)
            RoutePeriod.WEEK -> zdt.toLocalDate().minusDays(6).atStartOfDay(zone)
            RoutePeriod.MONTH -> zdt.toLocalDate().withDayOfMonth(1).atStartOfDay(zone)
            RoutePeriod.QUARTER -> zdt.toLocalDate().withDayOfMonth(1).minusMonths(((zdt.monthValue - 1) % 3).toLong()).atStartOfDay(zone)
            RoutePeriod.HALF_YEAR -> zdt.toLocalDate().withDayOfMonth(1).minusMonths(((zdt.monthValue - 1) % 6).toLong()).atStartOfDay(zone)
            RoutePeriod.YEAR -> zdt.toLocalDate().withDayOfYear(1).atStartOfDay(zone)
            RoutePeriod.CUSTOM -> zdt.toLocalDate().atStartOfDay(zone)
        }
        return RouteRange(start.toInstant().toString(), anchor.toString())
    }
}
