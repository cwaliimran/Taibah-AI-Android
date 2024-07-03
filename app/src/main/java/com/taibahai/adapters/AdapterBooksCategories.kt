package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelBooksCategories
import com.taibahai.databinding.ItemBooksCategoriesBinding


class AdapterBooksCategories(
    var showData: ArrayList<ModelBooksCategories.Data>,
    var listener: OnItemClick
) :
    RecyclerView.Adapter<AdapterBooksCategories.ViewHolder>() {
    lateinit var binding: ItemBooksCategoriesBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding =
            ItemBooksCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelBooksCategories.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booksPDFData = showData[position]
        holder.binding.data = booksPDFData
        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemBooksCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root)
}