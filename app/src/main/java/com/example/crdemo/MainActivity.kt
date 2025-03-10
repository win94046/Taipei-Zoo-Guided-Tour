package com.example.crdemo

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crdemo.adapter.ExhibitAdapter
import com.example.crdemo.databinding.ActivityMainBinding
import com.example.crdemo.viewmodel.ZooViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val zooViewModel: ZooViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 設定 RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 請求 API
        zooViewModel.fetchZooData()

        // 監聽 API 回應並更新 UI
        zooViewModel.exhibits.observe(this) { exhibitList ->
            binding.recyclerView.adapter = ExhibitAdapter(exhibitList)
        }
    }
}