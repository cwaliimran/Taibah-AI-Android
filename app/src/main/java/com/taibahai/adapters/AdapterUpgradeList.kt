package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemMoreLevelsBinding
import com.taibahai.databinding.ItemUpgradeListBinding
import com.taibahai.models.ModelMoreLevels
import com.taibahai.models.ModelUpgradeList

class AdapterUpgradeList( var showData: ArrayList<ModelUpgradeList>):
    RecyclerView.Adapter<AdapterUpgradeList.ViewHolder>() {
    lateinit var binding: ItemUpgradeListBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): AdapterUpgradeList.ViewHolder {
        binding = ItemUpgradeListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterUpgradeList.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterUpgradeList.ViewHolder, position: Int) {
        val moreData = showData[position]
        holder.binding.model = moreData
        holder.binding.tvOffers.text=showData[position].offer



    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemUpgradeListBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}