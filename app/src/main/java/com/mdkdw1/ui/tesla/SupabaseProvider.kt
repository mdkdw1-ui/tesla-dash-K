package com.mdkdw1.ui.tesla

object SupabaseProvider {
    val url: String get() = runCatching { BuildConfig::class.java.getField("SUPABASE_URL").get(null) as String }.getOrDefault("")
    val key: String get() = runCatching { BuildConfig::class.java.getField("SUPABASE_KEY").get(null) as String }.getOrDefault("")
}
