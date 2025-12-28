package com.example.crdemo

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.example.crdemo.utils.showToast
import com.example.crdemo.viewmodel.BleScanViewModel
import com.example.crdemo.viewmodel.DeviceDetailViewModel

class BleBluetoothActivity : ComponentActivity() {
    // 1. 改為 Lazy 初始化，避免在 onCreate 前調用導致崩潰
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    // Scanner 可能為 null (如果藍芽沒開)，所以要動態獲取
    private val bluetoothLeScanner
        get() = bluetoothAdapter?.bluetoothLeScanner

    // 2. 將 scanning 狀態改為 Compose State，讓 UI 可以觀察
    private var isScanning by mutableStateOf(false)
    private val handler = Handler(Looper.getMainLooper()) // 指定 Looper 避免 Deprecated 警告

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000
    private val viewModel: BleScanViewModel by viewModels()
    private val deviceDetailViewModel: DeviceDetailViewModel by viewModels()
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                // 傳入整個 result，而不只是 device
                viewModel.addDevice(it)
            }
        }
        @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT)
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            results?.forEach { result ->
                viewModel.addDevice(result)
            }
        }
        override fun onScanFailed(errorCode: Int) {
            // 處理掃描失敗
            isScanning = false
        }
    }



    private fun scanLeDevice() {
        // 檢查藍芽是否開啟
        if (bluetoothAdapter == null || !bluetoothAdapter!!.isEnabled) {
            showToast(context = this, "請先開啟藍芽")
            return
        }

        // 檢查權限 (Android 12+ 需要 BLUETOOTH_SCAN)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 這裡理論上應該由 PermissionGate 擋住，但做雙重檢查無妨
            return
        }
        if (!isScanning) {
            // === 開始掃描 ===
            // 10秒後自動停止
            handler.postDelayed({
                if(isScanning) { // 確保還在掃描才停止
                    isScanning = false
                    bluetoothLeScanner?.stopScan(leScanCallback)
                }
            }, SCAN_PERIOD)

            isScanning = true
            // 清空舊列表 (選擇性)
            viewModel.clearDevices()
            bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            // === 停止掃描 ===
            isScanning = false
            bluetoothLeScanner?.stopScan(leScanCallback)
            // 移除原本設定的 10秒 Runnable，避免多餘執行
            handler.removeCallbacksAndMessages(null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var currentScreen by remember { mutableStateOf("LIST") }
            var selectedDevice by remember { mutableStateOf<BluetoothDevice?>(null) }

            CRDemoTheme {
                // 假設這個 Gate 會負責處理 runtime permission
                // 當權限通過後，才會渲染內部的 content
                BluetoothPermissionsGate {


                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // 3. 加入一個浮動按鈕或頂部按鈕來觸發掃描
                    floatingActionButton = {
                        if (currentScreen == "LIST") {
                            Button(onClick = { scanLeDevice() }) {
                                Text(if (isScanning) "停止掃描" else "開始掃描")
                            }
                        }
                    }
                )
                { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding)
                    ){
                        when (currentScreen) {
                            "LIST" -> {
                                DeviceListScreen(
                                    viewModel = viewModel,
                                    onDeviceClick = { device ->
                                        // 停止掃描再連線是一個好習慣，避免干擾連線過程
                                        if (isScanning) scanLeDevice()

                                        selectedDevice = device
                                        deviceDetailViewModel.connectToDevice(device)
                                        currentScreen = "DETAIL"
                                    }
                                )
                            }
                            "DETAIL" -> {
                                selectedDevice?.let { device ->
                                    DeviceConnectScreen(
                                        device = device,
                                        viewModel = deviceDetailViewModel
                                    )
                                }
                                // 處理返回鍵邏輯通常需要 BackHandler
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ScannedDevice(
    val device: BluetoothDevice,
    val displayName: String?, // 優先顯示這個
    val address: String,
    val rssi: Int // 訊號強度 (可選)
)