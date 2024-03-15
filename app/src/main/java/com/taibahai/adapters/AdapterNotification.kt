package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelNotifications
import com.taibahai.databinding.ItemNotificationBinding

class AdapterNotification(var showData: MutableList<ModelNotifications.Data>) :
    RecyclerView.Adapter<AdapterNotification.ViewHolder>() {
    lateinit var binding: ItemNotificationBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notiData = showData[position]
        holder.binding.data = notiData

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)
}