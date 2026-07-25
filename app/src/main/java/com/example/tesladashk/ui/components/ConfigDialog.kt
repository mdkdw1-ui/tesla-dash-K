package com.example.tesladashk.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.tesladashk.ui.theme.*
import com.example.tesladashk.viewmodel.TeslaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigDialog(viewModel: TeslaViewModel, onDismiss: () -> Unit) {
    val config by viewModel.config.collectAsState()

    var kakaoKey by remember { mutableStateOf(config.kakaoKey) }
    var supabaseUrl by remember { mutableStateOf(config.supabaseUrl) }
    var supabaseKey by remember { mutableStateOf(config.supabaseKey) }
    var ghToken by remember { mutableStateOf(config.ghToken) }
    var vehicleId by remember { mutableStateOf(config.vehicleId) }
    var ntfyTopic by remember { mutableStateOf(config.ntfyTopic) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)),
            color = CardDark
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("⚙️ 환경 설정", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)

                ConfigTextField(label = "Kakao Map JavaScript Key", value = kakaoKey) { kakaoKey = it }
                ConfigTextField(label = "Supabase URL", value = supabaseUrl) { supabaseUrl = it }
                ConfigTextField(label = "Supabase API Key", value = supabaseKey) { supabaseKey = it }
                ConfigTextField(label = "GitHub PAT (sync.js)", value = ghToken) { ghToken = it }
                ConfigTextField(label = "Tesla Vehicle ID", value = vehicleId) { vehicleId = it }
                ConfigTextField(label = "ntfy Topic", value = ntfyTopic) { ntfyTopic = it }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("취소", color = TextGray)
                    }
                    Button(
                        onClick = {
                            viewModel.saveConfig(
                                kakaoKey, supabaseUrl, supabaseKey, ghToken, vehicleId, ntfyTopic
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("저장 및 갱신", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 11.sp, color = TextGray)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = BorderGray,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite
            )
        )
    }
}
