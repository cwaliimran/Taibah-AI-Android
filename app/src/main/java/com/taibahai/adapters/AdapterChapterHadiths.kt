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
) : RecyclerView.Adapter<AdapterChapterHadiths.ViewHolder>() {
    lateinit var binding: ItemChatpersHadithBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemChatpersHadithBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hadithChapter = showData[position]
        holder.binding.model = hadithChapter
        holder.binding.tvBookName.text = hadithChapter.book_name
        holder.binding.tvHadithType.text = type

    }

    override fun getItemCount(): Int {
        return showData.size
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