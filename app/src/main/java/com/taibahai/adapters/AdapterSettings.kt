package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemHadithBooksBinding
import com.taibahai.databinding.ItemSettingsBinding
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelSettings

class AdapterSettings(var showData: MutableList<ModelSettings>, private val onItemClick: (Int) -> Unit):RecyclerView.Adapter<AdapterSettings.ViewHolder>() {
    lateinit var binding: ItemSettingsBinding
    var selectedPosition=RecyclerView.NO_POSITION
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSettings.ViewHolder {
        binding = ItemSettingsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterSettings.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelSettings>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterSettings.ViewHolder, position: Int) {
        val settingData = showData[position]
        holder.binding.model = settingData
        holder.binding.ivicon.setImageResource(showData[position].icon)
        holder.binding.tvName.text=showData[position].settingName

        holder.itemView.setOnClickListener {
            onItemClick(position)

            selectedPosition = position
            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemSettingsBinding) : RecyclerView.ViewHolder(binding.root) {}
}