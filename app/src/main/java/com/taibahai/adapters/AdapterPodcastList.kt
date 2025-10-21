package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelPodcastsResponse
import com.taibahai.activities.BookPDFDetailActivity
import com.taibahai.databinding.ItemPodcastListBinding


class AdapterPodcastList(var showData: MutableList<ModelPodcastsResponse.Data.Podcast>) :
    RecyclerView.Adapter<AdapterPodcastList.ViewHolder>() {
    lateinit var binding: ItemPodcastListBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding = ItemPodcastListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelPodcastsResponse.Data.Podcast>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val podcast = showData[position]
        holder.binding.tvBookName.text = podcast.title


//        holder.itemView.setOnClickListener {
//            val context = holder.itemView.context
//            val intent = Intent(context, BookPDFDetailActivity::class.java)
//            if (booksPDFData.attachments.isNotEmpty()) {
//                intent.putExtra("title", booksPDFData.title)
//                intent.putExtra("url", booksPDFData.attachments.firstOrNull()?.file)
//                context.startActivity(intent)
//            }
//        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemPodcastListBinding) : RecyclerView.ViewHolder(binding.root)
}