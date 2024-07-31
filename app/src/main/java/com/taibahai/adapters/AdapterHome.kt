package com.taibahai.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.network.interfaces.OnItemClick
import com.network.models.ModelHome
import com.network.utils.convertDateToLong
import com.taibahai.R
import com.taibahai.databinding.ItemHomeBinding
import com.taibahai.databinding.ItemScientificHomeBinding
import com.taibahai.utils.ImageLoading.loadImageWithProgress
import com.taibahai.utils.showOptionsMenu


class AdapterHome(
    var showData: MutableList<ModelHome.Data>,
    private var isProfileFeed: Boolean = false,
    private var listener: OnItemClick,
    private val onMenuItemClickListener: (ModelHome.Data, MenuItem) -> Boolean

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val USER_POST = 0
    private val ADMIN_POST_SCIENTIFIC = 1

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return when (viewType) {
            ADMIN_POST_SCIENTIFIC -> {
                val binding =
                    ItemScientificHomeBinding.inflate(LayoutInflater.from(context), parent, false)


                ScientificViewHolder(binding, listener)
            }

            else -> {
                val binding = ItemHomeBinding.inflate(LayoutInflater.from(context), parent, false)
                HomeViewHolder(binding, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showData[position].post_type == "adminPostScientific") ADMIN_POST_SCIENTIFIC else USER_POST
    }

    fun setData(list: MutableList<ModelHome.Data>) {
        list.clear()
        list.addAll(showData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val userData = showData[position]
        when (holder) {
            is HomeViewHolder -> {
                holder.binding.data = userData
                holder.binding.tvTimesAgo.text =
                    TimeAgo.using(userData.timesince.convertDateToLong())
                if (userData.feed_attachments.isNotEmpty()) {
                    context.loadImageWithProgress(
                        userData.feed_attachments[0].file,
                        holder.binding.ivUploadImage,
                        holder.binding.progressBar1
                    )
                } else {
                    holder.binding.progressBar1.visibility = View.GONE
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
                        context.showOptionsMenu(it, R.menu.popup_report) { item ->
                            onMenuItemClickListener(userData, item)
                        }
                    }
                }
                holder.binding.tvLike.setOnClickListener {
                    listener.onClick(position, "like", userData.feed_id)
                }
            }

            is ScientificViewHolder -> {
                holder.binding.tvScientificDescription.text =
                    Html.fromHtml(userData.scientific_description, Html.FROM_HTML_MODE_COMPACT)
                holder.binding.description.text =
                    Html.fromHtml(userData.description, Html.FROM_HTML_MODE_COMPACT)
                if (userData.feed_attachments.isNotEmpty()) {
                    context.loadImageWithProgress(
                        userData.feed_attachments[0].file,
                        holder.binding.ivUploadImage,
                        holder.binding.progressBar1
                    )
                } else {
                    holder.binding.progressBar1.visibility = View.GONE
                }

                holder.binding.btnComment.setOnClickListener {
                    listener.onClick(holder.absoluteAdapterPosition, "scientific_detail")
                }
            }
        }
    }

    class HomeViewHolder(val binding: ItemHomeBinding, listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition, "comment")
            }
        }
    }

    class ScientificViewHolder(val binding: ItemScientificHomeBinding, listener: OnItemClick) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                listener.onClick(absoluteAdapterPosition, "scientific_detail")
            }
        }
    }
}