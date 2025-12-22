package com.example.crdemo

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.companion.AssociationInfo
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.crdemo.utils.BluetoothPermissionsGate
import com.example.crdemo.utils.showToast
import java.io.IOException
import java.util.UUID
import java.util.concurrent.Executor

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("ServiceCast")
class bluetoothActivity : ComponentActivity() {
    companion object {
        val REQUEST_ENABLE_BT = 1
        private const val SELECT_DEVICE_REQUEST_CODE = 0
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    }
    @Volatile private var selectedDevice: BluetoothDevice? = null
    @Volatile private var acceptThread: AcceptThread? = null
    @Volatile private var connectThread: ConnectThread? = null
    private val selectedDeviceNameState = mutableStateOf("未選擇")

    val BTTag = "BluetoothTest"
    private val deviceManager: CompanionDeviceManager by lazy {
        getSystemService(COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
    }

    private val executor: Executor = Executor { it.run() }
    private var pendingIntentSender: IntentSender? = null

    private lateinit var bluetoothAdapter: BluetoothAdapter
    // Create a BroadcastReceiver for ACTION_FOUND.

    // 用 Activity Result API 取代 onActivityResult / startIntentSenderForResult
    private val chooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result: ActivityResult ->
        if (result.resultCode != RESULT_OK) return@registerForActivityResult

        val data = result.data
        val device: BluetoothDevice? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE, BluetoothDevice::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
            }

