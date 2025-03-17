package com.example.crdemo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.crdemo.databinding.DialogChatBinding
import com.example.crdemo.viewmodel.ZooViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatDialogFragment : DialogFragment() {

    private lateinit var binding: DialogChatBinding
    private val zooViewModel: ZooViewModel by activityViewModels() // 🔥 使用 `activityViewModels()` 讓 ViewModel 共享

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 監聽 Gemini API 回應並更新 UI
        zooViewModel.message.observe(viewLifecycleOwner) { response ->
            binding.tvChatHistory.text = response
        }

        // 發送按鈕點擊事件
        binding.btnSend.setOnClickListener {
            val userMessage = binding.etUserInput.text.toString()
            if (userMessage.isNotEmpty()) {
                zooViewModel.postMessage(userMessage)
                binding.etUserInput.text.clear()
            }
        }

        // 關閉對話框
        binding.btnClose.setOnClickListener {
            dismiss()
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
            val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
            setLayout(width, height) }
    }
}
