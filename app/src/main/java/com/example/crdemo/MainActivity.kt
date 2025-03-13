package com.example.crdemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crdemo.adapter.AnimalAdapter
import com.example.crdemo.adapter.ExhibitAdapter
import com.example.crdemo.adapter.PlantAdapter
import com.example.crdemo.databinding.ActivityMainBinding
import com.example.crdemo.ui.fragments.AnimalDetailFragment
import com.example.crdemo.ui.fragments.ExhibitDetailFragment
import com.example.crdemo.viewmodel.ZooViewModel
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
        // 設定左上角的 Navigation Icon (類似漢堡選單)
        supportActionBar?.setDisplayHomeAsUpEnabled(false) // 關閉自動返回箭頭
        binding.toolbar?.setNavigationIcon(R.drawable.menu_summary_button_icon) // 你需要準備對應圖檔
        binding.toolbar?.setNavigationOnClickListener {
            // 點擊左上角時，顯示選單
            showPopupMenu(it)
        }

        setupAdapters()
        setupObservers()

        // 取得動物、植物與展覽資料
        zooViewModel.refreshAllData()
    }

    /** 初始化三種 Adapter，並預設先顯示動物 Adapter（可自由調整預設顯示） */
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
        binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        binding.recyclerView.adapter = animalAdapter
    }

    /** 這裡設定三個 LiveData 觀察者 */
    private fun setupObservers() {
        // 監聽展覽列表
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

    /** 顯示 PopupMenu，讓使用者選擇要顯示的清單 */
    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        // 新增三個選單項目
        popupMenu.menu.add(0, 0, 0, "展覽區域")
        popupMenu.menu.add(0, 1, 0, "動物總攬")
        popupMenu.menu.add(0, 2, 0, "植物總攬")

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> {
                    // 切換 RecyclerView 顯示 exhibitAdapter
                    binding.recyclerView.adapter = exhibitAdapter
                    true
                }
                1 -> {
                    // 切換 RecyclerView 顯示 animalAdapter
                    binding.recyclerView.adapter = animalAdapter
                    true
                }
                2 -> {
                    // 切換 RecyclerView 顯示 plantAdapter
                    binding.recyclerView.adapter = plantAdapter
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    // ======= 以下為詳細資料跳轉或顯示 =======
    private fun showExhibitDetail(exhibitId: Int) {
        val fragment = ExhibitDetailFragment()
        val bundle = Bundle().apply {
            putInt("exhibit_id", exhibitId)
        }
        fragment.arguments = bundle
        fragment.show(supportFragmentManager, "ExhibitDetailFragment")
    }

    private fun showAnimalDetail(animalId: Int) {
        // 顯示或跳轉到動物的詳細資料

    }

    private fun showPlantDetail(plantId: Int) {
        // 顯示或跳轉到植物的詳細資料
    }
}


