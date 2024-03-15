package com.taibahai.adapters

import android.annotation.SuppressLint
import android.content.Context
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

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val userData = showData[position]
        holder.binding.data = userData

        holder.binding.tvTimesAgo.text = TimeAgo.using(userData.timesince.convertDateToLong())

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