package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.activities.ChapterDetailActivity
import com.taibahai.databinding.ItemQuranChapterBinding
import com.taibahai.models.ModelQuranChapter

class AdapterQuranChapter( var showData: MutableList<ModelQuranChapter>):RecyclerView.Adapter<AdapterQuranChapter.ViewHolder>() {
    lateinit var binding: ItemQuranChapterBinding

    override fun onCreateViewHolder(  parent: ViewGroup,viewType: Int): AdapterQuranChapter.ViewHolder {
        binding = ItemQuranChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQuranChapter.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelQuranChapter>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterQuranChapter.ViewHolder, position: Int) {
        val chapterData = showData[position]
        holder.binding.model = chapterData
        holder.binding.tvCounter.text= showData[position].count.toString()
        holder.binding.tvQuranChapter.text=showData[position].chapterName
        holder.binding.tvChapterDes.text=showData[position].chapterDec

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChapterDetailActivity::class.java)

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemQuranChapterBinding) : RecyclerView.ViewHolder(binding.root) {}
}