package com.example.crdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crdemo.adapter.AnimalAdapter
import com.example.crdemo.adapter.ExhibitAdapter
import com.example.crdemo.adapter.PlantAdapter
import com.example.crdemo.databinding.ActivityMainBinding
import com.example.crdemo.ui.fragments.AnimalDetailFragment
import com.example.crdemo.ui.fragments.ExhibitDetailFragment
import com.example.crdemo.ui.fragments.PlantDetailFragment
import com.example.crdemo.viewmodel.ZooViewModel
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import dagger.hilt.android.AndroidEntryPoint

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
        // 展覽區域
        binding.leftDrawer?.findViewById<TextView>(R.id.tvExhibitMenu)?.setOnClickListener {
            binding.recyclerView?.adapter = exhibitAdapter
            binding.drawerLayout?.closeDrawer(GravityCompat.START) // 選完就關閉抽屜
        }
        // 動物總攬
        binding.leftDrawer?.findViewById<TextView>(R.id.tvAnimalMenu)?.setOnClickListener {
            binding.recyclerView?.adapter = animalAdapter
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        // 植物總攬
        binding.leftDrawer.findViewById<TextView>(R.id.tvPlantMenu).setOnClickListener {
            binding.recyclerView?.adapter = plantAdapter
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    // ==================== 其餘程式不變 ====================
    private fun setupAdapters() {
        exhibitAdapter = ExhibitAdapter { exhibitId ->
            showExhibitDetail(exhibitId)
        }
        animalAdapter = AnimalAdapter { animalId ->
            showAnimalDetail(animalId)
        }
        plantAdapter = PlantAdapter { plantId ->
            showPlantDetail(plantId)
        }

        // 預設先顯示「動物總攬」
        binding.recyclerView?.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recyclerView?.adapter = animalAdapter
    }
    private fun setupObservers() {
        zooViewModel.exhibits.observe(this) { exhibitList ->
            exhibitAdapter.submitList(exhibitList)
        }
        // 監聽動物列表
        zooViewModel.allAnimals.observe(this) { animalList ->
            animalAdapter.submitList(animalList)
        }
        // 監聽植物列表
        zooViewModel.allPlants.observe(this) { plantList ->
            plantAdapter.submitList(plantList)
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

//    fun useGeminiAPI(){
//        val generativeModel = GenerativeModel(
//            // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
//            modelName = "gemini-1.5-flash",
//            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
//            apiKey = BuildConfig.apiKey
//        )
//    }
}



