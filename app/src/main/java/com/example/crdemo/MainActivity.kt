package com.example.crdemo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crdemo.adapter.ExhibitAdapter
import com.example.crdemo.databinding.ActivityMainBinding
import com.example.crdemo.ui.fragments.ExhibitDetailFragment
import com.example.crdemo.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val zooViewModel: ZooViewModel by viewModels()
    private lateinit var exhibitAdapter: ExhibitAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()

        // 取得動物、植物與展覽資料
        zooViewModel.refreshAllData()
    }

    private fun setupRecyclerView() {
        exhibitAdapter = ExhibitAdapter { exhibitId ->
            // 當使用者點擊展覽項目時，顯示詳細資料
            showExhibitDetail(exhibitId)
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = exhibitAdapter
        }
    }

    private fun setupObservers() {
        // 監聽展覽列表
        zooViewModel.exhibits.observe(this) { exhibitList ->
            exhibitAdapter.submitList(exhibitList)
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
}

