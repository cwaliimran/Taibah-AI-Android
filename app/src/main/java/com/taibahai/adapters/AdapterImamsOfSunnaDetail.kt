package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelScholars
import com.taibahai.activities.BookPDFDetailActivity
import com.taibahai.databinding.ItemBooklistTitlesBinding

class AdapterImamsOfSunnaDetail(var showData: MutableList<ModelScholars.Data.Book>): RecyclerView.Adapter<AdapterImamsOfSunnaDetail.ViewHolder>() {
    lateinit var binding: ItemBooklistTitlesBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterImamsOfSunnaDetail.ViewHolder {
        binding = ItemBooklistTitlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterImamsOfSunnaDetail.ViewHolder(binding)
    }

    fun setData(list: MutableList<ModelScholars.Data.Book>) {
        list.clear()
        list.addAll(showData)
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: AdapterImamsOfSunnaDetail.ViewHolder, position: Int) {
        val scholarList = showData[position]
        holder.binding.model = scholarList
        holder.binding.tvBookName.text=scholarList.book_title



        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookPDFDetailActivity::class.java)

            intent.putExtra("fileUrl", scholarList.book_file)


            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemBooklistTitlesBinding) : RecyclerView.ViewHolder(binding.root) {}
}