package com.taibahai.adapters

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.R
import com.taibahai.databinding.ItemUpgradeBinding
import com.taibahai.models.ModelUpgrade
import com.taibahai.models.ModelUpgradeList

class AdapterUpgrade(var showData: List<ModelUpgrade>):RecyclerView.Adapter<AdapterUpgrade.ViewHolder>() {
    lateinit var binding:ItemUpgradeBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterUpgrade.ViewHolder {
        binding = ItemUpgradeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterUpgrade.ViewHolder(binding)
    }

    fun setData(list: List<ModelUpgrade>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterUpgrade.ViewHolder, position: Int) {
        val upgradeData = showData[position]
        holder.binding.model = upgradeData
        holder.binding.tvCount.text=showData[position].tvCount
        holder.binding.tvPackage.text=showData[position].packageName
        val adapter = AdapterUpgradeList( upgradeData.upgradeList)
        holder.rvMoreLevelsList.adapter = adapter
        holder.binding.tvSubscriptionPrice.text=showData[position].subscriptionPrice

        when (position) {
            1 -> setColorFilter(holder.binding.clBackground, Color.parseColor("#FCA120"))
            2 -> setColorFilter(holder.binding.clBackground, Color.parseColor("#F2542D"))
            else -> setColorFilter(holder.binding.clBackground, Color.TRANSPARENT)
        }


    }

    private fun setColorFilter(view: View, color: Int) {
        val colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        view.background.colorFilter = colorFilter
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemUpgradeBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvMoreLevelsList: RecyclerView = itemView.findViewById(R.id.rvUpgradeList)

    }

}