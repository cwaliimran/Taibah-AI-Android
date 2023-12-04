package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.activities.ScholarDetailActivity
import com.taibahai.databinding.Item100scholarsBinding
import com.taibahai.models.Model100Scholars

class Adapter100Scholars(var showData: MutableList<Model100Scholars>):RecyclerView.Adapter<Adapter100Scholars.ViewHolder>() {
    lateinit var binding: Item100scholarsBinding
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): Adapter100Scholars.ViewHolder {
        binding = Item100scholarsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Adapter100Scholars.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<Model100Scholars>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: Adapter100Scholars.ViewHolder, position: Int) {
        val scholarList = showData[position]
        holder.binding.model = scholarList
        holder.binding.ivImage.setImageResource(showData[position].image)
        holder.binding.tvScholarName.text=showData[position].scholarName
        holder.binding.tvScholarBirth.text=showData[position].birth


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ScholarDetailActivity::class.java)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: Item100scholarsBinding) : RecyclerView.ViewHolder(binding.root) {}
}