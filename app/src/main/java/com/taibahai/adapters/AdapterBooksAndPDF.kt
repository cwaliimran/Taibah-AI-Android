package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelBooks
import com.taibahai.activities.BookPDFDetailActivity
import com.taibahai.databinding.ItemBooksPdfBinding


class AdapterBooksAndPDF(var showData: MutableList<ModelBooks.Data>) :
    RecyclerView.Adapter<AdapterBooksAndPDF.ViewHolder>() {
    lateinit var binding: ItemBooksPdfBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding = ItemBooksPdfBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelBooks.Data>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booksPDFData = showData[position]
        holder.binding.model = booksPDFData
        holder.binding.tvBookName.text = booksPDFData.title


        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookPDFDetailActivity::class.java)
            if (booksPDFData.attachments.isNotEmpty()) {
                intent.putExtra("title", booksPDFData.title)
                intent.putExtra("url", booksPDFData.attachments.firstOrNull()?.file)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemBooksPdfBinding) : RecyclerView.ViewHolder(binding.root)
}