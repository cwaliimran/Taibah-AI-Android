package com.taibahai.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelHadithChapter2
import com.taibahai.databinding.ItemHadithChapterBinding
import com.taibahai.hadiths.ChapterHadithsActivity3

class AdapterHadithChapter(var showData: MutableList<ModelHadithChapter2.Data>, var listener: OnItemClick):RecyclerView.Adapter<AdapterHadithChapter.ViewHolder>() {
    lateinit var binding: ItemHadithChapterBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterHadithChapter.ViewHolder {
        binding = ItemHadithChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHadithChapter.ViewHolder(binding)
    }
    fun setDate(list: ArrayList<ModelHadithChapter2.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: AdapterHadithChapter.ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvTitle.text= hadithChapter.chapter_name
        holder.binding.tvChapters.text=hadithChapter.hadith_number
        holder.binding.tvLanguage.text=hadithChapter.arabic_name

        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemHadithChapterBinding) : RecyclerView.ViewHolder(binding.root) {}
}