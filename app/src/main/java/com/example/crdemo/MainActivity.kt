package com.example.crdemo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.crdemo.ui.screen.HomePage
import com.example.crdemo.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val zooViewModel: ZooViewModel by viewModels()

    companion object {
        private const val REQUEST_ENABLE_BT = 1 // 自己定義的 request code
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d("MainActivity", "Device doesn't support Bluetooth")
        }
        else{
            Log.d("MainActivity", "Device support Bluetooth")
        }
        Log.d("MainActivity", "bluetoothAdapter?.isEnabled == ${bluetoothAdapter?.isEnabled}")


        setContent {
            HomePage()
        }
//        zooViewModel.postMessage("動物園有幾種動物?")
    }
}



