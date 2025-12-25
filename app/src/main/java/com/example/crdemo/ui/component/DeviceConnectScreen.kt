package com.example.crdemo.ui.component

import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.crdemo.data.repository.ConnectionState
import com.example.crdemo.viewmodel.DeviceDetailViewModel

@Composable
fun DeviceConnectScreen(
    device: BluetoothDevice, // 從掃描頁面傳來的裝置
    viewModel: DeviceDetailViewModel
) {
    // 1. 收集狀態流
    val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "裝置: ${device.address}", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 2. 根據狀態顯示不同 UI
        when (connectionState) {
            ConnectionState.DISCONNECTED -> {
                Button(onClick = { viewModel.connectToDevice(device) }) {
                    Text("連線 (Connect)")
                }
            }
            ConnectionState.CONNECTING, ConnectionState.DISCOVERING_SERVICES -> {
                CircularProgressIndicator()
                Text("連線中 / 尋找服務中...")
            }
            ConnectionState.CONNECTED -> {
                Text("已連線 (Connected)", color = Color.Green)
                Button(
                    onClick = { viewModel.disconnect() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("斷線 (Disconnect)")
                }

                // 這裡通常會顯示下一步：操作特徵值 (Characteristics)
                // ServiceList(services = ...)
            }
        }
    }
}