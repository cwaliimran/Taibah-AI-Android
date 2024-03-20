package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.network.models.ModelComments
import com.taibahai.databinding.ItemCommentBinding

class AdapterComments(var showData: MutableList<ModelComments>): RecyclerView.Adapter<AdapterComments.ViewHolder>() {
    lateinit var binding: ItemCommentBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: MutableList<ModelComments>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterComments.ViewHolder, position: Int) {
        val userData = showData[position]

        holder.view.model = userData

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val view: ItemCommentBinding) : RecyclerView.ViewHolder(view.root) {
    }
}