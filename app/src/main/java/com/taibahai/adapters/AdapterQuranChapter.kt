package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.taibahai.activities.ChapterDetailActivity
import com.taibahai.databinding.ItemQuranChapterBinding

class AdapterQuranChapter(var showData: MutableList<ModelSurah>, var listener: OnItemClick):RecyclerView.Adapter<AdapterQuranChapter.ViewHolder>() {
    lateinit var binding: ItemQuranChapterBinding

    override fun onCreateViewHolder(  parent: ViewGroup,viewType: Int): AdapterQuranChapter.ViewHolder {
        binding = ItemQuranChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQuranChapter.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelSurah>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterQuranChapter.ViewHolder, position: Int) {
        val chapterData = showData[position]
        binding.data = chapterData

        holder.itemView.setOnClickListener {
            listener.onClick(position,"type", chapterData)


        }

    }




    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemQuranChapterBinding) : RecyclerView.ViewHolder(binding.root) {}
}