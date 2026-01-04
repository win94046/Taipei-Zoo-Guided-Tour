package com.example.crdemo.ui.component

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.crdemo.data.repository.ConnectionState
import com.example.crdemo.data.repository.LbsUuid
import com.example.crdemo.viewmodel.DeviceDetailViewModel

@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun DeviceConnectScreen(
    device: BluetoothDevice, // 從掃描頁面傳來的裝置
    viewModel: DeviceDetailViewModel
) {
    // 1. 收集狀態流
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
    val bleData by viewModel.bleData.collectAsStateWithLifecycle()

    // 2. 解析按鈕狀態邏輯 (依賴 UUID 判斷)
    // 這裡使用 remember 讓 UI 只有在 bleData 變更時才重新計算
    val isButtonPressed = remember(bleData) {
        val incomingUuid = bleData?.uuid
        val incomingBytes = bleData?.value
        val expectedUuid = LbsUuid.BUTTON_CHAR_UUID

        // --- 開始 Log 除錯 ---
        if (bleData != null) {
            Log.d("BleDebug", "========== UI 收到新數據 ==========")
            Log.d("BleDebug", "1. 目標 UUID: $expectedUuid")
            Log.d("BleDebug", "2. 收到 UUID: $incomingUuid")

            // 使用 contentToString() 把 ByteArray 印成漂亮的陣列格式，如 [1] 或 [0]
            Log.d("BleDebug", "3. 收到 Bytes: ${incomingBytes?.contentToString()}")

            // 檢查 UUID 是否相同
            val isUuidMatch = incomingUuid == expectedUuid
            Log.d("BleDebug", "4. UUID 是否匹配: $isUuidMatch")

            // 檢查數值是否為 1
            // 注意：0x01.toByte() 等於整數 1
            val firstByte = incomingBytes?.getOrNull(0)
            val isValueMatch = firstByte == 0x01.toByte()
            Log.d("BleDebug", "5. 第一個 Byte 是否為 01: $isValueMatch (實際值: $firstByte)")

            Log.d("BleDebug", "===================================")

            // 回傳計算結果
            isUuidMatch && isValueMatch
        } else {
            // 如果 bleData 是 null，代表還沒收到任何資料
            Log.d("BleDebug", "UI 重組: bleData 為 null (尚未收到數據)")
            false
        }
    }


    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // 元件間距
    ) {
        // --- 區塊 A: 裝置資訊與狀態 ---
        DeviceHeaderCard(device = device, connectionState = connectionState)

        // --- 區塊 B: 主要操作面板 (僅在連線時顯示) ---
        if (connectionState == ConnectionState.CONNECTED) {

            // 1. LED 控制區塊
            LedControlCard(
                onSwitchChanged = { isOn -> viewModel.toggleLed(isOn) }
            )

            // 2. 按鈕狀態監控區塊
            ButtonStatusCard(
                isPressed = isButtonPressed,
                onRefresh = { viewModel.refreshButtonStatus() }
            )

            Spacer(modifier = Modifier.weight(1f)) // 推到底部

            // 3. 斷線按鈕
            Button(
                onClick = { viewModel.disconnect() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("斷開連線 (Disconnect)")
            }
        } else if (connectionState == ConnectionState.DISCONNECTED) {
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { viewModel.connectToDevice(device) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("重新連線 (Connect)")
            }
        } else {
            // 連線中...
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator()
            Text("正在建立連線...", color = Color.Gray)
        }
    }
}

// --- 以下為拆分出來的 UI 元件 (Components) ---
@androidx.annotation.RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
@Composable
fun DeviceHeaderCard(device: BluetoothDevice, connectionState: ConnectionState) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = device.name ?: "Unknown Device",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 狀態標籤
            val (statusText, statusColor) = when (connectionState) {
                ConnectionState.CONNECTED -> "已連線" to Color(0xFF4CAF50) // Green
                ConnectionState.CONNECTING, ConnectionState.DISCOVERING_SERVICES -> "連線中..." to Color(0xFFFF9800) // Orange
                ConnectionState.DISCONNECTED -> "未連線" to Color.Gray
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = statusText, color = statusColor, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun LedControlCard(onSwitchChanged: (Boolean) -> Unit) {
    // 因為我們沒有從裝置讀回 LED 狀態，這裡用一個本地狀態來模擬開關顯示
    // 實務上最好是讀取裝置狀態後同步，這裡簡化為操作即切換
    var isLedOn by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("LED 控制 (Write)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = if (isLedOn) "狀態: 開啟 (ON)" else "狀態: 關閉 (OFF)")

                Switch(
                    checked = isLedOn,
                    onCheckedChange = { checked ->
                        isLedOn = checked
                        onSwitchChanged(checked) // 觸發 ViewModel 寫入
                    }
                )
            }

            // 補充：如果不喜歡 Switch，也可以用兩個按鈕
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        isLedOn = true
                        onSwitchChanged(true)
                    },
                    enabled = !isLedOn,
                    modifier = Modifier.weight(1f)
                ) { Text("開燈") }

                OutlinedButton(
                    onClick = {
                        isLedOn = false
                        onSwitchChanged(false)
                    },
                    enabled = isLedOn,
                    modifier = Modifier.weight(1f)
                ) { Text("關燈") }
            }
        }
    }
}

@Composable
fun ButtonStatusCard(isPressed: Boolean, onRefresh: () -> Unit) {
    // 增加一點狀態改變的顏色動畫
    val statusColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFFFF5252) else Color.LightGray,
        label = "ButtonColor"
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("按鈕狀態 (Read/Notify)", style = MaterialTheme.typography.titleMedium)
                // 重新整理按鈕
                IconButton(onClick = onRefresh) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "讀取狀態")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 視覺化顯示按鈕狀態
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                        .border(1.dp, Color.Gray, CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isPressed) "PRESSED (按下)" else "RELEASED (放開)",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}