        device?.let {
            selectedDevice = it
            selectedDeviceNameState.value = it.name ?: it.address ?: "Unknown"
            Log.i(BTTag, "User selected device: ${it.name} ${it.address}")
            @Suppress("MissingPermission")
            it.createBond()
        }
    }
    val bondReceiver = object : BroadcastReceiver() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
            val prev = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1)

            if (state == BluetoothDevice.BOND_BONDED) {
                // 💥 配對成功！
                Toast.makeText(context, "配對成功: ${device?.name}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register for broadcasts when a device is discovered.
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        enableEdgeToEdge()
        setContent {
            var statusText by remember { mutableStateOf("Ready") }
            var bondedDevices by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
            var showBondedMenu by remember { mutableStateOf(false) }

            // 你已經有 BluetoothPermissionsGate（SCAN/CONNECT）
            BluetoothPermissionsGate {
                // 這裡只是確保權限 OK，不會自動開始掃
            }

            Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "CompanionDeviceManager 掃描/配對",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(text = statusText)
                    Text(text = "已選擇裝置：${selectedDeviceNameState.value}")

                    Box {
                        Button(
                            onClick = {
                                if (bluetoothAdapter.isEnabled.not()) {
                                    statusText = "請先開啟藍牙"
                                    return@Button
                                }
                                @SuppressLint("MissingPermission")
                                val bonded = bluetoothAdapter.bondedDevices.orEmpty()
                                bondedDevices = bonded.toList()
                                if (bondedDevices.isEmpty()) {
                                    statusText = "目前沒有已配對裝置"
                                    showBondedMenu = false
                                } else {
                                    statusText = "選擇已配對裝置"
                                    showBondedMenu = true
                                }
                            }
                        ) {
                            Text("選擇已配對裝置")
                        }

                        DropdownMenu(
                            expanded = showBondedMenu,
                            onDismissRequest = { showBondedMenu = false }
                        ) {
                            bondedDevices.forEach { device ->
                                val name = device.name ?: device.address ?: "Unknown"
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        selectedDevice = device
                                        selectedDeviceNameState.value = name
                                        statusText = "已選擇裝置：$name"
                                        showBondedMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (bluetoothAdapter == null) {
                                statusText = "此裝置不支援藍牙"
                                return@Button
                            }
                            if (bluetoothAdapter.isEnabled.not()) {
                                statusText = "請先開啟藍牙（設定或自行加上 enable intent）"
                                return@Button
                            }

                            statusText = "開始搜尋（系統選擇器）…"
                            startAssociation(
                                onPending = { sender ->
                                    pendingIntentSender = sender
                                    chooserLauncher.launch(
                                        IntentSenderRequest.Builder(
                                            sender
                                        ).build()
                                    )
                                },
                                onCreated = { info ->
                                    // 有些版本會走這個 callback，你可以拿 association id 等資訊
                                    statusText = "Association created: id=${info.id}"
                                    Log.i(
                                        BTTag,
                                        "Association created id=${info.id}, mac=${info.deviceMacAddress}"
                                    )
                                },
                                onFail = { err ->
                                    statusText = "搜尋失敗：$err"
                                    Log.w(BTTag, "CDM associate failed: $err")
                                }
                            )
                        }
                    ) {
                        Text("搜尋並配對裝置")
                    }
                    Button(
                        onClick = {
                            if (bluetoothAdapter.isEnabled.not()) {
                                statusText = "請先開啟藍牙"
                                return@Button
                            }
                            statusText = "🟢 開始等待連線（Server accept）..."
                            startAccept(statusTextSetter = { statusText = it })
                        }
                    ) { Text("開始接收藍牙連線（Server）") }

                    Button(
                        onClick = {
                            if (bluetoothAdapter.isEnabled.not()) {
                                statusText = "請先開啟藍牙"
                                return@Button
                            }
                            val d = selectedDevice
                            if (d == null) {
                                statusText = "請先『搜尋並配對裝置』選一台裝置"
                                return@Button
                            }
                            statusText = "🔵 嘗試連線 ${d.name ?: d.address}（Client connect）..."
                            startConnect(d, statusTextSetter = { statusText = it })
                        }
                    ) { Text("主動連線（Client）") }
                }
            }
        }
        registerReceiver(bondReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED))

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun ensureBluetoothEnabled(adapter: BluetoothAdapter?) {
        Log.i(BTTag, "adapter enabled? = ${adapter?.isEnabled}")
        if (adapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            @Suppress("DEPRECATION")
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    @SuppressLint("MissingPermission")
    private fun logBondedDevices(adapter: BluetoothAdapter?) {
        val pairedDevices: Set<BluetoothDevice> = adapter?.bondedDevices.orEmpty()
        pairedDevices.forEach { device ->
            Log.i(BTTag, "${device.name} , ${device.address}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        acceptThread?.cancel()
        connectThread?.cancel()
        unregisterReceiver(bondReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun startAssociation(
        onPending: (IntentSender) -> Unit,
        onCreated: (AssociationInfo) -> Unit,
        onFail: (CharSequence?) -> Unit
    ) {
        // 你可以依需求套 filter：
        // 1) 名稱規則（正則）
        // 2) 服務 UUID（Classic / GATT service 視設備類型）
        val deviceFilter = BluetoothDeviceFilter.Builder()
            // .setNamePattern(Pattern.compile("My device")) // 需要的話再打開
            // .addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null) // 有 UUID 再加
            .build()

        val request = AssociationRequest.Builder()
            .addDeviceFilter(deviceFilter)
            .setSingleDevice(false)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ✅ API 33+ 才有這個 overload：associate(request, executor, callback)
            deviceManager.associate(
                request,
                executor,
                object : CompanionDeviceManager.Callback() {
                    override fun onAssociationPending(intentSender: IntentSender) {
                        onPending(intentSender)
                    }

                    override fun onAssociationCreated(associationInfo: AssociationInfo) {
                        onCreated(associationInfo)
                    }

                    override fun onFailure(errorMessage: CharSequence?) {
                        onFail(errorMessage)
                    }
                }
            )
        } else {
            // ✅ API 26~32 用舊版：associate(request, callback, handler)
            @Suppress("DEPRECATION")
            deviceManager.associate(
                request,
                object : CompanionDeviceManager.Callback() {
                    override fun onDeviceFound(intentSender: IntentSender) {
                        // 舊版 callback 名稱是 onDeviceFound
                        onPending(intentSender)
                    }

                    override fun onFailure(errorMessage: CharSequence?) {
                        onFail(errorMessage)
                    }
                },
                Handler(Looper.getMainLooper())
            )
        }
    }
    private inner class AcceptThread(
        private val onStatus: (String) -> Unit
    ) : Thread()
    {

        private var serverSocket: BluetoothServerSocket? = null

        @SuppressLint("MissingPermission")
        override fun run() {
            try {
                // ✅ 這裡才建立 server socket（更好控權限與生命週期）
                serverSocket = bluetoothAdapter
                    .listenUsingInsecureRfcommWithServiceRecord("8787", MY_UUID)

                onStatus("🟢 Server listening… 等待對方連線")

                val socket = serverSocket?.accept() // blocking
                if (socket != null) {
                    showToast(this@bluetoothActivity, "✅ 連線成功（Server）：$name")
                    onStatus("✅ Server 已接受連線：${socket.remoteDevice?.name ?: socket.remoteDevice?.address}")
                    manageMyConnectedSocket(socket)
                } else {
                    onStatus("⚠️ accept() 回傳 null")
                }
            } catch (se: SecurityException) {
                Log.e(BTTag, "Server socket permission denied", se)
                logBluetoothPermissionState("AcceptThread-SecurityException")
                onStatus("❌ 權限不足：BLUETOOTH_CONNECT")
            } catch (e: IOException) {
                Log.e(BTTag, "Server accept failed", e)
                showToast(this@bluetoothActivity,"❌ Server 接收連線失敗：${e.message}")

                onStatus("❌ Server accept 失敗：${e.message}")
            } finally {
                try { serverSocket?.close() } catch (_: IOException) {}
            }
        }

        fun cancel() {
            try { serverSocket?.close() } catch (_: IOException) {}
        }
    }
    private inner class ConnectThread(
        private val device: BluetoothDevice,
        private val onStatus: (String) -> Unit
    ) : Thread() {

        private var socket: BluetoothSocket? = null

        @SuppressLint("MissingPermission")
        override fun run() {
            try {
                bluetoothAdapter.cancelDiscovery()

                socket = device.createRfcommSocketToServiceRecord(MY_UUID)
                onStatus("🔵 Client connecting…")

                socket?.connect() // blocking
                val name = device.name ?: device.address
                showToast(this@bluetoothActivity, "✅ 連線成功（Client：$name")

                onStatus("✅ Client 連線成功：${device.name ?: device.address}")
                socket?.let { manageMyConnectedSocket(it) }
            } catch (se: SecurityException) {
                Log.e(BTTag, "Client connect permission denied", se)
                onStatus("❌ 權限不足：BLUETOOTH_CONNECT")
            } catch (e: IOException) {
                Log.e(BTTag, "Client connect failed", e)
                showToast(this@bluetoothActivity,"❌ Client 接收連線失敗：${e.message}")
                onStatus("❌ Client connect 失敗：${e.message}")
                try { socket?.close() } catch (_: IOException) {}
            }
        }

        fun cancel() {
            try { socket?.close() } catch (_: IOException) {}
        }
    }
    @SuppressLint("MissingPermission")
    private fun manageMyConnectedSocket(socket: BluetoothSocket) {
        Thread {
            val input = socket.inputStream
            val output = socket.outputStream

            val buffer = ByteArray(1024)
            while (true) {
                try {
                    val bytes = input.read(buffer)
                    val message = String(buffer, 0, bytes)
                    Log.i("BT", "Received: $message")
                } catch (e: IOException) {
                    Log.e("BT", "Connection lost", e)
                    break
                }
            }
        }.start()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun startAccept(statusTextSetter: (String) -> Unit) {
        // 避免重複開
        acceptThread?.cancel()
        logBluetoothPermissionState("startAccept")
        acceptThread = AcceptThread(
            onStatus = statusTextSetter
        ).also { it.start() }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun startConnect(device: BluetoothDevice, statusTextSetter: (String) -> Unit) {
        connectThread?.cancel()
        connectThread = ConnectThread(device, onStatus = statusTextSetter).also { it.start() }
    }

    private fun logBluetoothPermissionState(action: String) {
        val sdkInt = Build.VERSION.SDK_INT
        val targetSdk = applicationInfo.targetSdkVersion
        val adapterEnabled = bluetoothAdapter.isEnabled

        if (sdkInt >= Build.VERSION_CODES.S) {
            val connectGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            val scanGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            val advertiseGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) == PackageManager.PERMISSION_GRANTED

            Log.i(
                BTTag,
                "[$action] sdk=$sdkInt targetSdk=$targetSdk adapterEnabled=$adapterEnabled " +
                        "perm(connect=$connectGranted, scan=$scanGranted, advertise=$advertiseGranted)"
            )
        } else {
            Log.i(
                BTTag,
                "[$action] sdk=$sdkInt targetSdk=$targetSdk adapterEnabled=$adapterEnabled (pre-12, permissions not runtime)"
            )
        }
    }

}
