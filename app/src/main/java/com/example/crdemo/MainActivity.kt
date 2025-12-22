package com.example.crdemo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crdemo.BuildConfig.apiKey
import com.example.crdemo.adapter.AnimalAdapter
import com.example.crdemo.adapter.ExhibitAdapter
import com.example.crdemo.adapter.PlantAdapter
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.databinding.ActivityMainBinding
import com.example.crdemo.ui.fragments.AnimalDetailFragment
import com.example.crdemo.ui.fragments.ChatDialogFragment
import com.example.crdemo.ui.fragments.ExhibitDetailFragment
import com.example.crdemo.ui.fragments.PlantDetailFragment
import com.example.crdemo.ui.screen.HomePage
import com.example.crdemo.viewmodel.ListType
import com.example.crdemo.viewmodel.ZooViewModel
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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



