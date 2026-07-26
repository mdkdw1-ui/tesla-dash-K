package com.mdkdw1.ui.tesla

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TeslaDashTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D0D0D)
                ) {
                    TeslaDashboardScreen()
                }
            }
        }
    }
}

@Composable
fun TeslaDashboardScreen() {
    var speed by remember { mutableFloatStateOf(0f) }
    var batteryPercent by remember { mutableIntStateOf(88) }
    var gearState by remember { mutableStateOf("D") }

    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "SpeedGaugeAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("P", "R", "N", "D").forEach { gear ->
                    Text(
                        text = gear,
                        color = if (gearState == gear) Color.White else Color(0xFF555555),
                        fontSize = 22.sp,
                        fontWeight = if (gearState == gear) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$batteryPercent%",
                    color = if (batteryPercent > 20) Color(0xFF4CAF50) else Color(0xFFE53935),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF333333))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(fraction = batteryPercent / 100f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (batteryPercent > 20) Color(0xFF4CAF50) else Color(0xFFE53935))
                    )
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A1A1A))
                .border(4.dp, Color(0xFF333333), CircleShape)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = animatedSpeed.toInt().toString(),
                    color = Color.White,
                    fontSize = 84.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "KM/H",
                    color = Color(0xFF888888),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (speed >= 10f) speed -= 10f else speed = 0f
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C))
                ) {
                    Text("BRAKE", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (gearState == "D") speed += 10f
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0066FF))
                ) {
                    Text("DRIVE", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("P", "R", "N", "D").forEach { gear ->
                    Button(
                        onClick = {
                            gearState = gear
                            if (gear == "P") speed = 0f
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (gearState == gear) Color(0xFF444444) else Color(0xFF1E1E1E)
                        )
                    ) {
                        Text(gear, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TeslaDashTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        background = Color(0xFF0D0D0D),
        surface = Color(0xFF1A1A1A),
        primary = Color(0xFF0066FF)
    )

    MaterialTheme(
        colorScheme = darkColors,
        content = content
    )
}
