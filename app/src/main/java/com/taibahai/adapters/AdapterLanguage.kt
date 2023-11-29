package com.taibahai.adapters

import android.graphics.ColorSpace.Model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.R
import com.taibahai.models.ModelLanguages

class AdapterLanguage():RecyclerView.Adapter<AdapterLanguage.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterLanguage.ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_language,parent,false)
        return AdapterLanguage.ViewHolder(view)
    }
    var selectedPosition=RecyclerView.NO_POSITION


    lateinit var showLanguage:ArrayList<ModelLanguages>
    fun updateLanguage(list: ArrayList<ModelLanguages>)
    {
        showLanguage=list
    }

    override fun onBindViewHolder(holder: AdapterLanguage.ViewHolder, position: Int) {
        holder.tvLanguage.text = showLanguage[position].countryLanguage
        holder.ivCountryFlag.setImageResource(showLanguage[position].image)

        /*holder.itemView.setOnClickListener {
            onItemClick(position)

            // Update selected position and notify data set changed
            selectedPosition = position
            notifyDataSetChanged()
        }*/
    }

    override fun getItemCount(): Int {
        return showLanguage.size
    }

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView) {
        val tvLanguage: TextView = itemView.findViewById(R.id.tvLanguage)
        val ivCountryFlag: ImageView = itemView.findViewById(R.id.ivCountryFlag)
    }
}