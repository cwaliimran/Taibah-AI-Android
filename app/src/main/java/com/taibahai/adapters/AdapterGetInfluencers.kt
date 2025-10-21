package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.network.interfaces.OnItemClick
import com.network.models.ModelGetInfluencers
import com.taibahai.R
import com.taibahai.databinding.ItemWatchAndLearnBinding
class AdapterGetInfluencers(
    var showData: ArrayList<ModelGetInfluencers.Data.Influencer>,
    var listener: OnItemClick
) : RecyclerView.Adapter<AdapterGetInfluencers.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWatchAndLearnBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelGetInfluencers.Data.Influencer>) {
        showData = list
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val influencers = showData[position]
        holder.binding.tvBookName.text = influencers.name
        holder.binding.tvTotal.text = "Recorded: " + influencers.total_podcasts

        Glide.with(holder.itemView.context)
            .load(influencers.image_url)
            .placeholder(R.drawable.books)
            .into(holder.binding.ivImage)

        holder.itemView.setOnClickListener {
            listener.onClick(position)
        }
    }

    override fun getItemCount(): Int = showData.size


    class ViewHolder(val binding: ItemWatchAndLearnBinding) :
        RecyclerView.ViewHolder(binding.root)
}
