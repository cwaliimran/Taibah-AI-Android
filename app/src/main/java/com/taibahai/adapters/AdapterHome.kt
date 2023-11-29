package com.taibahai.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.network.interfaces.OnItemClick
import com.taibahai.databinding.ItemHomeBinding
import com.taibahai.models.ModelHome

class AdapterHome( private var listener: OnItemClick, var showData: MutableList<ModelHome>
) : RecyclerView.Adapter<AdapterHome.ViewHolder>() {

    lateinit var binding: ItemHomeBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelHome>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val userData = showData[position]
        holder.binding.model = userData

        holder.binding.ivProfileImage.setImageResource(showData[position].profileImage)
        holder.binding.tvUserName.text = showData[position].userName
        holder.binding.tvTimesAgo.text = showData[position].timesAgo
        holder.binding.ivUploadImage.setImageResource(showData[position].userPost)
        holder.binding.tvDescription.text = showData[position].userDescription



        holder.binding.tvLike.setOnClickListener {

        }

        holder.binding.tvComment.setOnClickListener {


        }
    }


    class ViewHolder(val binding: ItemHomeBinding) : RecyclerView.ViewHolder(binding.root) {
    }
}