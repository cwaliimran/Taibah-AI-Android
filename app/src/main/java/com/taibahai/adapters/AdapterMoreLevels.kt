package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.taibahai.databinding.ItemMoreLevelsBinding
import com.taibahai.models.ModelMoreLevels

class AdapterMoreLevels(
    var showData: ArrayList<ModelMoreLevels>,
    private val listener: OnItemClick,
) : RecyclerView.Adapter<AdapterMoreLevels.ViewHolder>() {
    lateinit var binding: ItemMoreLevelsBinding


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding = ItemMoreLevelsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val moreData = showData[position]
        holder.binding.model = moreData
        holder.binding.ivIcon.setImageResource(showData[position].icon)
        holder.binding.tvTitle.text = showData[position].title
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemMoreLevelsBinding) : RecyclerView.ViewHolder(binding.root)
}