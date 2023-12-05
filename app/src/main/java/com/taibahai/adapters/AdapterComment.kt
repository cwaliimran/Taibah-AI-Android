package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.taibahai.R
import com.taibahai.databinding.ItemCommentBinding
import com.taibahai.models.ModelComments
import com.taibahai.models.ModelComments.Companion.formatToHHmma

class AdapterComments(var showData: MutableList<ModelComments>): RecyclerView.Adapter<AdapterComments.ViewHolder>() {
    lateinit var binding: ItemCommentBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setDate(list: MutableList<ModelComments>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterComments.ViewHolder, position: Int) {
        val userData = showData[position]
        holder.view.model = userData
        holder.view.ivProfile.setImageResource(showData[position].ivProfile)


        holder.view.tvName.text = showData[position].tvName
        holder.view.tvComment.text = showData[position].tvComment
        holder.view.tvCommentTiming.text= showData[position].tvCommentTiming.toString()
        /*val timeFormatted = showData[position].tvCommentTiming?.formatToHHmma()

        val timeAgo =
            timeFormatted?.let { ModelComments.dateToMillis(it) }?.let { TimeAgo.using(it) }
        holder.view.tvCommentTiming.text = timeAgo
*/
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val view: ItemCommentBinding) : RecyclerView.ViewHolder(view.root) {
    }
}