package com.taibahai.quran

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.taibahai.R
import com.taibahai.databinding.ItemQuranChapterBinding
import com.taibahai.quran.StringUtils.getNameFromUrl
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Status
import java.io.File

class AllSurahListAdapter(private val context: Context) :
    RecyclerView.Adapter<AllSurahListAdapter.HomeListHolder>() {
    private var mData: List<SurahListModel>
    var layoutInflater: LayoutInflater
    var listener: OnPlayListener? = null
    private var selected = "-1"

    //    RoomDatabaseRepository dbRepository;
    init {
        mData = ArrayList()
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //        dbRepository = new RoomDatabaseRepository(context);
    }

    fun updateData(mData: List<SurahListModel>) {
        this.mData = mData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeListHolder {
        return HomeListHolder(ItemQuranChapterBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: HomeListHolder,
        @SuppressLint("RecyclerView") position: Int
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
        holder.binding.name.text = model.transliterationEn
        holder.binding.meaning.text =
            java.lang.String.format("%s (%s)", model.transliterationEn, model.totalVerses)
        holder.binding.number.text = model.number
        holder.binding.checkBox.isSelected = model.isFav
//        loadImageFromDrawable(context, R.drawable.surah, holder.binding.image, R.drawable.surah)


        // handling download statuses
        if (model.download != null) {
            val status = model.download!!.status
            when (status) {
                Status.COMPLETED -> {
                    val child = StringUtils.SURAH_FOLDER + getNameFromUrl(model.audio)
                    holder.binding.progress.visibility = View.GONE
                    if (isFileExists(child, context)) {
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

        //clicks
        holder.binding.checkBox.setOnClickListener(object : View.OnClickListener {
            var favModel: FavModel? = null
            override fun onClick(v: View) {
                if (!model.isFav) {
                    favModel = model.id?.let { FavModel(it.toLong()) }
                    // dbRepository.addToFavSurah(favModel);
                    holder.binding.checkBox.isSelected = true
                    model.isFav = true
                    Log.d("response", "park id inserted: " + model.id)
                } else {
                    //  dbRepository.deleteFromFavSurah(Integer.parseInt(model.getId()));
                    holder.binding.checkBox.isSelected = false
                    model.isFav = false
                    Log.d("response", "park id deleted: " + model.id)
                }
                //                notifyItemChanged(position);
            }
        })
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

    class HomeListHolder(var binding: ItemQuranChapterBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    fun updateView(download: Download) {
        for (position in mData.indices) {
            val downloadData = mData[position]
            if (downloadData.getDownloadId(context) == download.id) {
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

    companion object {
        fun loadImageFromDrawable(context: Context?, url: Int, imageView: ImageView?, error: Int) {
            try {
                Glide.with(context!!).load(url)
                    .error(error)
                    .into(imageView!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun isFileExists(childPath: String?, context: Context): Boolean {
            val yourFile = File(getAudioOutputDirectory(context), childPath)
            return yourFile.exists()
        }

        fun getAudioOutputDirectory(context: Context): File {
            val mediaStorageDir = File(
                context.filesDir.toString() + "/" +
                        context.getString(R.string.app_name) + "/Audios"
            )
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            return mediaStorageDir
        }
    }
}