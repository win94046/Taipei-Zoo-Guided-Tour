package com.example.crdemo.ui.component

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.crdemo.viewmodel.BleScanViewModel
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.lifecycle.viewmodel.compose.viewModel

@SuppressLint("MissingPermission") // 實際使用時需確保已取得 BLUETOOTH_CONNECT 權限
@Composable
fun LeDeviceItem(
    device: BluetoothDevice,
    onClick: (BluetoothDevice) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(device) } // 點擊事件
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 裝置名稱
            val deviceName = device.name
            Text(
                text = if (!deviceName.isNullOrEmpty()) deviceName else "Unknown Device",
                fontSize = 18.sp
            )

            // 裝置 MAC Address
            Text(
                text = device.address,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DeviceListScreen(
    viewModel: BleScanViewModel = viewModel(), // 獲取 ViewModel
    onDeviceClick: (BluetoothDevice) -> Unit
) {
    // LazyColumn 對應 RecyclerView/ListView
    LazyColumn {
        items(
            items = viewModel.leDevices,
            key = { device -> device.address } // 設定唯一鍵值 (MAC Address) 優化效能
        ) { device ->
            LeDeviceItem(
                device = device,
                onClick = onDeviceClick
            )
        }
    }
}