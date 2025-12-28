package com.example.crdemo.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.crdemo.viewmodel.DeviceControlViewModel
import com.example.crdemo.viewmodel.TARGET_CHAR_UUID

@Composable
fun DeviceControlScreen(
    viewModel: DeviceControlViewModel
) {
    val receivedValue by viewModel.latestValue.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "BLE 資料傳輸範例", style = MaterialTheme.typography.headlineMedium)

        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "特徵值 UUID: $TARGET_CHAR_UUID", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                // 顯示讀取到的數值
                Text(
                    text = receivedValue,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Blue
                )
            }
        }

        // 操作按鈕區
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.readData() }) {
                Text("主動讀取 (Read)")
            }
            Button(onClick = { viewModel.toggleNotifications() }) {
                Text("訂閱通知 (Notify)")
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            thickness = 1.dp,
        )

        // 寫入區
        OutlinedTextField(
            value = inputText,
            onValueChange = { viewModel.onInputChanged(it) },
            label = { Text("輸入要發送的文字") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.writeData() },
            modifier = Modifier.fillMaxWidth(),
            enabled = inputText.isNotEmpty()
        ) {
            Text("寫入 (Write)")
        }
    }
}