package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelDbSearchHadith
import com.taibahai.databinding.ItemChatpersHadithBinding
import com.taibahai.databinding.ItemDbSearchHadithBinding
import com.taibahai.hadiths.HadithDetailsActivity4

class AdapterDBSearchHadith(var showData: ArrayList<ModelDbSearchHadith.Data>): RecyclerView.Adapter<AdapterDBSearchHadith.ViewHolder>()
{
    lateinit var binding: ItemDbSearchHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDBSearchHadith.ViewHolder {
        binding = ItemDbSearchHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterDBSearchHadith.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelDbSearchHadith.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterDBSearchHadith.ViewHolder, position: Int) {
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
            val intent= Intent(holder.itemView.context, HadithDetailsActivity4::class.java)
            holder.itemView.context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemDbSearchHadithBinding) : RecyclerView.ViewHolder(binding.root) {}
}