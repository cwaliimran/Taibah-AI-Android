package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelChapterHadith3
import com.network.models.ModelDBSearchAll
import com.network.models.ModelDbSearchHadith
import com.taibahai.databinding.ItemChatpersHadithBinding
import com.taibahai.hadiths.HadithDetailsActivity4

class AdapterChapterHadiths(var showData: ArrayList<ModelChapterHadith3.Data>):RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>()
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
        val parts = hadithChapter.reference?.split("\t : ")
        if (parts?.size!! > 1) {
            holder.binding.tvBookName.text = parts[1]
        } else {
            holder.binding.tvBookName.text = hadithChapter.reference
        }
        holder.binding.tvArbiAyat.text=hadithChapter.arabic
        holder.binding.tvTranslation.text=hadithChapter.english_translation

        binding.btnReadMore.setOnClickListener {
            val intent= Intent(holder.itemView.context,HadithDetailsActivity4::class.java)
            intent.putExtra("id",hadithChapter.chapter_id)
            intent.putExtra("hadith_id",hadithChapter.hadith_no)


            holder.itemView.context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemChatpersHadithBinding) : RecyclerView.ViewHolder(binding.root) {}
}