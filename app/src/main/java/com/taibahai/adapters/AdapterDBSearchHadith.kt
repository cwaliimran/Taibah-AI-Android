package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelDbSearchHadith
import com.taibahai.databinding.ItemDbSearchHadithBinding
import com.taibahai.search_database_tablayout.TopHadithFragment

class AdapterDBSearchHadith(var showData: ArrayList<ModelDbSearchHadith.Data>, var listener: OnItemClick): RecyclerView.Adapter<AdapterDBSearchHadith.ViewHolder>()
{
    lateinit var binding: ItemDbSearchHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDBSearchHadith.ViewHolder {
        binding = ItemDbSearchHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterDBSearchHadith.ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelDbSearchHadith.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterDBSearchHadith.ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvHadithNo.text="Hadith No: ${hadithChapter.hadith_no}"
        holder.binding.tvBookName.text=hadithChapter.book_name
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
    class ViewHolder(val binding: ItemDbSearchHadithBinding) : RecyclerView.ViewHolder(binding.root) {}
}