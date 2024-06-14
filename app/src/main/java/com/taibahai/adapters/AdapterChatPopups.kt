package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.databinding.ItemChatPopupsBinding
import com.taibahai.models.ModelChatPopups


class AdapterChatPopups(
    var showMessagePopups: ArrayList<ModelChatPopups>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<AdapterChatPopups.ViewHolder>() {
    lateinit var binding: ItemChatPopupsBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding = ItemChatPopupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelChatPopups>) {
        showMessagePopups = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messagePopups = showMessagePopups[position]
        holder.binding.model = messagePopups
        holder.binding.tvMessageItem.text = showMessagePopups[position].message

        holder.binding.root.setOnClickListener {
            onItemClick.invoke(showMessagePopups[position].message)

        }
    }

    override fun getItemCount(): Int {
        return showMessagePopups.size
    }

    class ViewHolder(val binding: ItemChatPopupsBinding) : RecyclerView.ViewHolder(binding.root)

}