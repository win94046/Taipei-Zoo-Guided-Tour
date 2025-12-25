package com.example.crdemo.viewmodel

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.crdemo.data.repository.BleRepository
import com.example.crdemo.data.repository.ConnectionState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.UUID


class DeviceDetailViewModel(
    private val bleRepository: BleRepository // 實務上通常透過 DI (如 Hilt) 注入
) : ViewModel() {

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
}