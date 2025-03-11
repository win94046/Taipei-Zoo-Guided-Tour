package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.PlantData
import com.example.crdemo.databinding.FragmentPlantDetailBinding

import com.google.gson.Gson

class PlantDetailFragment : Fragment() {

    private lateinit var binding: FragmentPlantDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取得 Plant JSON 並解析
        val plantJson = arguments?.getString("plant_data")
        val plant = Gson().fromJson(plantJson, PlantData::class.java)

        // 設定 UI 顯示植物資訊
        binding.apply {
            tvPlantName.text = "${plant.nameChinese} (${plant.nameEnglish})"
            tvPlantLatinName.text = "學名: ${plant.nameLatin}"
            tvPlantFamilyGenus.text = "科: ${plant.family} | 屬: ${plant.genus}"
            tvPlantBrief.text = plant.brief
            tvPlantFeature.text = plant.feature
            tvPlantFunctionApplication.text = plant.functionApplication

            // 載入圖片
            if (!plant.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext()).load(plant.imageUrl).into(ivPlantImage)
            } else {
                ivPlantImage.visibility = View.GONE
            }
        }
    }
}
