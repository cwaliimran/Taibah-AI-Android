package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemHadithChapterBinding
import com.taibahai.hadiths.ChapterHadithsActivity3
import com.taibahai.hadiths.HadithChaptersActivity2
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelHadithChapter

class AdapterHadithChapter(var showData: MutableList<ModelHadithChapter>):RecyclerView.Adapter<AdapterHadithChapter.ViewHolder>() {
    lateinit var binding: ItemHadithChapterBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterHadithChapter.ViewHolder {
        binding = ItemHadithChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHadithChapter.ViewHolder(binding)
    }
    fun setDate(list: ArrayList<ModelHadithChapter>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterHadithChapter.ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvTitle.text= showData[position].title
        holder.binding.tvChapters.text=showData[position].chapters
        holder.binding.tvLanguage.text=showData[position].language

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChapterHadithsActivity3::class.java)

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemHadithChapterBinding) : RecyclerView.ViewHolder(binding.root) {}
}