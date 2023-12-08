package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelDBSearch
import com.taibahai.databinding.ItemChatpersHadithBinding
import com.taibahai.databinding.ItemHadithChapterBinding
import com.taibahai.hadiths.HadithDetailsActivity4
import com.taibahai.models.ModelChapterHadiths
import com.taibahai.models.ModelHadithChapter

class AdapterChapterHadiths(var showData: MutableList<ModelDBSearch.Data>):RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>()
{
    lateinit var binding: ItemChatpersHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterChapterHadiths.ViewHolder {
        binding = ItemChatpersHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterChapterHadiths.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelDBSearch.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterChapterHadiths.ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvHadithNo.text = "Hadith No: ${hadithChapter.hadith_no}"
        val parts = hadithChapter.reference.split("\t : ")
        if (parts.size > 1) {
            holder.binding.tvBookName.text = parts[1]
        } else {
            holder.binding.tvBookName.text = hadithChapter.reference
        }
        //holder.binding.tvHadithType.text=hadithChapter.reference
        holder.binding.tvArbiAyat.text=hadithChapter.arabic
        holder.binding.tvTranslation.text=hadithChapter.english_translation

        binding.btnReadMore.setOnClickListener {
            val intent= Intent(holder.itemView.context,HadithDetailsActivity4::class.java)
            holder.itemView.context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemChatpersHadithBinding) : RecyclerView.ViewHolder(binding.root) {}
}