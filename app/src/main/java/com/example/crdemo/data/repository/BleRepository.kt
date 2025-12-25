package com.example.crdemo.data.repository

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

// 定義連線狀態
enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, DISCOVERING_SERVICES
}

class BleRepository(private val context: Context) {

    private var bluetoothGatt: BluetoothGatt? = null
    // 定義一個資料模型來傳遞收到的數據
    data class BleData(val uuid: UUID, val value: ByteArray)
    // 使用 SharedFlow 發送數據事件 (因為數據是連續不斷的流)
    private val _dataFlow = MutableSharedFlow<BleData>(replay = 0)
    val dataFlow: SharedFlow<BleData> = _dataFlow.asSharedFlow()
    // 使用 StateFlow 取代 BroadcastReceiver 來通知 UI
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    // 實作 Callback，對應官方文件的 BluetoothGattCallback
    private val gattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connectionState.value = ConnectionState.CONNECTED

                // 連線成功後，必須立刻開始探索服務 (官方文件重點)
                _connectionState.value = ConnectionState.DISCOVERING_SERVICES
                gatt.discoverServices()

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connectionState.value = ConnectionState.DISCONNECTED
                close() // 斷線後釋放資源
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 服務探索完成，此時才可以開始讀寫特徵值
                _connectionState.value = ConnectionState.CONNECTED
                // 這裡可以將 gatt.services 存下來或發送出去
            }
        }
        // 1. 讀取回調
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                emitData(characteristic)
            }
        }

        // 2. 寫入回調 (通常用來確認寫入成功，這邊簡化略過)
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        // 3. 通知回調 (當裝置主動推播數據時觸發)
        // 注意：API 33 (Android 13) 有新的簽名，這裡使用舊版以相容大多數裝置，或需做版本判斷
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            emitData(characteristic)
        }

        private fun emitData(characteristic: BluetoothGattCharacteristic) {
            // 將資料發送給 ViewModel
            _dataFlow.tryEmit(BleData(characteristic.uuid, characteristic.value))
        }
    }
    // --- 功能方法 ---

    // A. 讀取資料
    @SuppressLint("MissingPermission")
    fun readCharacteristic(serviceUUID: UUID, charUUID: UUID) {
        val service = bluetoothGatt?.getService(serviceUUID)
        val characteristic = service?.getCharacteristic(charUUID)
        characteristic?.let {
            bluetoothGatt?.readCharacteristic(it)
        }
    }

    // B. 寫入資料
    @SuppressLint("MissingPermission")
    fun writeCharacteristic(serviceUUID: UUID, charUUID: UUID, value: ByteArray) {
        val service = bluetoothGatt?.getService(serviceUUID)
        val characteristic = service?.getCharacteristic(charUUID)
        characteristic?.let {
            // 設定寫入類型 (譬如 WRITE_TYPE_DEFAULT 或 WRITE_TYPE_NO_RESPONSE)
            it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            it.value = value
            bluetoothGatt?.writeCharacteristic(it)
        }
    }

    // C. 啟用通知 (這是最複雜的一步)
    @SuppressLint("MissingPermission")
    fun enableNotifications(serviceUUID: UUID, charUUID: UUID) {
        val service = bluetoothGatt?.getService(serviceUUID)
        val characteristic = service?.getCharacteristic(charUUID)

        characteristic?.let { char ->
            // 1. 本地開啟
            bluetoothGatt?.setCharacteristicNotification(char, true)

            // 2. 寫入 Descriptor (CCCD) 告訴遠端裝置開啟
            // 這是 BLE 標準的 UUID，固定不變
            val cccdUuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            val descriptor = char.getDescriptor(cccdUuid)

            descriptor?.let { desc ->
                desc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                bluetoothGatt?.writeDescriptor(desc)
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice) {
        _connectionState.value = ConnectionState.CONNECTING
        // 對應官方文件: device.connectGatt(this, false, gattCallback)
        // 注意：Android 12+ 建議在 transport 參數指定 TRANSPORT_LE 以加快連線
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun close() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}