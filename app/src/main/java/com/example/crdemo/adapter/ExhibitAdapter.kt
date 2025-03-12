package com.example.crdemo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.crdemo.R
import com.example.crdemo.data.model.Exhibit
import com.example.crdemo.data.model.ExhibitTable
import com.example.crdemo.databinding.ItemExhibitBinding
import com.example.crdemo.utils.toSecureUrl


class ExhibitAdapter(private val onItemClick: (Int) -> Unit) :
    ListAdapter<ExhibitTable, ExhibitAdapter.ExhibitViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExhibitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_exhibit, parent, false)
        return ExhibitViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExhibitViewHolder, position: Int) {
        val exhibit = getItem(position)
        holder.bind(exhibit)
    }

    inner class ExhibitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvExhibitName: TextView = itemView.findViewById(R.id.tvExhibitName)
        private val tvExhibitInfo: TextView = itemView.findViewById(R.id.tvExhibitInfo)
        private val imgExhibit: ImageView = itemView.findViewById(R.id.imgExhibit)

        fun bind(exhibit: ExhibitTable) {
            tvExhibitName.text = exhibit.e_name
            tvExhibitInfo.text = exhibit.e_info

            // 使用 Glide 載入圖片
            Glide.with(itemView.context)
                .load(exhibit.e_pic_url.toSecureUrl())
                .placeholder(R.drawable.placeholder) // 預設圖片
                .error(R.drawable.error) // 載入失敗時顯示的圖片
                .into(imgExhibit)

            // 點擊展覽名稱，觸發點擊事件
            itemView.setOnClickListener { onItemClick(exhibit._id) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ExhibitTable>() {
        override fun areItemsTheSame(oldItem: ExhibitTable, newItem: ExhibitTable) = oldItem._id == newItem._id
        override fun areContentsTheSame(oldItem: ExhibitTable, newItem: ExhibitTable) = oldItem == newItem
    }
}

