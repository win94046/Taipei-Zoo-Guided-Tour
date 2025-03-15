package com.example.crdemo.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.AnimalData
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.databinding.FragmentAnimalDetailBinding
import com.example.crdemo.utils.toSecureUrl
import com.example.crdemo.viewmodel.ZooViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnimalDetailFragment : DialogFragment() {

    private var _binding: FragmentAnimalDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ZooViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimalDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 取得 animalId
        val animalId = arguments?.getInt("animal_id")?: return
        viewModel.getAnimalById(animalId).observe(viewLifecycleOwner) { animal ->
            animal?.let {
                binding.tvAnimalName.text = "${animal.nameChinese} (${animal.nameEnglish})"
                binding.tvAnimalLatinName.showOrGone("學名: ${animal.nameLatin}")
                binding.tvAnimalPhylum.showOrGone("門: ${animal.phylum}")
                binding.tvAnimalClass.showOrGone("綱: ${animal.animalClass}")
                binding.tvAnimalOrder.showOrGone("目: ${animal.order}")
                binding.tvAnimalFamily.showOrGone("科: ${animal.family}")
                binding.tvAnimalConservation.showOrGone("保育狀況: ${animal.conservation}")
                binding.tvAnimalDistribution.showOrGone("分布地區: \n${animal.distribution}")
                binding.tvAnimalHabitat.showOrGone("棲息地: \n${animal.habitat ?: "未知"}")
                binding.tvAnimalFeature.showOrGone("特徵: \n${animal.feature ?: "無"}")
                binding.tvAnimalBehavior.showOrGone("行為: \n${animal.behavior ?: "無"}")
                binding.tvAnimalDiet.showOrGone("飲食習慣: \n${animal.diet ?: "無"}")
                binding.tvAnimalCrisis.showOrGone("危機: \n${animal.crisis ?: "無"}")
                binding.tvAnimalInfo.showOrGone("館區位置: \n${animal.location}")

                // 載入圖片
                loadAnimalImage(animal.pic01Url, binding.ivAnimalPic1)
                loadAnimalImage(animal.pic02Url, binding.ivAnimalPic2)
                loadAnimalImage(animal.pic03Url, binding.ivAnimalPic3)
                loadAnimalImage(animal.pic04Url, binding.ivAnimalPic4)

                // 設定影片按鈕
                if (!animal.videoUrl.isNullOrEmpty()) {
                    binding.btnWatchVideo.visibility = View.VISIBLE
                    binding.btnWatchVideo.setOnClickListener {
                        openVideo(animal.videoUrl)
                    }
                } else {
                    binding.btnWatchVideo.visibility = View.GONE
                }

                binding.btnClose.setOnClickListener { dismiss() }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            setLayout(width, height) }
    }

    /**
     * 使用 Glide 載入圖片
     */
    private fun loadAnimalImage(url: String?, imageView: ImageView) {
        if (!url.isNullOrEmpty()) {
            Glide.with(requireContext()).load(url.toSecureUrl()).into(imageView)
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

fun TextView.showOrGone(textValue: String) {
    val trimmed = textValue.trim()
    if (trimmed.isBlank()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        text = trimmed
    }
}