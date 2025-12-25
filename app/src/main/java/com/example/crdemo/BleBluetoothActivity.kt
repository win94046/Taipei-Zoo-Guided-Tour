package com.example.crdemo

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.crdemo.service.BluetoothLeService
import com.example.crdemo.ui.component.DeviceConnectScreen
import com.example.crdemo.ui.component.DeviceListScreen
import com.example.crdemo.ui.theme.CRDemoTheme
import com.example.crdemo.utils.BluetoothPermissionsGate
import com.example.crdemo.viewmodel.BleScanViewModel
import com.example.crdemo.viewmodel.DeviceDetailViewModel

class BleBluetoothActivity : ComponentActivity() {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler()

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private val viewModel: BleScanViewModel by viewModels()
    private val deviceDetailViewModel: DeviceDetailViewModel by viewModels()
    private var bluetoothService : BluetoothLeService? = null
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.device?.let { device ->
                // 重點：將掃描到的裝置加入 ViewModel
                // 注意：ScanCallback 可能在背景執行緒，更新 StateList 通常是線程安全的，
                // 但如果涉及複雜邏輯，建議用 viewModelScope.launch(Dispatchers.Main) 包覆
                viewModel.addDevice(device)
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                viewModel.addDevice(result.device)
            }
        }
    }



    private fun scanLeDevice() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (!scanning) { // Stops scanning after a pre-defined scan period.
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            // 簡單的畫面切換範例
            var currentScreen by remember { mutableStateOf("LIST") }
            var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

            CRDemoTheme {
                BluetoothPermissionsGate {
                    // 這裡只是確保權限 OK，不會自動開始掃
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        // 顯示 UI
                        when (currentScreen) {
                            "LIST" -> {
                                DeviceListScreen(
                                    viewModel = viewModel, // 這裡可能是 ScanViewModel
                                    onDeviceClick = { device ->
                                        selectedDevice = device
                                        // 1. 觸發連線 (呼叫 ViewModel)
                                        deviceDetailViewModel.connectToDevice(device)
                                        // 2. 切換畫面
                                        currentScreen = "DETAIL"
                                    }
                                )
                            }
                            "DETAIL" -> {
                                selectedDevice?.let { device ->
                                    DeviceConnectScreen(
                                        device = device,
                                        viewModel = deviceDetailViewModel // 這裡顯示連線狀態的 ViewModel
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

