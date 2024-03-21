package com.taibahai.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.network.models.ModelInheritanceLaw
import com.taibahai.R
import com.taibahai.activities.InheritanceLawDetailActivity

class AdapterInheritanceLaw(private val showData: ArrayList<ModelInheritanceLaw.Data>) :
    RecyclerView.Adapter<AdapterInheritanceLaw.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.item_inheritancelaw_text, parent, false)
                TextViewHolder(view)
            }

            TYPE_PDF -> {
                val view = inflater.inflate(R.layout.item_inheritancelaw_pdf, parent, false)
                PdfViewHolder(view)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = showData[position]
        when (holder) {
            is TextViewHolder -> {
                holder.bindText(item)
                holder.itemView.setOnClickListener {
                    if (item.type == "text") {
                        val intent = Intent(
                            holder.itemView.context,
                            InheritanceLawDetailActivity::class.java
                        )
                        intent.putExtra("type", "text")
                        intent.putExtra("description", item.description)
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }

            is PdfViewHolder -> {
                holder.bindPdf(item)
                holder.itemView.setOnClickListener {
                    if (item.type == "pdf") {
                        val intent = Intent(
                            holder.itemView.context,
                            InheritanceLawDetailActivity::class.java
                        )
                        intent.putExtra("type", "pdf")
                        intent.putExtra("pdfUrl", item.attachment.file)
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (showData[position].type) {
            "text" -> TYPE_TEXT
            "pdf" -> TYPE_PDF
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class TextViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bindText(item: ModelInheritanceLaw.Data) {
            val title = itemView.findViewById<TextView>(R.id.tvTextDetail)
            title.text = item.title
        }
    }

    class PdfViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bindPdf(item: ModelInheritanceLaw.Data) {
            val title = itemView.findViewById<TextView>(R.id.tvOpenPdf)
            title.text = item.title
        }
    }

    companion object {
        private const val TYPE_TEXT = 1
        private const val TYPE_PDF = 2
    }
}