package com.mdkdw1.ui.tesla

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Tailwind 스타일 단일 출처 색상 정의
val DarkBg = Color(0xFF0D0E12)
val DarkCardBg = Color(0xFF161820)
val DarkBorder = Color(0xFF262936)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8A8F9E)
val AccentRed = Color(0xFFE53935)
val AccentGreen = Color(0xFF4CAF50)
val AccentBlue = Color(0xFF2196F3)

@Composable
fun MetricCard(
    title: String,
    value: String,
    subValue: String? = null,
    modifier: Modifier = Modifier,
    valueColor: Color = TextPrimary,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkCardBg)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            if (subValue != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subValue,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    statusText: String,
    isActive: Boolean
) {
    val bgColor = if (isActive) AccentGreen.copy(alpha = 0.15f) else TextSecondary.copy(alpha = 0.15f)
    val textColor = if (isActive) AccentGreen else TextSecondary

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
