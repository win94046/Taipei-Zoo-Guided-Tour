package com.example.crdemo

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.crdemo.utils.BluetoothPermissionsGate

class BleBluetooth : ComponentActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10_000

    // Compose 用的掃描結果清單（去重用 address）
    private val devices = mutableStateListOf<BluetoothDeviceUi>()

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val d = result.device ?: return
            val address = d.address ?: return

            // 去重：同 address 只留一筆（可選擇更新 RSSI）
            val idx = devices.indexOfFirst { it.address == address }
            val name = d.name
            val rssi = result.rssi

            if (idx >= 0) {
                devices[idx] = devices[idx].copy(name = name, rssi = rssi)
            } else {
                devices.add(BluetoothDeviceUi(name = name, address = address, rssi = rssi))
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun scanLeDeviceToggle() {
        if (!scanning) {
            // 可選：每次開始掃描先清單清空
            devices.clear()

            handler.postDelayed({
                scanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)

            scanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 防止 Activity 結束後 handler 還有排程
        handler.removeCallbacksAndMessages(null)
        // 防呆停掃
        if (scanning) {
            try {
                bluetoothLeScanner.stopScan(leScanCallback)
            } catch (_: SecurityException) {
                // 沒權限就算了
            }
            scanning = false
        }
    }
    @RequiresPermission(android.Manifest.permission.BLUETOOTH_SCAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ 正確初始化順序：先拿 Adapter，再拿 Scanner
        val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = btManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        setContent {
            // 你已經有 BluetoothPermissionsGate（SCAN/CONNECT）
            BluetoothPermissionsGate {
                // 這裡只是確保權限 OK，不會自動開始掃
            }
            BleScanScreen(
                devices = devices,
                onToggleScan =  {
                    // 這裡呼叫需要 BLUETOOTH_SCAN 權限的方法
                    scanLeDeviceToggle()
                },
                isScanning = { scanning }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BleScanScreen(
    devices: List<BluetoothDeviceUi>,
    onToggleScan: () -> Unit,
    isScanning: () -> Boolean
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("BLE Scanner") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val scanningNow = isScanning()

            Button(
                onClick = onToggleScan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (scanningNow) "停止掃描" else "開始掃描（10 秒）")
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "已發現：${devices.size} 台",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(devices, key = { it.address }) { d ->
                    DeviceRow(d)
                    Divider()
                }
            }
        }
    }
}

@Composable
private fun DeviceRow(d: BluetoothDeviceUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 之後可點擊進入連線流程 */ }
            .padding(vertical = 10.dp)
    ) {
        Text(text = d.name ?: "Unknown Device", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(2.dp))
        Text(text = d.address, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(2.dp))
        Text(text = "RSSI: ${d.rssi}", style = MaterialTheme.typography.bodySmall)
    }


}

private data class BluetoothDeviceUi(
    val name: String?,
    val address: String,
    val rssi: Int
)
