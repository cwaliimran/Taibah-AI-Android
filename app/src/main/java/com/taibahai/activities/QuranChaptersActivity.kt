package com.taibahai.activities

import android.Manifest
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import com.network.utils.GlobalClass
import com.network.utils.StringUtils
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.utils.DownloadMusicFile
import com.taibahai.utils.FileDownloader
import org.json.JSONException
import java.io.File


class QuranChaptersActivity : BaseActivity() {
    lateinit var binding: ActivityQuranChaptersBinding
    lateinit var adapter: AdapterQuranChapter
    var modelSurahList = mutableListOf<ModelSurah>()
    var model = ModelSurah()
    val fileDownloader = FileDownloader(this)
    var isDownload = false
    private val TAG = "QuranChaptersActivity"

    override fun onCreate() {
        binding = ActivityQuranChaptersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appbar.tvTitle.setText("Quran")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.heartt))
        initDownload()

    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
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
                        isDownload  = true

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

                     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                            TedPermission.with(context)
                                .setPermissionListener(object : PermissionListener() {
                                    fun onPermissionGranted() {
                                        Log.d(
                                            TAG,
                                            "onPlay: " + GlobalClass.BASE_URL_1 + model.getAudio()
                                        )
                                        if (GlobalClass.isOnline(context)) {
                                            if (isDownload) downloadAudio(model.getAudio())
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.turn_on_internet),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                    fun onPermissionDenied(deniedPermissions: List<String?>?) {}
                                })
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(
                                    Manifest.permission.READ_MEDIA_AUDIO,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ).check()
                        } else {
                            TedPermission.with(context)
                                .setPermissionListener(object : PermissionListener() {
                                    fun onPermissionGranted() {
                                        Log.d(
                                            TAG,
                                            "onPlay: " + GlobalClass.BASE_URL_1 + model.getAudio()
                                        )
                                        if (GlobalClass.isOnline(context)) {
                                            if (isDownload) downloadAudio(model.getAudio())
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.turn_on_internet),
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                    fun onPermissionDenied(deniedPermissions: List<String?>?) {}
                                })
                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ).check()
                        }*/
                    }
                    "play"->{
                        val intent= Intent(context, ChapterDetailActivity::class.java)
                        intent.putExtra("ayat_id",modelSurahList[position].id )
                        intent.putExtra("ayat_name",modelSurahList[position].transliteration_en )
                        intent.putExtra("ayat_verse",modelSurahList[position].total_verses )
                        intent.putExtra("ayat_type",modelSurahList[position].revelation_type )
                        startActivity(intent)
                    }
                    else -> {}
                }
            }
        })
        binding.rvQuranChapter.adapter = adapter




        loadData()
    }

    private fun initDownload() {
        val savedDownloadRequest = fileDownloader.getSavedDownloadRequest()

        if (savedDownloadRequest != null) {
            val (url, title, _) = savedDownloadRequest
            val downloadId = fileDownloader.downloadFile(url, title, "Download Description")



            fileDownloader.saveDownloadRequest("", "", "")
        }
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
//                if (isDownload) downloadAudio(model.audio)
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
            Toast.makeText(context, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT).show()
            // Handle denied permissions
        }
    }

    fun downloadAudio(s: String) {
//        val child: String = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(s)
//        val file: File = File(getAudioOutputDirectory(), child)
//        val audio_path = file.absolutePath
//        Log.d(TAG, "downloadAudio: $audio_path")
        Log.d(TAG, "downloadAudio: $s")
        val downloadMusicFile = DownloadMusicFile(this)
        downloadMusicFile.downloadMusicFile(s)
      /*  request = Request(GlobalClass.BASE_URL_1 + s, audio_path)
        request.setPriority(Priority.HIGH)
        request.setNetworkType(NetworkType.ALL)
        request.setGroupId(StringUtils.SURAH_GROUP_ID)
        //        request.addHeader(CLIENT_KEY, StringUtils.CLIENT_KEY_HEADER);
        sharedPref.storeString(StringUtils.PREV_SURAH_URL, request.getUrl())
        sharedPref.storeString(StringUtils.PREV_SURAH_FILEPATH, request.getFile())
        fetch.enqueue(request, { updatedRequest -> }) { error -> }*/
    }


}