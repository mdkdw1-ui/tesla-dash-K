package com.example.tesladashk.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GuardianScreen(
    logs: List<String>,
    onSendAlert: (String, String) -> Unit
) {
    var topic by remember { mutableStateOf("tesla-dash-alerts") }
    var message by remember { mutableStateOf("Test Guardian Alert") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Guardian Settings", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("Ntfy Topic") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Alert Message") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { onSendAlert(topic, message) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Manual Alert")
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text("Logs", style = MaterialTheme.typography.titleMedium)

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(logs) { log ->
                Text(text = log, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(4.dp))
                HorizontalDivider()
            }
        }
    }
}
