package com.example.crdemo.data.repository

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.crdemo.data.model.BleData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// 定義連線狀態
enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, DISCOVERING_SERVICES
}

@Singleton // BLE 通常需要全域單例，避免重複連線或資源競爭
class BleRepository @Inject constructor(
    @ApplicationContext private val context: Context // Hilt 會自動幫你把 ApplicationContext 塞進來
){
    private var bluetoothGatt: BluetoothGatt? = null
    // 定義一個資料模型來傳遞收到的數據
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
            // 1. 先看 status：如果有任何錯誤，直接判死刑
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e("BLE", "發生錯誤，錯誤碼: $status")
                // 無論 newState 是什麼，只要 status 錯了，就必須斷開並釋放資源
                gatt.close()
                _connectionState.value = ConnectionState.DISCONNECTED
                return // 結束，不要往下執行
            }

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

        // A. 寫入完成的回調
        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.uuid == LbsUuid.LED_CHAR_UUID) {
                    Log.d("BleRepo", "LED 寫入成功: ${characteristic.value?.contentToString()}")
                    // 這裡可以發送一個 Event 通知 UI 寫入成功
                }
            } else {
                Log.e("BleRepo", "LED 寫入失敗 status: $status")
            }
        }

        // ==========================================================
        //  1. onCharacteristicRead (讀取回調)
        // ==========================================================

        // 【新版 API 33+】Android 13 以上會呼叫這個
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray, // <--- 這裡直接給你數據，不用去 char 裡面拿
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                handleIncomingData(characteristic.uuid, value)
            }
        }

        // 【舊版 API < 33】Android 12 以下會呼叫這個
        // @Suppress("DEPRECATION") 消除編譯器的過時警告
        @Suppress("DEPRECATION")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 舊版必須從 characteristic 物件中撈取 value
                handleIncomingData(characteristic.uuid, characteristic.value)
            }
        }

        // ==========================================================
        //  2. onCharacteristicChanged (通知/Notify 回調)
        // ==========================================================

        // 【新版 API 33+】
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray // <--- 直接拿到數據
        ) {
            handleIncomingData(characteristic.uuid, value)
        }

        // 【舊版 API < 33】
        @Suppress("DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // 舊版必須從 characteristic 物件中撈取 value
            handleIncomingData(characteristic.uuid, characteristic.value)
        }

        // ==========================================================
        //  統一處理邏輯
        // ==========================================================

        // 抽離出一個共用函式，避免邏輯重複
        private fun handleIncomingData(uuid: UUID, value: ByteArray) {
            // 1. 打印 Log 方便除錯
            // Log.d("BleRepo", "收到數據: UUID=$uuid, Hex=${value.toHexString()}")

            // 2. 判斷是否為按鈕數據 (LBS Button)
            if (uuid == LbsUuid.BUTTON_CHAR_UUID) {
                val isPressed = value.isNotEmpty() && value[0] == 0x01.toByte()
                Log.d("BleRepo", "按鈕狀態: ${if (isPressed) "按下" else "放開"}")
            }

            // 3. 發送給 ViewModel
            _dataFlow.tryEmit(BleData(uuid, value))
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

    // --- 寫入 LED 狀態 ---
    @SuppressLint("MissingPermission")
    fun writeLedState(isOn: Boolean) {
        val gatt = bluetoothGatt ?: return

        val service = gatt.getService(LbsUuid.SERVICE_UUID)
        if (service == null) {
            Log.e("BleRepo", "找不到 LBS 服務")
            return
        }

        val characteristic = service.getCharacteristic(LbsUuid.LED_CHAR_UUID)
        if (characteristic == null) {
            Log.e("BleRepo", "找不到 LED 特徵值")
            return
        }

        // 準備數據：1 byte
        val value = byteArrayOf(if (isOn) 0x01 else 0x00)

        // Android 13 (API 33) 寫法區分
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(characteristic, value, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        } else {
            // 舊版寫法
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            characteristic.value = value
            gatt.writeCharacteristic(characteristic)
        }
    }
    // --- 主動讀取按鈕狀態 ---
    @SuppressLint("MissingPermission")
    fun readButtonState() {
        Log.d("BleRepo", "主动讀取按鈕狀態, bluetoothGatt= null? : ${bluetoothGatt==null}")
        val gatt = bluetoothGatt ?: return

        val service = gatt.getService(LbsUuid.SERVICE_UUID)
        val characteristic = service?.getCharacteristic(LbsUuid.BUTTON_CHAR_UUID)

        characteristic?.let {
            gatt.readCharacteristic(it)
        }
    }

}

object LbsUuid {
    // 1. Service UUID (通常 Nordic LBS 的 Service UUID 是 1523)
    // 注意：如果連線後發現找不到服務，請確認這個 UUID 是否正確，或用 log 印出 gatt.services 檢查
    val SERVICE_UUID = UUID.fromString("00001523-1212-EFDE-1523-785FEABCD123")

    // 2. LED Characteristic (截圖上方那個)
    // 屬性: Read, Write. 用來寫入 0x01 (開燈) 或 0x00 (關燈)
    val LED_CHAR_UUID = UUID.fromString("00001525-1212-EFDE-1523-785FEABCD123")

    // 3. Button Characteristic (截圖下方那個)
    // 屬性: Read, Notify. 用來讀取按鈕狀態，建議使用 Notify
    val BUTTON_CHAR_UUID = UUID.fromString("00001524-1212-EFDE-1523-785FEABCD123")
}