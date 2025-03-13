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
import com.example.crdemo.data.model.PlantDataTable
import com.example.crdemo.utils.toSecureUrl

class PlantAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<PlantDataTable, PlantAdapter.PlantViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = getItem(position)
        holder.bind(plant)
    }

    inner class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgPlant: ImageView = itemView.findViewById(R.id.imgPlant)
        private val tvPlantName: TextView = itemView.findViewById(R.id.tvPlantName)
        private val tvPlantLocation: TextView = itemView.findViewById(R.id.tvPlantLocation)
        private val tvPlantAlsoKnown: TextView = itemView.findViewById(R.id.tvPlantAlsoKnown)
        fun bind(plant: PlantDataTable) {
            // 植物名稱
            tvPlantName.text = plant.nameChinese
            tvPlantAlsoKnown.text = plant.alsoKnown
            // 植物特色，或者可顯示 brief/functionAndApplication 等欄位
            tvPlantLocation.text = plant.location

            // 如果有圖片連結，就用 Glide or Coil 之類載入
            Glide.with(itemView.context)
                .load(plant.imageUrl?.toSecureUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imgPlant)

            // 點擊整個 item，回傳植物 id
            itemView.setOnClickListener {
                onItemClick(plant.id)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PlantDataTable>() {
        override fun areItemsTheSame(oldItem: PlantDataTable, newItem: PlantDataTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlantDataTable, newItem: PlantDataTable): Boolean {
            return oldItem == newItem
        }
    }
}
