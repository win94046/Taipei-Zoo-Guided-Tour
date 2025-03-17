package com.example.crdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.example.crdemo.viewmodel.ListType
import com.example.crdemo.viewmodel.ZooViewModel
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val zooViewModel: ZooViewModel by viewModels()

    private lateinit var exhibitAdapter: ExhibitAdapter
    private lateinit var animalAdapter: AnimalAdapter
    private lateinit var plantAdapter: PlantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 設定 Toolbar 為 ActionBar
        setSupportActionBar(binding.toolbar)

        // 漢堡按鈕
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding.toolbar.setNavigationIcon(R.drawable.menu_summary_button_icon)
        /// 點擊左上角 -> 開 / 關 Drawer
        binding.toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }

        setupAdapters()
        setupObservers()

        // 取得動物、植物與展覽資料
        zooViewModel.refreshAllData()

        // 綁定抽屜內各個 TextView 的點擊事件
        initDrawerMenuClick()

        binding.btnOpenChatDialog?.setOnClickListener {
            val dialog = ChatDialogFragment()
            dialog.show(supportFragmentManager, "ChatDialogFragment")
        }

//        zooViewModel.postMessage("動物園有幾種動物?")
    }


    /**
     * 開關抽屜
     */
    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    /**
     * 設定抽屜裡的 TextView click 事件
     */
    private fun initDrawerMenuClick() {
        binding.leftDrawer.findViewById<TextView>(R.id.tvExhibitMenu).setOnClickListener {
            zooViewModel.onMenuItemClicked(ListType.EXHIBIT)
        }
        binding.leftDrawer.findViewById<TextView>(R.id.tvAnimalMenu).setOnClickListener {
            zooViewModel.onMenuItemClicked(ListType.ANIMAL)
        }
        binding.leftDrawer.findViewById<TextView>(R.id.tvPlantMenu).setOnClickListener {
            zooViewModel.onMenuItemClicked(ListType.PLANT)
        }
    }
    //  監聽 ViewModel 來控制 UI 變更
    private fun setupAdapters() {
        exhibitAdapter = ExhibitAdapter {
            zooViewModel.onItemClicked(ListType.EXHIBIT, it)

        }
        animalAdapter = AnimalAdapter {
            zooViewModel.onItemClicked(ListType.ANIMAL, it)

        }
        plantAdapter = PlantAdapter {
            zooViewModel.onItemClicked(ListType.PLANT, it)

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
    }
    // 👀 監聽 ViewModel 來控制 UI 變更
    private fun setupObservers() {
        zooViewModel.currentAdapterType.observe(this) { adapterType ->
            when (adapterType) {
                ListType.ANIMAL -> binding.recyclerView.adapter = animalAdapter
                ListType.PLANT -> binding.recyclerView.adapter = plantAdapter
                ListType.EXHIBIT -> binding.recyclerView.adapter = exhibitAdapter
                null -> {}
            }
        }

        zooViewModel.currentList.observe(this) { list ->
            when (zooViewModel.currentAdapterType.value) {
                ListType.ANIMAL -> animalAdapter.submitList(list as List<AnimalDataTable>)
                ListType.PLANT -> plantAdapter.submitList(list as List<PlantDataTable>)
                ListType.EXHIBIT -> exhibitAdapter.submitList(list as List<ExhibitTable>)
                else -> {}
            }
        }

        zooViewModel.navigateToDetail.observe(this) { event ->
            event.getContentIfNotHandled()?.let { (type, id) ->
                when (type) {
                    ListType.EXHIBIT -> showExhibitDetail(id)
                    ListType.ANIMAL -> showAnimalDetail(id)
                    ListType.PLANT -> showPlantDetail(id)
                }
            }
        }

        // 監聽 `closeDrawerEvent`，當 ViewModel 觸發事件時關閉 Drawer
        zooViewModel.closeDrawerEvent.observe(this) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    private fun showExhibitDetail(exhibitId: Int) {
        val fragment = ExhibitDetailFragment()
        val bundle = Bundle().apply {
            putInt("exhibit_id", exhibitId)
        }
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "ExhibitDetailFragment")
    }
    private fun showAnimalDetail(animalId: Int) {
        // 建立對話框的實例
        val dialogFragment = AnimalDetailFragment()
        // 設置參數 (animal_id)
        val bundle = Bundle().apply {
            putInt("animal_id", animalId)
        }
        dialogFragment.arguments = bundle

        // 以對話框形式顯示
        dialogFragment.show(supportFragmentManager, "AnimalDetailDialog")
    }
    private fun showPlantDetail(plantId: Int) {
// 建立對話框的實例
        val dialogFragment = PlantDetailFragment()
        val bundle = Bundle().apply {
            putInt("plant_id", plantId)
        }
        dialogFragment.arguments = bundle

        // 以對話框形式顯示
        dialogFragment.show(supportFragmentManager, "PlantDetailDialog")
    }
}



