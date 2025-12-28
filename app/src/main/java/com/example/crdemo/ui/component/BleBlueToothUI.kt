package com.example.crdemo.ui.component

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.crdemo.ScannedDevice

@Composable
fun LeDeviceItem(
    scannedDevice: ScannedDevice, // 改收 Wrapper 物件
    onClick: (BluetoothDevice) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(scannedDevice.device) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顯示名稱
            Text(
                // 直接使用我們在 ViewModel 解析好的名字
                text = scannedDevice.displayName ?: "Unknown Device",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            // 顯示 MAC Address
            Text(
                text = scannedDevice.address,
                fontSize = 14.sp
            )

            // (選用) 顯示訊號強度
            Text(
                text = "RSSI: ${scannedDevice.rssi} dBm",
                fontSize = 12.sp,
                color = Color.Gray
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
            items = viewModel.scannedDevices,
            key = { device -> device.address } // 設定唯一鍵值 (MAC Address) 優化效能
        ) { device ->
            LeDeviceItem(
                scannedDevice = device,
                onClick = onDeviceClick
            )
        }
    }
}