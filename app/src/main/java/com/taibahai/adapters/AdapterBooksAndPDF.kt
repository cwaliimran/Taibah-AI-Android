package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.taibahai.activities.BookPDFDetailActivity
import com.taibahai.databinding.ItemBooksPdfBinding
import com.taibahai.hadiths.HadithChaptersActivity2
import com.taibahai.models.ModelBooksAndPDF


class AdapterBooksAndPDF(var showData: MutableList<ModelBooksAndPDF>):RecyclerView.Adapter<AdapterBooksAndPDF.ViewHolder>() {
    lateinit var binding: ItemBooksPdfBinding

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): AdapterBooksAndPDF.ViewHolder {
        binding = ItemBooksPdfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterBooksAndPDF.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelBooksAndPDF>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterBooksAndPDF.ViewHolder, position: Int) {
        val booksPDFData = showData[position]
        holder.binding.model = booksPDFData
        holder.binding.ivImage.setImageResource(showData[position].image)
        holder.binding.tvBookName.text=showData[position].bookName

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookPDFDetailActivity::class.java)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return  showData.size
    }

    class ViewHolder(val binding: ItemBooksPdfBinding) : RecyclerView.ViewHolder(binding.root) {}
}