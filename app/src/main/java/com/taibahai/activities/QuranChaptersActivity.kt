package com.taibahai.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.databinding.DataBindingUtil.setContentView
import androidx.media3.exoplayer.offline.Download
import com.cwnextgen.amnames.utils.getJsonDataFromAsset
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.network.models.ModelSurahList
import com.network.network.NetworkUtils.isInternetAvailable
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.getAudioOutputDirectory
import com.network.utils.AppClass.Companion.sharedPref
import com.network.utils.StringUtils
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.utils.FileDownloader
import com.taibahai.utils.StorageUtils
import org.json.JSONException
import java.io.File
import java.io.Serializable

class QuranChaptersActivity : BaseActivity() {
    lateinit var binding: ActivityQuranChaptersBinding
    lateinit var adapter: AdapterQuranChapter
    var modelSurahList = mutableListOf<ModelSurah>()
    var mData = mutableListOf<ModelSurah>()
    var model = ModelSurah()
    val fileDownloader = FileDownloader(this)
    var isDownload = false
    private val TAG = "QuranChaptersActivity"
    val ACTIVITY_RESULT_CODE = 123
    var audio_path = ""


    override fun onCreate() {
        binding = ActivityQuranChaptersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.tvTitle.setText("Quran")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.heartt))

    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        initAudioPlay()
        if (!modelSurahList.isEmpty()) {
            getDownloads()
        }
    }


    private fun initAudioPlay() {
        AudioPlayer.getInstance()!!.OnItemClickListener(object : AudioPlayer.OnViewClickListener {
            override fun onPlayStarted(duration: Int) {
                Log.d(TAG, "onPlayStarted: " + binding.ii.ivPlay.isSelected())
                binding.ii.ivPlay.setSelected(true)
            }

            override fun updateDuration(duration: Int, currentPosition: Int) {
                if (!binding.ii.ivPlay.isSelected()) binding.ii.ivPlay.setSelected(true)
            }

            override fun onPause() {
                binding.ii.ivPlay.setSelected(false)
                //adapter.updateView(StringUtils.NO_INDEX)
            }

            override fun onCompleted(mp1: MediaPlayer?) {
                binding.ii.ivPlay.setSelected(false)
                //adapter.updateView(StringUtils.NO_INDEX)

            }
        })
    }


    override fun initAdapter() {
        super.initAdapter()

        adapter = AdapterQuranChapter(modelSurahList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                super.onClick(position, type, data)
                isDownload = false
                model = modelSurahList[position]
                when (type) {
                    "download" -> {
                        isDownload = true

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            TedPermission.create()
                                .setPermissionListener(permissionListener)
                                .setDeniedMessage("If you reject permission, you cannot use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                                .check()


                        } else {
                            TedPermission.create()
                                .setPermissionListener(permissionListener)
                                .setDeniedMessage("If you reject permission, you cannot use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                )
                                .check()
                        }


                    }

                    "play" -> {
                        getPlayerList()
                        gotoDetails()
                    }


                    else -> {}


                }


            }
        })
        binding.rvQuranChapter.adapter = adapter




        loadData()
    }


    private fun gotoDetails() {
        val intent = Intent(context, ChapterDetailActivity::class.java)
        intent.putExtra("ayat_id", model.id)
        intent.putExtra("ayat_name", model.transliteration_en)
        intent.putExtra("ayat_verse", model.total_verses)
        intent.putExtra("ayat_type", model.type)


        if (!modelSurahList.isEmpty()) {
            val args = Bundle()
            args.putSerializable(StringUtils.ARRAY, modelSurahList as Serializable?)
            intent.putExtra(StringUtils.BUNDLE, args)
        }
        startActivityForResult(intent, ACTIVITY_RESULT_CODE)
    }


    fun getPlayerList(): List<ModelSurah> {
        val playerList = mutableListOf<ModelSurah>()

        for (model in mData) {
            val download = model.download

            if (download != null && download.state == Download.STATE_COMPLETED) {
                val child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.audio)
                if (StorageUtils.isFileExists(child)) {
                    playerList.add(model)
                }
            }
        }

        return playerList
    }


    @SuppressLint("Range")
    private fun getDownloads() {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val query = DownloadManager.Query()
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL)

        val cursor: Cursor = downloadManager.query(query)

        if (cursor.moveToFirst()) {
            do {
                val downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))

                adapter.updateView(downloadId)

            } while (cursor.moveToNext())
        }

        cursor.close()
    }


    private fun loadData() {
        try {

            val jsonFileString = getJsonDataFromAsset("allsurahlist")
            Log.d("TAG", "prepareDatabase: $jsonFileString")
            val dataMap = gson.fromJson(jsonFileString, ModelSurahList::class.java)
            modelSurahList.addAll(dataMap.surahList)
            adapter.notifyDataSetChanged()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


    val permissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Log.d(TAG, "onPlay: " + AppClass.BASE_URL_1 + model.audio)
            if (isInternetAvailable()) {
                if (isDownload) downloadAudio(AppClass.BASE_URL_1 + model.audio)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.turn_on_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(context, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT)
                .show()
            // Handle denied permissions
        }
    }

    fun downloadAudio(s: String) {
        val audioUrl = s

        val child: String = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(s)
        val file = File(getAudioOutputDirectory(), child)
        audio_path = file.absolutePath
        Log.d(TAG, "downloadAudio: $audio_path")

        val request = DownloadManager.Request(Uri.parse(audioUrl))
        val destinationUri = Uri.fromFile(
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                child
            )
        )
        request.setDestinationUri(destinationUri)

        // Enqueue the download request
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        // Save the download ID and file path in SharedPreferences
        sharedPref.storeLong(StringUtils.PREV_SURAH_URL, downloadId)
        sharedPref.storeString(StringUtils.PREV_SURAH_FILEPATH, destinationUri.toString())


        /* val downloadMusicFile = DownloadMusicFile(context, object : DownloadListener {
             override fun onDownloadComplete(file: File) {
                 val filePath = file.absolutePath
                 Log.d(TAG, "Downloaded audio file path: $filePath")
                 Toast.makeText(context, "Completed.", Toast.LENGTH_SHORT).show()

             }

             override fun onDownloadFailed(error: String) {
                 // Handle download failure
                 Toast.makeText(context, "Download failed: $error", Toast.LENGTH_SHORT).show()
             }
         })*/

        //    downloadMusicFile.downloadMusicFile(s)

    }


}