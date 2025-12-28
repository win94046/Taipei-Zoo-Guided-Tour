package com.example.crdemo.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.crdemo.ScannedDevice

class BleScanViewModel : ViewModel() {
    // 使用 mutableStateListOf 讓 Compose 能監聽列表變化
    // 這裡直接使用 BluetoothDevice，但在正式專案中建議封裝成自定義的 data class 以處理權限和名稱
    // 改存 ScannedDevice
    private val _scannedDevices = mutableStateListOf<ScannedDevice>()
    val scannedDevices: List<ScannedDevice> = _scannedDevices

    fun ByteArray.toHexString(): String = joinToString(" ") { "%02X".format(it) }

    // 輔助函式：取得裝置類型文字
    fun getDeviceTypeString(type: Int): String {
        return when (type) {
            BluetoothDevice.DEVICE_TYPE_CLASSIC -> "Classic"
            BluetoothDevice.DEVICE_TYPE_LE -> "LE (Low Energy)"
            BluetoothDevice.DEVICE_TYPE_DUAL -> "Dual (Classic + LE)"
            BluetoothDevice.DEVICE_TYPE_UNKNOWN -> "Unknown"
            else -> "N/A"
        }
    }

    /**
     * 對應原本 Adapter 中的 addDevice 方法
     * 檢查是否已存在，不存在則加入
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun addDevice(result: ScanResult) {
        val device = result.device

        // 1. 嘗試從廣播封包 (ScanRecord) 拿名稱 (最準確)
        val scanRecordName = result.scanRecord?.deviceName

        val scanRecord = result.scanRecord

        // 使用 buildString 一次性組裝所有文字
        val logOutput = buildString {
            appendLine("========================================")
            appendLine("📡 [Device Found]")
            appendLine("   ├─ MAC: ${device.address}")
            appendLine("   ├─ Name (System Cached): ${device.name}")
            appendLine("   ├─ Type: ${getDeviceTypeString(device.type)}")
            appendLine("   ├─ RSSI: ${result.rssi} dBm")
            appendLine("   └─ Timestamp: ${result.timestampNanos}")

            if (scanRecord != null) {
                appendLine("   📦 [ScanRecord / Advertisement Data]")
                appendLine("      ├─ Advertised Name: ${scanRecord.deviceName}")
                appendLine("      ├─ Tx Power: ${scanRecord.txPowerLevel}")

                // Service UUIDs
                val services = scanRecord.serviceUuids
                if (!services.isNullOrEmpty()) {
                    appendLine("      ├─ Service UUIDs:")
                    services.forEach { uuid ->
                        appendLine("      │    * $uuid")
                    }
                } else {
                    appendLine("      ├─ Service UUIDs: (Empty)")
                }

                // Manufacturer Data
                val manufacturerData = scanRecord.manufacturerSpecificData
                if (manufacturerData != null && manufacturerData.size() > 0) {
                    appendLine("      ├─ Manufacturer Data:")
                    for (i in 0 until manufacturerData.size()) {
                        val companyId = manufacturerData.keyAt(i)
                        val bytes = manufacturerData.valueAt(i)
                        appendLine("      │    * ID: $companyId (0x${Integer.toHexString(companyId)})")
                        appendLine("      │      Hex: ${bytes.toHexString()}")
                    }
                }

                // Service Data
                val serviceData = scanRecord.serviceData
                if (serviceData != null && serviceData.isNotEmpty()) {
                    appendLine("      └─ Service Data:")
                    serviceData.forEach { (uuid, bytes) ->
                        appendLine("           * UUID: $uuid")
                        appendLine("             Hex: ${bytes.toHexString()}")
                    }
                }
            } else {
                appendLine("   ⚠️ ScanRecord is NULL")
            }

            // Android 8.0+ (API 26) 進階資訊
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appendLine("   🚀 [Advanced (API 26+)]")
                appendLine("      ├─ Connectable: ${result.isConnectable}")
                appendLine("      ├─ Advertising SID: ${result.advertisingSid}")
                appendLine("      ├─ Primary PHY: ${result.primaryPhy}")
                appendLine("      └─ Periodic Interval: ${result.periodicAdvertisingInterval}")
            }
            append("========================================")
        }

        // 最後只執行一次 Log 輸出
        Log.d("BLE_DEBUG", logOutput)
        // 2. 如果沒有，再試試看 device.name
        val deviceName = device.name

        // 3. 決定最終顯示名稱
        val finalName = if (!scanRecordName.isNullOrEmpty()) {
            scanRecordName
        } else if (!deviceName.isNullOrEmpty()) {
            deviceName
        } else {
            "Unknown Device"
        }

        // 檢查是否已存在列表中 (透過 MAC Address 判斷)
        val existingDeviceIndex = _scannedDevices.indexOfFirst { it.address == device.address }

        if (existingDeviceIndex >= 0) {
            // 如果已存在，但新的掃描結果有名字 (之前可能是 Unknown)，則更新它
            if (_scannedDevices[existingDeviceIndex].displayName == "Unknown Device" && finalName != "Unknown Device") {
                _scannedDevices[existingDeviceIndex] = ScannedDevice(device, finalName, device.address, result.rssi)
            }
            // 或是你想要即時更新 RSSI，也可以在這裡更新
        } else {
            // 新裝置，加入列表
            _scannedDevices.add(ScannedDevice(device, finalName, device.address, result.rssi))
        }
    }

    fun clearDevices() {
        _scannedDevices.clear()
    }
}