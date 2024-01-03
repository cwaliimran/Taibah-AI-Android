package com.taibahai.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.taibahai.R
import com.taibahai.databinding.ItemQuranChapterBinding
import com.taibahai.room_database.FavModel
import com.taibahai.room_database.SurahDatabase
import java.util.concurrent.Executors

class AdapterQuranChapter(var showData: MutableList<ModelSurah>, var listener: OnItemClick) :
    RecyclerView.Adapter<AdapterQuranChapter.ViewHolder>() {
    lateinit var binding: ItemQuranChapterBinding
    var favModel: FavModel? = null


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): AdapterQuranChapter.ViewHolder {
        binding =
            ItemQuranChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterQuranChapter.ViewHolder(binding)
    }

    fun setDate(list: ArrayList<ModelSurah>) {
        showData = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: AdapterQuranChapter.ViewHolder, position: Int) {
        val chapterData = showData[position]
        binding.data = chapterData

        holder.itemView.setOnClickListener {
            if (chapterData.type == "play") {
                listener.onClick(position, "play")
            } else {
                listener.onClick(position, "download")
            }
        }

        holder.binding.ivPlay.setOnClickListener {
            listener.onClick(position, "play")

        }

        holder.binding.ivPlay.setOnClickListener {
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
            }
            else {
                Executors.newSingleThreadExecutor().execute {
                    SurahDatabase.getDatabase(context).surahDao().deleteFav(chapterData.id.toLong())
                    Log.d("Database", "Audio removed from favorites. ID: ${chapterData.id}")
                }

                holder.binding.ivFavourite.setImageResource(R.drawable.heartt2)
                chapterData.fav = false
                Toast.makeText(context, "Audio is removed from favorites", Toast.LENGTH_SHORT) .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return showData.size
    }

    class ViewHolder(val binding: ItemQuranChapterBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}