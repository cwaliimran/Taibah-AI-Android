package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemChatpersHadithBinding
import com.taibahai.databinding.ItemHadithChapterBinding
import com.taibahai.hadiths.HadithDetailsActivity4
import com.taibahai.models.ModelChapterHadiths
import com.taibahai.models.ModelHadithChapter

class AdapterChapterHadiths(var showData: MutableList<ModelChapterHadiths>):RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>()
{
    lateinit var binding: ItemChatpersHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterChapterHadiths.ViewHolder {
        binding = ItemChatpersHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterChapterHadiths.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelChapterHadiths>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterChapterHadiths.ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvHadithNo.text= showData[position].hadithNo.toString()
        holder.binding.tvBookName.text=showData[position].bookName
        holder.binding.tvHadithType.text=showData[position].hadithType
        holder.binding.tvArbiAyat.text=showData[position].arbiAyat
        holder.binding.tvTranslation.text=showData[position].translation

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