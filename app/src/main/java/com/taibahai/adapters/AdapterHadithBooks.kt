package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelHadithBooks
import com.taibahai.databinding.ItemHadithBooksBinding
import com.taibahai.hadiths.HadithChaptersActivity2

class AdapterHadithBooks(var showData: MutableList<ModelHadithBooks.Data>, var listener: OnItemClick):RecyclerView.Adapter<AdapterHadithBooks.ViewHolder>() {
    lateinit var binding: ItemHadithBooksBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): AdapterHadithBooks.ViewHolder {
        binding = ItemHadithBooksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHadithBooks.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelHadithBooks.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterHadithBooks.ViewHolder, position: Int) {
        val hadithData = showData[position]
        holder.binding.model = hadithData
        holder.binding.tvCounter.text = (position + 1).toString()
        holder.binding.tvBookName.text=hadithData.title
        holder.binding.tvChapter.text = "${hadithData.imam}, ${hadithData.total_chapters} Chapters"

        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemHadithBooksBinding) : RecyclerView.ViewHolder(binding.root) {}
}