package com.taibahai.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.Download
import androidx.recyclerview.widget.RecyclerView
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.network.utils.StringUtils
import com.taibahai.R
import com.taibahai.databinding.ItemQuranChapterBinding
import com.taibahai.room_database.FavModel
import com.taibahai.room_database.SurahDatabase
import com.taibahai.utils.StorageUtils
import java.util.concurrent.Executors

class AdapterQuranChapter(var showData: MutableList<ModelSurah>, var listener: OnItemClick) :
    RecyclerView.Adapter<AdapterQuranChapter.ViewHolder>() {
    lateinit var binding: ItemQuranChapterBinding
    var favModel: FavModel? = null
    private var selected = "-1"


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ViewHolder {
        binding =
            ItemQuranChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun setData(list: ArrayList<ModelSurah>) {
        showData = list
        notifyDataSetChanged()
    }

    @OptIn(UnstableApi::class) override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapterData = showData[position]
        binding.data = chapterData

        holder.itemView.setOnClickListener {
            if (chapterData.type == "play") {
                listener.onClick(position, "play")
            } else {
                listener.onClick(position, "download")
            }
        }

        holder.binding.play.setOnClickListener {
            listener.onClick(position, "play")

        }


        holder.binding.ivFavourite.setOnClickListener {
            if (!chapterData.fav) {
                favModel = FavModel(position = chapterData.id.toLong())
                Executors.newSingleThreadExecutor().execute {
                    SurahDatabase.getDatabase(context).surahDao().insertFav(favModel)
                    Log.d("Database", "Audio added to favorites. ID: ${chapterData.id}")
                }

                holder.binding.ivFavourite.setImageResource(R.drawable.heart3)
                chapterData.fav = true
                Toast.makeText(context, "Audio is added to favorites", Toast.LENGTH_SHORT).show()
            } else {
                Executors.newSingleThreadExecutor().execute {
                    SurahDatabase.getDatabase(context).surahDao().deleteFav(chapterData.id.toLong())
                    Log.d("Database", "Audio removed from favorites. ID: ${chapterData.id}")
                }

                holder.binding.ivFavourite.setImageResource(R.drawable.heartt2)
                chapterData.fav = false
                Toast.makeText(context, "Audio is removed from favorites", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        if (chapterData.download != null) {
            val status = chapterData.download!!.state

            when (status) {
                Download.STATE_COMPLETED -> {
                    val filePath = chapterData.getCurrentFile(context)?.absolutePath


                    if (StorageUtils.isFileExists(filePath)) {
                        holder.binding.progress.visibility = View.GONE
                        holder.binding.play.setImageResource(R.drawable.ic_delete)
                       // holder.binding.play.setOnClickListener { listener.onDelete(chapterData) }
                        //holder.itemView.setOnClickListener { listener.onPlayClick(chapterData) }
                    } else {
                        holder.binding.play.setImageResource(R.drawable.ic_download)
                        //holder.binding.playLayout.setOnClickListener { listener.onDownload(chapterData) }
                        //holder.itemView.setOnClickListener { listener.onDownload(chapterData) }
                    }
                }
//                Download.STATE_PAUSED, Download.STATE_ADDED -> {
//                    holder.binding.progress.visibility = View.GONE
//                    holder.binding.play.setImageResource(R.drawable.ic_download)
//                    holder.binding.playLayout.setOnClickListener { listener.onResume(chapterData) }
//                    holder.itemView.setOnClickListener { listener.onResume(chapterData) }
//                }
//                Download.STATE_REMOVING, Download.STATE_DELETED -> {
//                    holder.binding.progress.visibility = View.GONE
//                    holder.binding.play.setImageResource(R.drawable.ic_download)
//                    holder.binding.playLayout.setOnClickListener { listener.onDownload(chapterData) }
//                    holder.itemView.setOnClickListener { listener.onDownload(chapterData) }
//                }
                Download.STATE_FAILED -> {
                    holder.binding.progress.visibility = View.GONE
                    holder.binding.play.setImageResource(R.drawable.ic_download)
//                    holder.binding.playLayout.setOnClickListener { listener.onRetryDownload(chapterData) }
//                    holder.itemView.setOnClickListener { listener.onRetryDownload(chapterData) }
                }
                Download.STATE_DOWNLOADING -> {
                    holder.binding.progress.visibility = View.VISIBLE
//                    holder.binding.progress.setIndeterminateMode(true)
//                    holder.binding.playLayout.setOnClickListener { listener.onPause(chapterData) }
                }
                Download.STATE_QUEUED -> {
                    holder.binding.progress.visibility = View.VISIBLE
//                    holder.binding.progress.setIndeterminateMode(false)
//                    holder.binding.play.setImageResource(R.drawable.ic_pin)
//                    holder.binding.playLayout.setOnClickListener { listener.onPause(chapterData) }
                }
                else -> {
                    // Handle other states if needed
                }
            }
        } else {
            holder.binding.progress.visibility = View.GONE
            holder.binding.play.setImageResource(R.drawable.ic_download)
            //holder.binding.playLayout.setOnClickListener { listener.onDownload(chapterData) }
            //holder.itemView.setOnClickListener { listener.onDownload(chapterData) }
        }

    }

    fun updateView(downloadId: Long) {
        for (position in 0 until showData.size) {
            val downloadData = showData[position]
            if (downloadData.downloadId.toLong() == downloadId) {
                notifyItemChanged(position)
                return
            }
        }
    }

    fun updateView(id: String) {
        selected = id
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemQuranChapterBinding) :
        RecyclerView.ViewHolder(binding.root)
}