package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelDbSearchHadith
import com.taibahai.databinding.ItemDbSearchHadithBinding

class AdapterDBSearchHadith(
    var showData: ArrayList<ModelDbSearchHadith.Data>, var listener: OnItemClick
) : RecyclerView.Adapter<AdapterDBSearchHadith.ViewHolder>() {
    lateinit var binding: ItemDbSearchHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemDbSearchHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    fun setData(list: ArrayList<ModelDbSearchHadith.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.data = hadithChapter
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemDbSearchHadithBinding, listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition)
            }
        }
    }
}