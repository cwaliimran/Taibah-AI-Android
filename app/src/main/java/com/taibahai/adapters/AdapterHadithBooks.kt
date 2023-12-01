package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.activities.ChapterDetailActivity
import com.taibahai.databinding.ItemHadithBooksBinding
import com.taibahai.databinding.ItemQuranChapterDetailBinding
import com.taibahai.hadiths.HadithChaptersActivity2
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelQuranDetail

class AdapterHadithBooks(var showData: MutableList<ModelHadithBook>):RecyclerView.Adapter<AdapterHadithBooks.ViewHolder>() {
    lateinit var binding: ItemHadithBooksBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int ): AdapterHadithBooks.ViewHolder {
        binding = ItemHadithBooksBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterHadithBooks.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelHadithBook>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterHadithBooks.ViewHolder, position: Int) {
        val hadithData = showData[position]
        holder.binding.model = hadithData
        holder.binding.tvCounter.text= showData[position].count.toString()
        holder.binding.tvBookName.text=showData[position].bookName
        holder.binding.tvChapter.text=showData[position].chapters

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, HadithChaptersActivity2::class.java)

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemHadithBooksBinding) : RecyclerView.ViewHolder(binding.root) {}
}