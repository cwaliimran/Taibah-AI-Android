package com.taibahai.adapters

import android.graphics.ColorSpace.Model
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.R
import com.taibahai.models.ModelLanguages

class AdapterLanguage :RecyclerView.Adapter<AdapterLanguage.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_language,parent,false)
        return ViewHolder(view)
    }
    var selectedPosition = RecyclerView.NO_POSITION
        set(value) {
            // Update selected position and notify data set changed
            field = value
            notifyDataSetChanged()
        }


    lateinit var showLanguage:ArrayList<ModelLanguages>
    fun updateLanguage(list: ArrayList<ModelLanguages>)
    {
        showLanguage=list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvLanguage.text = showLanguage[position].countryLanguage
        holder.ivCountryFlag.setImageResource(showLanguage[position].image)

        val isSelected = position == selectedPosition
        val backgroundDrawableRes = if (isSelected) R.drawable.language_checked_bg else R.drawable.language_bg
        holder.clSimple.setBackgroundResource(backgroundDrawableRes)

        holder.itemView.setOnClickListener {
            selectedPosition = position
        }


    }

    override fun getItemCount(): Int {
        return showLanguage.size
    }

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView) {
        val clSimple: ConstraintLayout = itemView.findViewById(R.id.clSimple)
        val tvLanguage: TextView = itemView.findViewById(R.id.tvLanguage)
        val ivCountryFlag: ImageView = itemView.findViewById(R.id.ivCountryFlag)
    }
}