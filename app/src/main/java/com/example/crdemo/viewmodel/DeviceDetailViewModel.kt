package com.example.crdemo.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.data.model.BleData
import com.example.crdemo.data.repository.BleRepository
import com.example.crdemo.data.repository.ConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val bleRepository: BleRepository
) : ViewModel() {
    // UI State: 顯示收到的數據
    private val _latestValue = MutableStateFlow("尚未收到數據")
    val latestValue = _latestValue.asStateFlow()
    val bleData: StateFlow<BleData?> = bleRepository.dataFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 關鍵魔法
            initialValue = null
        )

    init {
        // 啟動監聽資料流
        viewModelScope.launch {
            bleRepository.dataFlow.collect { bleData ->
                if (bleData.uuid == TARGET_CHAR_UUID) {
                    // 將 ByteArray 轉為 String (假設裝置傳的是 UTF-8 字串)
                    // 如果是心率或其他數值，需在此進行 Hex 解析
                    val text = String(bleData.value, Charsets.UTF_8)
                    _latestValue.value = "收到: $text"
                }
            }
        }
    }

    // 將 Repository 的 Flow 轉換為 Compose 可觀察的 State
    val connectionState = bleRepository.connectionState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConnectionState.DISCONNECTED
        )

    fun connectToDevice(device: BluetoothDevice) {
        bleRepository.connect(device)
    }

    fun disconnect() {
        bleRepository.close()
    }

    override fun onCleared() {
        super.onCleared()
        bleRepository.close() // ViewModel 銷毀時確保斷線
    }
    fun toggleLed(isOn: Boolean) {
        bleRepository.writeLedState(isOn)
    }

    fun refreshButtonStatus() {
        bleRepository.readButtonState()
    }
}