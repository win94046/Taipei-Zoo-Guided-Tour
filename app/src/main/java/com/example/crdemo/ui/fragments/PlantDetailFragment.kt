package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.databinding.FragmentPlantDetailBinding
import com.example.crdemo.utils.toSecureUrl
import com.example.crdemo.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PlantDetailFragment : DialogFragment() {

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
//        viewModel.getPlantById(plantId).observe(viewLifecycleOwner) { plant ->
//            plant?.let {
//                binding.tvPlantName.text = plant.nameChinese
//                binding.tvPlantLatinName.showOrGone("學名: ${plant.nameLatin}")
//                binding.tvPlantFamilyGenus.showOrGone("科: ${plant.family} | 屬: ${plant.genus}")
//                binding.tvPlantBrief.showOrGone(plant.brief)
//                binding.tvPlantFeature.showOrGone(plant.feature)
//                binding.tvPlantFunctionApplication.showOrGone(plant.functionAndApplication)
//
//                // 使用 Glide 載入圖片
//                if (!plant.imageUrl.isNullOrEmpty()) {
//                    Glide.with(requireContext()).load(plant.imageUrl.toSecureUrl()).into(binding.ivPlantImage)
//                } else {
//                    binding.ivPlantImage.visibility = View.GONE
//                }
//                binding.btnClose.setOnClickListener { dismiss() }
//
//            }
//        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            setLayout(width, height) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
