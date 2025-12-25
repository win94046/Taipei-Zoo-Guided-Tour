package com.example.crdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.data.repository.BleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

// 假設我們已知目標裝置的 Service 和 Characteristic UUID
// 在實際掃描中，這些可能來自使用者選擇
val TARGET_SERVICE_UUID = UUID.fromString("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
val TARGET_CHAR_UUID = UUID.fromString("yyyyyyyy-yyyy-yyyy-yyyy-yyyyyyyyyyyy")

class DeviceControlViewModel(
    private val bleRepository: BleRepository
) : ViewModel() {

    // UI State: 顯示收到的數據
    private val _latestValue = MutableStateFlow("尚未收到數據")
    val latestValue = _latestValue.asStateFlow()

    // UI State: 輸入框的文字
    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

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

    fun onInputChanged(text: String) {
        _inputText.value = text
    }

    fun readData() {
        bleRepository.readCharacteristic(TARGET_SERVICE_UUID, TARGET_CHAR_UUID)
    }

    fun writeData() {
        val bytes = _inputText.value.toByteArray(Charsets.UTF_8)
        bleRepository.writeCharacteristic(TARGET_SERVICE_UUID, TARGET_CHAR_UUID, bytes)
    }

    fun toggleNotifications() {
        bleRepository.enableNotifications(TARGET_SERVICE_UUID, TARGET_CHAR_UUID)
    }
}