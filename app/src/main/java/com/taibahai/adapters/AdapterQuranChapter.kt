package com.taibahai.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.Status
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.network.utils.AppClass
import com.network.utils.StringUtils
import com.taibahai.databinding.ItemQuranChapterBinding

class AdapterQuranChapter(var showData: MutableList<ModelSurah>, var listener: OnItemClick):RecyclerView.Adapter<AdapterQuranChapter.ViewHolder>() {
    lateinit var binding: ItemQuranChapterBinding

    override fun onCreateViewHolder(  parent: ViewGroup,viewType: Int): AdapterQuranChapter.ViewHolder {
        binding = ItemQuranChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQuranChapter.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelSurah>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterQuranChapter.ViewHolder, position: Int) {
        val chapterData = showData[position]
        binding.data = chapterData

       /* if (chapterData.download != null) {
            val status: Status = chapterData.download.stat
            when (status) {
                COMPLETED -> {
                    val child: String =
                        StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(chapterData.audio)
                    if (AppClass.isFileExists(child)) {
                        holder.binding.imageView.setImageResource(R.drawable.ic_delete)
                        holder.binding.play.setOnClickListener { v -> listener.onDelete(model) }
                        holder.itemView.setOnClickListener { v: View? ->
                            listener.onPlayClick(
                                model
                            )
                        }
                    } else {
                        holder.binding.play.setImageResource(R.drawable.ic_download)
                        holder.binding.playLayout.setOnClickListener { v ->
                            listener.onDownload(
                                model
                            )
                        }
                        holder.itemView.setOnClickListener { v: View? ->
                            listener.onDownload(
                                model
                            )
                        }
                    }
                }

                PAUSED, ADDED -> {
                    holder.binding.progress.setVisibility(View.GONE)
                    holder.binding.play.setImageResource(R.drawable.ic_download)
                    holder.binding.playLayout.setOnClickListener { v -> listener.onResume(model) }
                    holder.itemView.setOnClickListener { v: View? ->
                        listener.onResume(
                            model
                        )
                    }
                }

                REMOVED, DELETED -> {
                    holder.binding.progress.setVisibility(View.GONE)
                    holder.binding.play.setImageResource(R.drawable.ic_download)
                    holder.binding.playLayout.setOnClickListener { v -> listener.onDownload(model) }
                    holder.itemView.setOnClickListener { v: View? ->
                        listener.onDownload(
                            model
                        )
                    }
                }

                FAILED -> {
                    holder.binding.progress.setVisibility(View.GONE)
                    holder.binding.play.setImageResource(R.drawable.ic_download)
                    holder.binding.playLayout.setOnClickListener { v ->
                        listener.onRetryDownload(
                            model
                        )
                    }
                    holder.itemView.setOnClickListener { v: View? ->
                        listener.onRetryDownload(
                            model
                        )
                    }
                }

                DOWNLOADING -> {
                    holder.binding.progress.setVisibility(View.VISIBLE)
                    holder.binding.progress.setIndeterminateMode(true)
                    holder.binding.play.setImageResource(R.drawable.ic_pin)
                    holder.binding.playLayout.setOnClickListener { v -> listener.onPause(model) }
                }

                QUEUED -> {
                    holder.binding.progress.setVisibility(View.VISIBLE)
                    holder.binding.progress.setIndeterminateMode(false)
                    holder.binding.play.setImageResource(R.drawable.ic_pin)
                    holder.binding.playLayout.setOnClickListener { v -> listener.onPause(model) }
                }

                else -> {}
            }
        }
*/










        holder.itemView.setOnClickListener {
            listener.onClick(position,"type", chapterData)


        }

    }




    override fun getItemCount(): Int {
        return showData.size
    }
    class ViewHolder(val binding: ItemQuranChapterBinding) : RecyclerView.ViewHolder(binding.root) {}
}