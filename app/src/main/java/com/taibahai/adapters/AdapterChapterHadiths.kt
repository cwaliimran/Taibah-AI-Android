package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelChapterHadith3
import com.network.models.ModelDBSearchAll
import com.network.models.ModelDbSearchHadith
import com.taibahai.databinding.ItemChatpersHadithBinding
import com.taibahai.hadiths.HadithDetailsActivity4

class AdapterChapterHadiths(var showData: ArrayList<ModelChapterHadith3.Data>,var listener: OnItemClick):RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>()
{
    lateinit var binding: ItemChatpersHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterChapterHadiths.ViewHolder {
        binding = ItemChatpersHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterChapterHadiths.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelChapterHadith3.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterChapterHadiths.ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvHadithNo.text="Hadith No: ${hadithChapter.hadith_no}"
        holder.binding.tvBookName.text = hadithChapter.book_name
        holder.binding.tvHadithType.text=hadithChapter.type
        holder.binding.tvArbiAyat.text=hadithChapter.arabic
        holder.binding.tvTranslation.text=hadithChapter.english_translation

        binding.btnReadMore.setOnClickListener {

            listener.onClick(position)
        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemChatpersHadithBinding) : RecyclerView.ViewHolder(binding.root) {}
}