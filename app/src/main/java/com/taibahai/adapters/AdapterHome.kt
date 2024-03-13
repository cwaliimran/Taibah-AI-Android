package com.taibahai.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.network.interfaces.OnItemClick
import com.network.models.ModelHome
import com.taibahai.R
import com.taibahai.databinding.ItemHomeBinding
import com.taibahai.utils.showOptionsMenu


class AdapterHome(
    var showData: MutableList<ModelHome.Data>,
    private var isProfileFeed: Boolean = false,
    private var listener: OnItemClick,
    private val onMenuItemClickListener: (ModelHome.Data, MenuItem) -> Boolean

) : RecyclerView.Adapter<AdapterHome.ViewHolder>() {


    lateinit var context: Context
    lateinit var binding: ItemHomeBinding


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        binding = ItemHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding, listener)
    }

    fun setDate(list: MutableList<com.network.models.ModelHome.Data>) {
        list.clear()
        list.addAll(showData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    @SuppressLint("ResourceAsColor", "SuspiciousIndentation", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val userData = showData[position]
        holder.binding.data = userData

        Glide.with(holder.itemView.context).load(userData.user_image)
            .into(holder.binding.ivProfileImage)
        holder.binding.tvUserName.text = userData.user_name
        holder.binding.tvTimesAgo.text = userData.timesince
        holder.binding.tvDescription.text = userData.description
        holder.binding.likesCounting.text = userData.likes.toString()
        holder.binding.commentCounts.text = "${userData.comments} Comments"
        Glide.with(holder.itemView.context).load(userData.feed_attachments.firstOrNull()?.file)
            .into(holder.binding.ivUploadImage)

        if (userData.likes == 1) {
            holder.binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0)
        } else {
            holder.binding.tvLike.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.like_2,
                0,
                0,
                0
            )
        }
        if (isProfileFeed) {
            holder.binding.ivDots.visibility = View.GONE
            holder.binding.ivDelete.visibility = View.VISIBLE

                holder.binding.ivDelete.setOnClickListener {
                listener.onClick(position, "delete", userData.feed_id)
            }
        } else {
            holder.binding.ivDots.visibility = View.VISIBLE
            holder.binding.ivDelete.visibility = View.GONE

            holder.binding.ivDots.setOnClickListener {
                //show option menu
                context.showOptionsMenu(it, R.menu.popup_report) { item ->
                    onMenuItemClickListener(userData, item)
                }

            }
        }
        holder.binding.tvLike.setOnClickListener {
            listener.onClick(position, "like", userData.feed_id)
        }
    }


    class ViewHolder(val binding: ItemHomeBinding, listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition, "comment")

            }
        }
    }

}