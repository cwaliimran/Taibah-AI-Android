package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelUpcoming
import com.taibahai.databinding.RowUpcomingFeaturesBinding

class AdapterFeatures(var showData: MutableList<ModelUpcoming.Data>, var listener: OnItemClick) :
    RecyclerView.Adapter<AdapterFeatures.ViewHolder>() {
    lateinit var binding: RowUpcomingFeaturesBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            RowUpcomingFeaturesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = showData[position]
        holder.binding.data = data

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: RowUpcomingFeaturesBinding, var listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition)
            }
        }
    }
}