package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelDbSearchHadith
import com.network.models.ModelDbSearchQuran
import com.taibahai.databinding.ItemChatpersHadithBinding
import com.taibahai.databinding.ItemDbQuranBinding
import com.taibahai.hadiths.HadithDetailsActivity4

class AdapterDbSearchQuran(var showData: ArrayList<ModelDbSearchQuran.Data>): RecyclerView.Adapter<AdapterDbSearchQuran.ViewHolder>()
{
    lateinit var binding: ItemDbQuranBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDbSearchQuran.ViewHolder {
        binding = ItemDbQuranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterDbSearchQuran.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelDbSearchQuran.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterDbSearchQuran.ViewHolder, position: Int) {
        val quranList = showData[position]
        holder.binding.model = quranList
        holder.binding.tvSurahName.text=quranList.transliteration_en
       holder.binding.tvDes.text=quranList.translation_en
        holder.binding.tvCounter.text = (position + 1).toString()

    }

    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemDbQuranBinding) : RecyclerView.ViewHolder(binding.root) {}
}