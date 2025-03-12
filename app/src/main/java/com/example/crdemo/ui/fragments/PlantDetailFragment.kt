package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.databinding.FragmentPlantDetailBinding
import com.example.crdemo.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ZooViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取得 plantId
        val plantId = arguments?.getInt("plant_id") ?: -1
        if (plantId == -1) {
            Toast.makeText(requireContext(), "找不到植物資訊", Toast.LENGTH_SHORT).show()
            return
        }

        // 監聽 ViewModel 的 LiveData，當數據變更時自動更新 UI
        viewModel.getPlantById(plantId).observe(viewLifecycleOwner) { plant ->
            plant?.let { updateUI(it) }
        }
    }

    /**
     * 更新 UI 顯示植物資訊
     */
    private fun updateUI(plant: PlantDataTable) {
        binding.apply {
            tvPlantName.text = plant.nameChinese
            tvPlantLatinName.text = "學名: ${plant.nameLatin}"
            tvPlantFamilyGenus.text = "科: ${plant.family} | 屬: ${plant.genus}"
            tvPlantBrief.text = plant.brief
            tvPlantFeature.text = plant.feature
            tvPlantFunctionApplication.text = plant.functionAndApplication

            // 使用 Glide 載入圖片
            if (!plant.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext()).load(plant.imageUrl).into(ivPlantImage)
            } else {
                ivPlantImage.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
