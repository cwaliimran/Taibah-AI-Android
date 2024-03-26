package com.taibahai.quran

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.util.JsonUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.network.network.NetworkUtils.isInternetAvailable
import com.network.utils.AppClass
import com.network.utils.ProgressLoading.displayLoading
import com.taibahai.R
import com.taibahai.audioPlayer.AudioPlayer.Companion.instance
import com.taibahai.audioPlayer.AudioPlayer.OnViewClickListener
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.notifications.MediaNotificationManager
import com.taibahai.quran.AllSurahListAdapter.OnPlayListener
import com.taibahai.utils.AppJsonUtils
import com.taibahai.utils.Constants
import com.tonyodev.fetch2.AbstractFetchListener
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2.FetchConfiguration.Builder
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2.NetworkType
import com.tonyodev.fetch2.Priority
import com.tonyodev.fetch2.Request
import com.tonyodev.fetch2.Status
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.Serializable
import java.util.Collections

class QuranChaptersActivity : AppCompatActivity() {
    var BASE_URL_1 = "https://taibahislamic.com/admin/"
    var isFavourite = false
    var isDownload = true
    var isPlaySuffle = false
    var isPlaySimple = false
    var jsonArr: JSONArray? = null
    private var mData: MutableList<SurahListModel>? = null
    private var mFilteredData: MutableList<SurahListModel>? = null
    private var mPlayerList: MutableList<SurahListModel?>? = null
    private var allSurahListAdapter: AllSurahListAdapter? = null
    private var favModels: List<FavModel>? = null
    var fetchConfiguration: FetchConfiguration? = null
    var fetch: Fetch? = null
    var request: Request? = null
    var binding: ActivityQuranChaptersBinding? = null
    var currentIndex = 0
    var currentFile = ""
    var surahName = ""
    var surahId = ""
    var model: SurahListModel? = null

    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuranChaptersBinding.inflate(layoutInflater)
        context = this
        initViews()
        initDownloader()
        initAdapter()
        initClicks()
        initData()
        setContentView(binding!!.root)
    }

    private fun initViews() {
        binding!!.recyclerView.isNestedScrollingEnabled = false
        mData = ArrayList()
        mFilteredData = ArrayList()
        favModels = ArrayList()
        mPlayerList = ArrayList()
    }

    public override fun onResume() {
        super.onResume()
        initAudioPlay()
        if (!mData!!.isEmpty()) {
            downloads
        }
    }

    private fun initDownloader() {
        fetchConfiguration = Builder(context).build()
        fetch = getInstance(fetchConfiguration!!)
        val requestUrl = AppClass.sharedPref.getString(StringUtils.PREV_SURAH_URL, "")
        val requestPath = AppClass.sharedPref.getString(StringUtils.PREV_SURAH_FILEPATH, "")
        if (!requestUrl!!.isEmpty() || !requestPath!!.isEmpty()) {
            request = Request(requestUrl, requestPath!!)
            request!!.groupId = StringUtils.SURAH_GROUP_ID
            fetch!!.getDownload(request!!.id) { result: Download? ->
                if (result != null) {
                    if (result.status != Status.COMPLETED) {
                        fetch!!.enqueue(
                            request!!,
                            { updatedRequest: Request? -> }) { error: Error? ->
                            Log.d(
                                TAG, "call: " + result.error
                            )
                        }
                    }
                }
            }.addListener(fetchListener)
        }
    }

    //                Collections.sort(list, (first, second) -> Long.compare(first.getCreated(), second.getCreated()));
    private val downloads: Unit
        private get() {
            fetch!!.getDownloadsInGroup(StringUtils.SURAH_GROUP_ID) { downloads: List<Download>? ->
                val list = ArrayList(downloads)
                Log.d(TAG, "call: " + Gson().toJson(list))
                //                Collections.sort(list, (first, second) -> Long.compare(first.getCreated(), second.getCreated()));
                for (download in list) {
                    allSurahListAdapter!!.updateView(download)
                }
            }.addListener(fetchListener)
        }

    private fun initAdapter() {
        allSurahListAdapter = AllSurahListAdapter(context)
        binding!!.recyclerView.adapter = allSurahListAdapter
        allSurahListAdapter!!.setOnItemClickListener(object : OnPlayListener {
            override fun onDownload(model: SurahListModel?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    TedPermission.create().setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            if (isInternetAvailable()) {
                                if (isDownload) model?.audio?.let { downloadAudio(it) }
                            } else {
                                Toast.makeText(
                                    context,
                                    context!!.getString(R.string.turn_on_internet),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onPermissionDenied(deniedPermissions: List<String>) {}
                    })
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(
                            Manifest.permission.READ_MEDIA_AUDIO,
                            Manifest.permission.POST_NOTIFICATIONS
                        ).check()
                } else {
                    TedPermission.create().setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            if (isInternetAvailable()) {
                                if (isDownload) if (model != null) {
                                    model.audio?.let { downloadAudio(it) }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context!!.getString(R.string.turn_on_internet),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onPermissionDenied(deniedPermissions: List<String>) {}
                    })
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ).check()
                }
            }

            override fun onDelete(model: SurahListModel?) {
                if (model != null) {
                    fetch!!.remove(model.getDownloadId(context))
                }
            }

            override fun onPause(model: SurahListModel?) {
                AppClass.sharedPref.removeKey(StringUtils.PREV_SURAH_FILEPATH)
                AppClass.sharedPref.removeKey(StringUtils.PREV_SURAH_URL)
                if (model != null) {
                    fetch!!.pause(model.getDownloadId(context))
                }
            }

            override fun onResume(model: SurahListModel?) {
                if (model != null) {
                    fetch!!.resume(model.getDownloadId(context))
                }
            }

            override fun onRetryDownload(model: SurahListModel?) {
                if (model != null) {
                    fetch!!.retry(model.getDownloadId(context))
                }
            }

            override fun onPlayClick(result: SurahListModel?) {
                model = result
                isPlaySimple = false
                isPlaySuffle = false
                instance.release()
                updateUi()
                playerList
                gotoDetails()
            }
        })
    }

    private fun updateUi() {
        surahId = model!!.id.toString()
        currentFile = model!!.download?.file.toString()
        surahName = model!!.transliterationEn.toString()
        binding!!.playLayout.surahName.text = surahName
        allSurahListAdapter!!.updateView(surahId)
    }

    private fun gotoDetails() {
        val intent = Intent(context, Al_Quran_Details::class.java)
        intent.putExtra("ayat_id", model!!.id)
        intent.putExtra("ayat_name", model!!.transliterationEn)
        intent.putExtra("ayat_verse", model!!.totalVerses)
        intent.putExtra("ayat_type", model!!.revelationType)
        if (!mPlayerList!!.isEmpty()) {
            val args = Bundle()
            args.putSerializable(StringUtils.ARRAY, mPlayerList as Serializable?)
            intent.putExtra(StringUtils.BUNDLE, args)
        }
        startActivityForResult(intent, Constants.ACTIVITY_RESULT_CODE)
    }

    fun downloadAudio(s: String) {
        val child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(s)
        val file = File(getAudioOutputDirectory(context), child)
        val audio_path = file.absolutePath
        request = Request(BASE_URL_1 + s, audio_path)
        request!!.priority = Priority.HIGH
        request!!.networkType = NetworkType.ALL
        request!!.groupId = StringUtils.SURAH_GROUP_ID
        //        request.addHeader(CLIENT_KEY, StringUtils.CLIENT_KEY_HEADER);
        AppClass.sharedPref.storeString(StringUtils.PREV_SURAH_URL, request!!.url)
        AppClass.sharedPref.storeString(StringUtils.PREV_SURAH_FILEPATH, request!!.file)
        fetch!!.enqueue(request!!, { updatedRequest: Request? -> }) { error: Error? -> }
    }

    private fun initData() {
        try {
            mFilteredData!!.clear()
            jsonArr = JSONArray(AppJsonUtils.readRawResource(context, R.raw.allsurahlist))
            val gson = Gson()
            val type = object : TypeToken<List<SurahListModel?>?>() {}.type
            mFilteredData = gson.fromJson(jsonArr.toString(), type)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        loadJson()
        //        new getFavSurah().execute();
    }

    private fun initClicks() {

        binding!!.playLayout.play.setOnClickListener { v: View? ->
            if (!currentFile.isEmpty()) {
                startPlaying(currentFile)
                allSurahListAdapter!!.updateView(surahId)
            }
        }
//        binding!!.playLayout.fastForward.setOnClickListener { v: View? -> if (!mPlayerList!!.isEmpty()) playNext() }
        binding!!.playLayout.play.setOnClickListener { v: View? ->
            playerList
            if (!mPlayerList!!.isEmpty()) {
                instance.release()
                isPlaySimple = true
                isPlaySuffle = false
                currentIndex = -1
                playNext()
            } else {
                Toast.makeText(context, getString(R.string.no_surah_downloaded), Toast.LENGTH_SHORT)
                    .show()
            }
        }
//        binding!!.shuffleBtn.setOnClickListener { v: View? ->
//            playerList
//            if (!mPlayerList!!.isEmpty()) {
//                instance.release()
//                isPlaySimple = false
//                isPlaySuffle = true
//                currentIndex = -1
//                playNext()
//            } else {
//                Toast.makeText(context, getString(R.string.no_surah_downloaded), Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
    }

    private val playerList: Unit
        private get() {
            mPlayerList!!.clear()
            for (model in mData!!) {
                    if (model.download?.status == Status.COMPLETED) {
                        val child =
                            StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.audio)
                        if (isFileExists(child, context)) {
                            mPlayerList!!.add(model)
                        }
                }
            }
            if (isPlaySuffle) {
                Collections.shuffle(mPlayerList)
            }
        }

    fun updateCurrentIndex() {
        for (i in mPlayerList!!.indices) {
            val model = mPlayerList!![i]
            if (!surahId.isEmpty()) {
                if (surahId == model!!.id) {
                    currentIndex = i
                    break
                }
            }
        }
    }

    private fun playNext() {
        currentIndex++
        if (currentIndex > mPlayerList!!.size - 1) {
            currentIndex = 0
        }
        val model = mPlayerList!![currentIndex]
        if (model!!.download != null) {
            if (model.download?.status == Status.COMPLETED) {
                val child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(
                    model.audio
                )
                if (isFileExists(child, context)) {
                    surahId = model.id.toString()
                    currentFile = model.download?.file.toString()
                    surahName = model.transliterationEn.toString()
                    binding!!.playLayout.surahName.text = surahName
                    startPlaying(currentFile)
                    model.id?.let { allSurahListAdapter!!.updateView(it) }
                }
            }
        }
    }

    private fun loadJson() {
        mData!!.clear()
        for (model in mFilteredData!!) {
            val id = model.id?.toLong()
            if (id?.let { isFavSaved(it) } == true && isFavourite && id != -1L) {
                model.isFav = true
                mData!!.add(model)
            }
            if (!isFavourite) {
                model.isFav = id?.let { isFavSaved(it) } == true
                mData!!.add(model)
            }
        }
        allSurahListAdapter!!.updateData(mData!!)
        displayLoading(false)
//        binding!!.buttonsLayout.visibility = View.VISIBLE
//        binding!!.playView.visibility = View.VISIBLE
        downloads
//        if (mData!!.size == 0) {
//            binding!!.textView.visibility = View.VISIBLE
//        } else {
//            binding!!.textView.visibility = View.GONE
//        }
    }

    private fun isFavSaved(id: Long): Boolean {
        var check = false
        for (favModels in favModels!!) {
            val parkIdCurrent = favModels.position
            //            Log.d("response", "isSaved: " + parkIdCurrent + "");
            if (parkIdCurrent == id) {
                check = true
                break
            }
        }
        return check
    }

    //    @Override
    //    public void onChanged(Download download, @NotNull Reason reason) {
    //        if (request.getId() == download.getId()) {
    //            allSurahListAdapter.updateView(download);
    //        }
    //    }
    //    class getFavSurah extends AsyncTask<Void, Void, List<FavModel>> {
    //
    //        @Override
    //        protected List<FavModel> doInBackground(Void... voids) {
    //            return DatabaseClient
    //                    .getInstance(getActivity())
    //                    .getAppDatabase().surahDao()
    //                    .getAllfav();
    //        }
    //
    //        @Override
    //        protected void onPostExecute(List<FavModel> tasks) {
    //            super.onPostExecute(tasks);
    //            favModels.clear();
    //            favModels.addAll(tasks);
    //            new Handler().postDelayed(AlQuranFragment.this::loadJson, 200);
    //        }
    //    }
    private fun startPlaying(audio_url: String) {
        instance.setData(audio_url)
        instance.playOrPause()
        Handler(Looper.myLooper()!!).postDelayed({ MediaNotificationManager.showMediaNotification(surahName) }, 200)
    }

    private fun stopPlaying() {
        instance.stop()
    }

    private fun initAudioPlay() {
        instance.OnItemClickListener(object : OnViewClickListener {
            override fun onPlayStarted(duration: Int) {
                Log.d(TAG, "onPlayStarted: " + binding!!.playLayout.play.isSelected)
                binding!!.playLayout.play.isSelected = true
            }

            override fun updateDuration(duration: Int, currentPosition: Int) {
                if (!binding!!.playLayout.play.isSelected) binding!!.playLayout.play.isSelected =
                    true
            }

            override fun onPause() {
                binding!!.playLayout.play.isSelected = false
                allSurahListAdapter!!.updateView(StringUtils.NO_INDEX)
            }

            override fun onCompleted(mp1: MediaPlayer?) {
                binding!!.playLayout.play.isSelected = false
                allSurahListAdapter!!.updateView(StringUtils.NO_INDEX)
                if (isPlaySimple || isPlaySuffle) playNext()
            }
        })
    }

    var fetchListener: FetchListener = object : AbstractFetchListener() {
        override fun onError(download: Download, error: Error, throwable: Throwable?) {
            super.onError(download, error, throwable)
            Log.d(TAG, "onError: " + error.value + " " + error.httpResponse + " " + error.throwable)
            Log.d(
                TAG,
                "onError: " + error.throwable + " " + error.httpResponse + " " + error.throwable
            )
            assert(throwable != null)
            Toast.makeText(context, throwable!!.message + "", Toast.LENGTH_SHORT).show()
            allSurahListAdapter!!.updateView(download)
            isDownload = true
        }

        override fun onAdded(download: Download) {
            allSurahListAdapter!!.updateView(download)
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            allSurahListAdapter!!.updateView(download)
        }

        override fun onCompleted(download: Download) {
            allSurahListAdapter!!.updateView(download)
            isDownload = true
        }

        override fun onProgress(
            download: Download,
            etaInMilliSeconds: Long,
            downloadedBytesPerSecond: Long
        ) {
            if (isDownload) allSurahListAdapter!!.updateView(download)
            isDownload = false
            Log.d(TAG, "onProgress: " + downloadedBytesPerSecond / 1024)
        }

        override fun onPaused(download: Download) {
            allSurahListAdapter!!.updateView(download)
            isDownload = true
        }

        override fun onResumed(download: Download) {
            allSurahListAdapter!!.updateView(download)
        }

        override fun onCancelled(download: Download) {
            allSurahListAdapter!!.updateView(download)
        }

        override fun onRemoved(download: Download) {
            isDownload = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create().setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        allSurahListAdapter!!.updateView(download)
                        if (download.file != null && !download.file.isEmpty()) deleteFile(
                            download.file,
                            context
                        )
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.POST_NOTIFICATIONS
                    ).check()
            } else {
                TedPermission.create().setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        allSurahListAdapter!!.updateView(download)
                        if (download.file != null && !download.file.isEmpty()) deleteFile(
                            download.file,
                            context
                        )
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ).check()
            }
        }

        override fun onDeleted(download: Download) {
            allSurahListAdapter!!.updateView(download)
        }
    }

    public override fun onPause() {
        super.onPause()
        if (request != null) {
//            fetch.removeFetchObserversForDownload(request.getId(), this);
            fetch!!.removeListener(fetchListener)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        fetch!!.close()
        //        AudioPlayer.Companion.getInstance().release();
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ACTIVITY_RESULT_CODE && resultCode == RESULT_OK) {
            if (data!!.hasExtra(StringUtils.OBJECT)) {
                model = data.getSerializableExtra(StringUtils.OBJECT) as SurahListModel?
                updateUi()
                updateCurrentIndex()
            }
        }
    }

    companion object {
        private const val TAG = "AlQuranFragment"
        fun getAudioOutputDirectory(context: Context?): File {
            val mediaStorageDir = File(
                context!!.filesDir.toString() + "/" +
                        context.getString(R.string.app_name) + "/Audios"
            )
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs()
            }
            return mediaStorageDir
        }

        fun isFileExists(childPath: String?, context: Context?): Boolean {
            val yourFile = File(getAudioOutputDirectory(context), childPath)
            return yourFile.exists()
        }

        fun deleteFile(filePath: String?, context: Context?): Boolean {
            val dir = context!!.filesDir
            val file = File(dir, filePath)
            return file.delete()
        }
    }
}