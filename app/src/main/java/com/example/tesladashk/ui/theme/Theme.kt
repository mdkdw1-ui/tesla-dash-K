package com.example.tesladashk.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkBg = Color(0xFF0B0F17)
val CardBg = Color(0xFF131B2E)
val CardDark = Color(0xFF131B2E)
val CardDarker = Color(0xFF0D121D)
val CardBorder = Color(0xFF1E293B)
val BorderGray = Color(0xFF1E293B)
val PrimaryText = Color(0xFFF1F5F9)
val TextWhite = Color(0xFFFFFFFF)
val SubText = Color(0xFF94A3B8)
val TextGray = Color(0xFF94A3B8)
val AccentBlue = Color(0xFF2563EB)
val AccentGreen = Color(0xFF10B981)
val AccentEmerald = Color(0xFF10B981)
val AccentOrange = Color(0xFFF59E0B)
val AccentRed = Color(0xFFEF4444)
val LogGreen = Color(0xFF22C55E)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    background = DarkBg,
    surface = CardBg
)

@Composable
fun TeslaDashTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
