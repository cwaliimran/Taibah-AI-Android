package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemHadithBooksBinding
import com.taibahai.databinding.ItemNotificationBinding
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelHadithChapter
import com.taibahai.models.ModelNotification

class AdapterNotification(var showData: MutableList<ModelNotification>):RecyclerView.Adapter<AdapterNotification.ViewHolder>() {
    lateinit var binding: ItemNotificationBinding
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterNotification.ViewHolder {
        binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterNotification.ViewHolder(binding)
    }
    fun setDate(list: ArrayList<ModelNotification>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterNotification.ViewHolder, position: Int) {
        val notiData = showData[position]
        holder.binding.model = notiData
        holder.binding.ivIcon.setImageResource(showData[position].image)
        holder.binding.tvTitle.text= showData[position].title
        holder.binding.tvDes.text=showData[position].description
        holder.binding.tvTime.text=showData[position].time

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {}
}