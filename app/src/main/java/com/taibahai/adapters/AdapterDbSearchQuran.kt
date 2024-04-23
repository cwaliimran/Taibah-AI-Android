package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.taibahai.databinding.ItemDbQuranBinding
import com.taibahai.quran.SurahListModel

class AdapterDbSearchQuran(
    var showData: MutableList<SurahListModel>,
    var listener: OnItemClick
) :
    RecyclerView.Adapter<AdapterDbSearchQuran.ViewHolder>() {
    lateinit var binding: ItemDbQuranBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemDbQuranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    fun setData(list: ArrayList<SurahListModel>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quranList = showData[position]
        holder.binding.model = quranList
        holder.binding.surahName.text = quranList.transliteration_en
        holder.binding.tvDes.text = quranList.translation_en
        holder.binding.tvCounter.text = quranList.number

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemDbQuranBinding, listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition)
            }
        }

    }
}