package com.taibahai.quran

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.network.interfaces.OnItemClick
import com.network.utils.AppClass.Companion.isFileExists
import com.taibahai.R
import com.taibahai.databinding.ItemQuranChapterBinding
import com.taibahai.quran.StringUtils.getNameFromUrl
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status

class AllSurahListAdapter(private val context: Context, val clickListener: OnItemClick) :
    RecyclerView.Adapter<AllSurahListAdapter.ViewHolder>() {
    private var mData: List<SurahListModel>
    var layoutInflater: LayoutInflater
    var listener: OnPlayListener? = null
    private var selected = "-1"

    init {
        mData = ArrayList()
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun updateData(mData: List<SurahListModel>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemQuranChapterBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        // showing data
        val model = mData[position]
        if (selected == model.id) {
            holder.binding.name.setTextColor(Color.RED)
            holder.binding.meaning.setTextColor(Color.RED)
        } else {
            holder.binding.name.setTextColor(Color.BLACK)
            holder.binding.meaning.setTextColor(Color.BLACK)
        }
        holder.binding.name.text = model.transliteration_en
        holder.binding.meaning.text =
            java.lang.String.format("%s (%s)", model.translation_en, model.total_verses)
        holder.binding.number.text = model.number
        holder.binding.ivFav.isSelected = model.isFav

        holder.binding.ivFav.setOnClickListener {
            clickListener.onClick(holder.absoluteAdapterPosition)
        }


        // handling download statuses
        if (model.download != null) {
            val status = model.download!!.status
            when (status) {
                Status.COMPLETED -> {
                    val child = StringUtils.SURAH_FOLDER + getNameFromUrl(model.audio)
                    holder.binding.progress.visibility = View.GONE
                    if (isFileExists(child)) {
                        holder.binding.play.setImageResource(R.drawable.ic_delete)
                        holder.binding.play.setOnClickListener { v: View? ->
                            listener!!.onDelete(
                                model
                            )
                        }
                        holder.itemView.setOnClickListener { v: View? ->
                            listener!!.onPlayClick(
                                model
                            )
                        }
                    } else {
                        holder.binding.play.setImageResource(R.drawable.ic_download)
                        holder.binding.playLayout.setOnClickListener { v: View? ->
                            listener!!.onDownload(
                                model
                            )
                        }
                        holder.itemView.setOnClickListener { v: View? -> listener!!.onDownload(model) }
                    }
                }

                Status.PAUSED, Status.ADDED -> {
                    holder.binding.progress.visibility = View.GONE
                    holder.binding.play.setImageResource(R.drawable.ic_download)
                    holder.binding.playLayout.setOnClickListener { v: View? ->
                        listener!!.onResume(
                            model
                        )
                    }
                    holder.itemView.setOnClickListener { v: View? -> listener!!.onResume(model) }
                }

                Status.REMOVED, Status.DELETED -> {
                    holder.binding.progress.visibility = View.GONE
                    holder.binding.play.setImageResource(R.drawable.ic_download)
                    holder.binding.playLayout.setOnClickListener { v: View? ->
                        listener!!.onDownload(
                            model
                        )
                    }
                    holder.itemView.setOnClickListener { v: View? -> listener!!.onDownload(model) }
                }

                Status.FAILED -> {
                    holder.binding.progress.visibility = View.GONE
                    holder.binding.play.setImageResource(R.drawable.ic_download)
                    holder.binding.playLayout.setOnClickListener { v: View? ->
                        listener!!.onRetryDownload(
                            model
                        )
                    }
                    holder.itemView.setOnClickListener { v: View? ->
                        listener!!.onRetryDownload(
                            model
                        )
                    }
                }

                Status.DOWNLOADING -> {
                    holder.binding.progress.visibility = View.VISIBLE
                    holder.binding.progress.indeterminateMode = true
                    holder.binding.play.setImageResource(R.drawable.ic_pin)
                    holder.binding.playLayout.setOnClickListener { v: View? ->
                        listener!!.onPause(
                            model
                        )
                    }
                }

                Status.QUEUED -> {
                    holder.binding.progress.visibility = View.VISIBLE
                    holder.binding.progress.indeterminateMode = false
                    holder.binding.play.setImageResource(R.drawable.ic_pin)
                    holder.binding.playLayout.setOnClickListener { v: View? ->
                        listener!!.onPause(
                            model
                        )
                    }
                }

                else -> {}
            }
        } else {
            holder.binding.progress.visibility = View.GONE
            holder.binding.play.setImageResource(R.drawable.ic_download)
            holder.binding.playLayout.setOnClickListener { v: View? -> listener!!.onDownload(model) }
            holder.itemView.setOnClickListener { v: View? -> listener!!.onDownload(model) }
        }


    }

    fun setOnItemClickListener(listener: OnPlayListener?) {
        this.listener = listener
    }

    interface OnPlayListener {
        fun onDownload(model: SurahListModel?)
        fun onDelete(model: SurahListModel?)
        fun onPause(model: SurahListModel?)
        fun onResume(model: SurahListModel?)
        fun onRetryDownload(model: SurahListModel?)
        fun onPlayClick(model: SurahListModel?)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(var binding: ItemQuranChapterBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    fun updateView(download: Download) {
        for (position in mData.indices) {
            val downloadData = mData[position]
            if (downloadData.getDownloadId() == download.id) {
                downloadData.download = download
                notifyItemChanged(position)
                return
            }
        }
    }

    fun updateView(id: String) {
        selected = id
        notifyDataSetChanged()
    }

}