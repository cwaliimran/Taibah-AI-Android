package com.taibahai.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.network.interfaces.OnItemClick
import com.taibahai.activities.HomeDetailActivity
import com.taibahai.databinding.ItemHomeBinding
import com.taibahai.models.ModelHome

class AdapterHome(  private var listener: OnItemClick, var showData: MutableList<com.network.models.ModelHome.Data>
) : RecyclerView.Adapter<AdapterHome.ViewHolder>() {

    lateinit var binding: ItemHomeBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    fun setDate(list: MutableList<com.network.models.ModelHome.Data>) {
        list.clear()
        list.addAll(showData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val userData = showData[position]
        holder.binding.model = userData

        Glide.with(holder.itemView.context).load(userData.user_image).into(holder.binding.ivProfileImage)
        holder.binding.tvUserName.text=userData.user_name
        holder.binding.tvTimesAgo.text = userData.timesince
        holder.binding.tvDescription.text = userData.description
        holder.binding.likesCounting.text = userData.likes.toString()
        holder.binding.commentCounts.text = "${userData.comments} Comments"
        Glide.with(holder.itemView.context).load(userData.feed_attachments.firstOrNull()?.file).into(holder.binding.ivUploadImage)

        holder.binding.tvLike.setOnClickListener {

        }

        holder.binding.tvAddComment.setOnClickListener {

            val intent=Intent(holder.itemView.context,HomeDetailActivity::class.java)
            intent.putExtra("feedId", userData.feed_id)
            intent.putExtra("likes", userData.likes)
            intent.putExtra("comments", userData.comments)
            intent.putExtra("post", userData.feed_attachments.firstOrNull()?.file)
            holder.itemView.context.startActivity(intent)


        }

        holder.binding.ivDots.setOnClickListener {
            listener.onClick(position, "dots", userData)

        }


    }


    class ViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}