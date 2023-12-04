package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemMoreLevelsBinding
import com.taibahai.models.ModelMoreLevels

class AdapterMoreLevels( private val onItemClick: (ModelMoreLevels) -> Unit, var showData: MutableList<ModelMoreLevels>):RecyclerView.Adapter<AdapterMoreLevels.ViewHolder>() {
    lateinit var binding: ItemMoreLevelsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): AdapterMoreLevels.ViewHolder {
        binding = ItemMoreLevelsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMoreLevels.ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMoreLevels.ViewHolder, position: Int) {
        val moreData = showData[position]
        holder.binding.model = moreData
        holder.binding.ivIcon.setImageResource(showData[position].icon)
        holder.binding.tvTitle.text=showData[position].title
        holder.itemView.setOnClickListener {
            onItemClick(moreData)
        }


    }

    override fun getItemCount(): Int {
    return showData.size
    }
    class ViewHolder(val binding: ItemMoreLevelsBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}