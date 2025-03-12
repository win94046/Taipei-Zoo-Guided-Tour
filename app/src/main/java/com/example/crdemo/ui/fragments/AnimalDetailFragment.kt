package com.example.crdemo.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.AnimalData
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.databinding.FragmentAnimalDetailBinding
import com.example.crdemo.viewmodel.ZooViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimalDetailFragment : Fragment() {

    private var _binding: FragmentAnimalDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ZooViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取得 animalId
        val animalId = arguments?.getInt("animal_id") ?: -1
        if (animalId == -1) {
            Toast.makeText(requireContext(), "找不到動物資訊", Toast.LENGTH_SHORT).show()
            return
        }

        // 監聽 ViewModel 的 LiveData，當數據變更時自動更新 UI
        viewModel.getAnimalById(animalId).observe(viewLifecycleOwner) { animal ->
            animal?.let { updateUI(it) }
        }
    }

    /**
     * 更新 UI 顯示動物資訊
     */
    private fun updateUI(animal: AnimalDataTable) {
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
            loadAnimalImage(animal.pic01Url, ivAnimalPic1)
            loadAnimalImage(animal.pic02Url, ivAnimalPic2)
            loadAnimalImage(animal.pic03Url, ivAnimalPic3)
            loadAnimalImage(animal.pic04Url, ivAnimalPic4)

            // 設定影片按鈕
            if (!animal.videoUrl.isNullOrEmpty()) {
                btnWatchVideo.visibility = View.VISIBLE
                btnWatchVideo.setOnClickListener {
                    openVideo(animal.videoUrl)
                }
            } else {
                btnWatchVideo.visibility = View.GONE
            }
        }
    }

    /**
     * 使用 Glide 載入圖片
     */
    private fun loadAnimalImage(url: String?, imageView: ImageView) {
        if (!url.isNullOrEmpty()) {
            Glide.with(requireContext()).load(url).into(imageView)
        } else {
            imageView.visibility = View.GONE
        }
    }

    /**
     * 開啟影片（WebView 或 YouTube App）
     */
    private fun openVideo(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

