package com.example.crdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crdemo.data.model.Exhibit
import com.example.crdemo.databinding.ItemExhibitBinding
import com.example.crdemo.utils.toSecureUrl


class ExhibitAdapter(private val exhibits: List<Exhibit>) :
    RecyclerView.Adapter<ExhibitAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemExhibitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(exhibit: Exhibit) {
            binding.tvExhibitName.text = exhibit.e_name
            binding.tvExhibitInfo.text = exhibit.e_info
            // 將圖片網址的http轉換為https
            val secureUrl = exhibit.e_pic_url.toSecureUrl()
            // 載入圖片
            Glide.with(binding.imgExhibit.context)
                .load(secureUrl)
                .into(binding.imgExhibit)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExhibitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(exhibits[position])
    }

    override fun getItemCount() = exhibits.size
}