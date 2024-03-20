package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelChapterHadith3
import com.taibahai.databinding.ItemChatpersHadithBinding

class AdapterChapterHadiths(var showData: ArrayList<ModelChapterHadith3.Data>,var listener: OnItemClick):RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>()
{
    lateinit var binding: ItemChatpersHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ViewHolder {
        binding = ItemChatpersHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    fun setData(list: ArrayList<ModelChapterHadith3.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvHadithNo.text=hadithChapter.hadith_no
        holder.binding.tvBookName.text = hadithChapter.book_name
        holder.binding.tvHadithType.text=hadithChapter.type
        holder.binding.tvArbiAyat.text=hadithChapter.arabic
        holder.binding.tvTranslation.text=hadithChapter.english_translation

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemChatpersHadithBinding, listener: OnItemClick) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition)

            }
        }
    }
}