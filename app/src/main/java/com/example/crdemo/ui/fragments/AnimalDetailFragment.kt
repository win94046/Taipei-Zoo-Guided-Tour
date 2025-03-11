package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.AnimalData
import com.example.crdemo.databinding.FragmentAnimalDetailBinding
import com.google.gson.Gson


class AnimalDetailFragment : Fragment() {

    private var _binding: FragmentAnimalDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取得 Animal JSON 並解析
        val animalJson = arguments?.getString("animal_data")
        val animal = Gson().fromJson(animalJson, AnimalData::class.java)

        // 設定 UI 顯示動物資訊
        binding.apply {
            tvAnimalName.text = "${animal.nameChinese} (${animal.nameEnglish})"
            tvAnimalLatinName.text = "學名: ${animal.nameLatin}"
            tvAnimalPhylum.text = "門: ${animal.phylum}"
            tvAnimalClass.text = "綱: ${animal.animalClass}"
            tvAnimalOrder.text = "目: ${animal.order}"
            tvAnimalFamily.text = "科: ${animal.family}"
            tvAnimalConservation.text = "保育狀況: ${animal.conservation}"
            tvAnimalDistribution.text = "分布地區: ${animal.distribution}"
            tvAnimalHabitat.text = "棲息地: ${animal.habitat ?: "未知"}"
            tvAnimalFeature.text = "特徵: ${animal.feature ?: "無"}"
            tvAnimalBehavior.text = "行為: ${animal.behavior ?: "無"}"
            tvAnimalDiet.text = "飲食習慣: ${animal.diet ?: "無"}"
            tvAnimalCrisis.text = "危機: ${animal.crisis ?: "無"}"
            tvAnimalLocation.text = "館區位置: ${animal.location}"

            // 載入圖片
            if (!animal.pic01Url.isNullOrEmpty()) {
                Glide.with(requireContext()).load(animal.pic01Url).into(ivAnimalPic1)
            } else {
                ivAnimalPic1.visibility = View.GONE
            }

            if (!animal.pic02Url.isNullOrEmpty()) {
                Glide.with(requireContext()).load(animal.pic02Url).into(ivAnimalPic2)
            } else {
                ivAnimalPic2.visibility = View.GONE
            }

            if (!animal.pic03Url.isNullOrEmpty()) {
                Glide.with(requireContext()).load(animal.pic03Url).into(ivAnimalPic3)
            } else {
                ivAnimalPic3.visibility = View.GONE
            }

            if (!animal.pic04Url.isNullOrEmpty()) {
                Glide.with(requireContext()).load(animal.pic04Url).into(ivAnimalPic4)
            } else {
                ivAnimalPic4.visibility = View.GONE
            }

            // 設定影片按鈕
            if (!animal.videoUrl.isNullOrEmpty()) {
                btnWatchVideo.visibility = View.VISIBLE
                btnWatchVideo.setOnClickListener {
                    // 這裡可以加上跳轉到 WebView 或 Youtube 播放影片
                }
            } else {
                btnWatchVideo.visibility = View.GONE
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
