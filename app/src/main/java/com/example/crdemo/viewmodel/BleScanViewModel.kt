package com.example.crdemo.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class BleScanViewModel : ViewModel() {
    // 使用 mutableStateListOf 讓 Compose 能監聽列表變化
    // 這裡直接使用 BluetoothDevice，但在正式專案中建議封裝成自定義的 data class 以處理權限和名稱
    private val _leDevices = mutableStateListOf<BluetoothDevice>()
    val leDevices: List<BluetoothDevice> = _leDevices

    /**
     * 對應原本 Adapter 中的 addDevice 方法
     * 檢查是否已存在，不存在則加入
     */
    fun addDevice(device: BluetoothDevice) {
        if (!_leDevices.contains(device)) {
            _leDevices.add(device)
        }
    }

    fun clearDevices() {
        _leDevices.clear()
    }
}