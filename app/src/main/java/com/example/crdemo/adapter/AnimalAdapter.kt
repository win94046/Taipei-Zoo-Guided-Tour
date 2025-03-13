package com.example.crdemo.adapter

import android.util.Log
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
import com.example.crdemo.data.model.AnimalDataTable
import com.example.crdemo.utils.toSecureUrl

class AnimalAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<AnimalDataTable, AnimalAdapter.AnimalViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_animal, parent, false)
        return AnimalViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = getItem(position)
        holder.bind(animal)
    }

    inner class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgAnimal: ImageView = itemView.findViewById(R.id.imgAnimal)
        private val tvAnimalName: TextView = itemView.findViewById(R.id.tvAnimalName)
        private val tvAnimalLocation: TextView = itemView.findViewById(R.id.tvAnimalLocation)
        private val tvAnimalAlsoKnown: TextView = itemView.findViewById(R.id.tvAnimalAlsoKnown)
        fun bind(animal: AnimalDataTable) {
            tvAnimalName.text = animal.nameChinese
            tvAnimalAlsoKnown.text = animal.alsoKnown
            tvAnimalLocation.text = animal.location

            // 範例：如果 AnimalDataTable 裡有 pic01Url，可以使用 Glide 來載入
            Log.d("AnimalAdapter", "pic01Url: ${animal.pic01Url}")
            Glide.with(itemView.context)
                .load(animal.pic01Url?.toSecureUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imgAnimal)

            // 點擊整個 item，將動物的 id 回傳出去
            itemView.setOnClickListener {
                onItemClick(animal.id)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AnimalDataTable>() {
        override fun areItemsTheSame(oldItem: AnimalDataTable, newItem: AnimalDataTable): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AnimalDataTable, newItem: AnimalDataTable): Boolean {
            return oldItem == newItem
        }
    }
}
