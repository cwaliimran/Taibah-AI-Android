package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.R
import com.taibahai.databinding.ItemHomeBinding
import com.taibahai.databinding.ItemMoreBinding
import com.taibahai.models.ModelHome
import com.taibahai.models.ModelMore

class AdapterMore( var showData: MutableList<ModelMore>):RecyclerView.Adapter<AdapterMore.ViewHolder>() {
    lateinit var binding: ItemMoreBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMore.ViewHolder {
        binding = ItemMoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterMore.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelMore>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterMore.ViewHolder, position: Int) {
        val moreData = showData[position]
        holder.binding.model = moreData
        holder.binding.tvLevel.text=showData[position].level
        holder.binding.tvPackege.text=showData[position].packageName
        val adapter=AdapterMoreLevels(moreData.levelsList)
        holder.rvMoreLevelsList.adapter=adapter
        if (position == 0) {
            holder.binding.tvFree.visibility = View.VISIBLE
            holder.binding.tvLevel.visibility = View.INVISIBLE
            holder.binding.tvPackege.visibility = View.INVISIBLE
            holder.binding.btnUpgrade.visibility = View.INVISIBLE
            val layoutParams = holder.rvMoreLevelsList.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.topMargin = -40
            holder.rvMoreLevelsList.layoutParams = layoutParams
        } else {
            holder.binding.tvFree.visibility = View.INVISIBLE
            holder.binding.tvLevel.visibility = View.VISIBLE
            holder.binding.tvPackege.visibility = View.VISIBLE
            holder.binding.btnUpgrade.visibility = View.VISIBLE

        }



    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        val rvMoreLevelsList: RecyclerView = itemView.findViewById(R.id.rvMoreLevelsList)

    }
}