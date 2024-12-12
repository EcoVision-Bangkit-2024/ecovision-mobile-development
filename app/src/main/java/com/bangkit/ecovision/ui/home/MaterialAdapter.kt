package com.bangkit.ecovision.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.ecovision.data.response.get.Data
import com.bangkit.ecovision.databinding.ItemMaterialBinding

class MaterialAdapter(private val materials: List<Data>) :
    RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val binding = ItemMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MaterialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        val material = materials[position]
        holder.bind(material)
    }

    override fun getItemCount(): Int = materials.size

    class MaterialViewHolder(private val binding: ItemMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(material: Data) {
            binding.tvTitle.text = material.materialName
            binding.amount.text = "Amount: ${material.amount} KG"
        }
    }
}