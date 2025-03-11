package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.crdemo.databinding.FragmentPlantDetailBinding
import com.example.crdemo.viewmodel.ZooViewModel


class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ZooViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取得傳遞的 JSON 並設定數據


        // 監聽 ViewModel 的 plantData 並更新 UI
        viewModel.plantData.observe(viewLifecycleOwner) { plant ->
            plant?.let {
                binding.tvPlantName.text = it.nameChinese
                binding.tvPlantLatinName.text = "學名: ${it.nameLatin}"
                binding.tvPlantFamilyGenus.text = "科: ${it.family} | 屬: ${it.genus}"
                binding.tvPlantBrief.text = it.brief
                binding.tvPlantFeature.text = it.feature
                binding.tvPlantFunctionApplication.text = it.functionApplication

                // 使用 Glide 加載圖片
                Glide.with(this)
                    .load(it.imageUrl)
                    .into(binding.ivPlantImage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
