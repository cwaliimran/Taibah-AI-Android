package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.models.ModelChapterHadith3
import com.taibahai.databinding.ItemChatpersHadithBinding

class AdapterChapterHadiths(
    var showData: ArrayList<ModelChapterHadith3.Data>,
    var listener: OnItemClick,
    var type: String,
    var title: String
) : RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>() {
    lateinit var binding: ItemChatpersHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemChatpersHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    fun setData(list: ArrayList<ModelChapterHadith3.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvHadithNo.text = hadithChapter.hadith_no
        holder.binding.tvBookName.text = title
        holder.binding.tvHadithType.text = type
        holder.binding.ayatArabicText.text = hadithChapter.arabic
        holder.binding.ayatEnglishTranslitration.text = hadithChapter.english_translation

    }

    override fun getItemCount(): Int {
        return showData.size
    }

    fun updateData(showHadithData: ArrayList<ModelChapterHadith3.Data>, bookTitle: String) {
        showData = showHadithData
        title = bookTitle
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemChatpersHadithBinding, listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition)

            }
        }
    }
}