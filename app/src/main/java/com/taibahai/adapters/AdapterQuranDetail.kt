package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemQuranChapterBinding
import com.taibahai.databinding.ItemQuranChapterDetailBinding
import com.taibahai.models.ModelQuranDetail

class AdapterQuranDetail(var showData: MutableList<ModelQuranDetail>):RecyclerView.Adapter<AdapterQuranDetail.ViewHolder>() {
    lateinit var binding: ItemQuranChapterDetailBinding

    override fun onCreateViewHolder( parent: ViewGroup,viewType: Int): AdapterQuranDetail.ViewHolder {
        binding = ItemQuranChapterDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQuranDetail.ViewHolder(binding)
    }
    fun setDate(list: ArrayList<ModelQuranDetail>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterQuranDetail.ViewHolder, position: Int) {
        val chapterData = showData[position]
        holder.binding.model = chapterData
        holder.binding.tvCount.text= showData[position].count.toString()
        holder.binding.tvArbiAyat.text=showData[position].arbiAyat
        holder.binding.tvEnglishAyat.text=showData[position].englishAyat
        holder.binding.tvTranslation.text=showData[position].translation

    }

    override fun getItemCount(): Int {
        return showData.size

    }
    class ViewHolder(val binding: ItemQuranChapterDetailBinding) : RecyclerView.ViewHolder(binding.root) {}
}