package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.crdemo.databinding.FragmentExhibitDetailBinding
import com.example.crdemo.utils.toSecureUrl
import com.example.crdemo.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExhibitDetailFragment : DialogFragment() {

    private var _binding: FragmentExhibitDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ZooViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExhibitDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exhibitId = arguments?.getInt("exhibit_id") ?: return
        viewModel.getExhibitById(exhibitId).observe(viewLifecycleOwner) { exhibit ->
            exhibit?.let {
                binding.tvExhibitName.text = it.e_name
                binding.tvExhibitCategory.text = "類別: ${it.e_category}"
                binding.tvExhibitInfo.text = it.e_info
                binding.tvExhibitMemo.text = "備註: ${it.e_memo}"

                Glide.with(this)
                    .load(it.e_pic_url)
                    .into(binding.ivExhibitImage)

                binding.btnClose.setOnClickListener { dismiss() }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


