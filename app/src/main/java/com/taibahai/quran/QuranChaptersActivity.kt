package com.taibahai.quran

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.network.interfaces.OnItemClick
import com.network.network.NetworkUtils.isInternetAvailable
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.BASE_URL_1
import com.network.utils.AppClass.Companion.getAudioOutputDirectory
import com.network.utils.AppClass.Companion.getTimeString
import com.network.utils.AppClass.Companion.isFileExists
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.taibahai.R
import com.taibahai.audioPlayer.AudioPlayer.Companion.instance
import com.taibahai.audioPlayer.AudioPlayer.OnViewClickListener
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.databinding.DialogHistoryBinding
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

class QuranChaptersActivity : AppCompatActivity() {
    var isDownload = true
    var isPlaySuffle = false
    var isPlaySimple = false
    var jsonArr: JSONArray? = null
    private var mData: MutableList<SurahListModel> = mutableListOf()
    private var mPlayerList: MutableList<SurahListModel?>? = null
    private lateinit var allSurahListAdapter: AllSurahListAdapter
    var fetchConfiguration: FetchConfiguration? = null
    var fetch: Fetch? = null
    var request: Request? = null
    lateinit var binding: ActivityQuranChaptersBinding
    var currentIndex = 0
    var currentFile = ""
    var surahName = ""
    var surahId = ""
    var model: SurahListModel = SurahListModel()
    private val TAG = "QuranChaptersActivity"
    lateinit var context: Context
    private var favSurahs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuranChaptersBinding.inflate(layoutInflater)
        context = this
        initViews()
        initDownloader()
        initAdapter()
        initClicks()
        initData()
        setContentView(binding.root)
        askNotificationPermission()
    }

    private fun initViews() {
        binding.recyclerView.isNestedScrollingEnabled = false
        mData = ArrayList()
        mPlayerList = ArrayList()
        favSurahs = mutableListOf()
        binding.appbar.tvTitle.text = getString(R.string.chapters)
        binding.playLayout.seekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                instance.removeCallbacks()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                instance.seek(seekBar.progress.toLong())
            }
        })
        binding.playLayout.ivFav.setOnClickListener {
            if (mData[currentIndex].isFav) {
                mData[currentIndex].isFav = false
                favSurahs.remove(mData[currentIndex].number)
                AppClass.sharedPref.storeList(AppConstants.FAV_SURAHS, favSurahs)
            } else {
                favSurahs.add(mData[currentIndex].number)
                AppClass.sharedPref.storeList(AppConstants.FAV_SURAHS, favSurahs)
                mData[currentIndex].isFav = true
            }
            binding.playLayout.ivFav.isSelected = mData[currentIndex].isFav

            allSurahListAdapter.notifyItemChanged(currentIndex)

        }
    }

    public override fun onResume() {
        super.onResume()
        initAudioPlay()
        if (!mData.isEmpty()) {
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
                        }
                    }
                }
            }.addListener(fetchListener)
        }
    }

    private val downloads: Unit
        private get() {
            fetch!!.getDownloadsInGroup(StringUtils.SURAH_GROUP_ID) { downloads: List<Download>? ->
                val list = ArrayList(downloads)
                for (download in list) {
                    allSurahListAdapter.updateView(download)
                }
            }.addListener(fetchListener)
        }

    private fun initAdapter() {
        allSurahListAdapter = AllSurahListAdapter(context, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                if (mData[position].isFav) {
                    mData[position].isFav = false
                    favSurahs.remove(mData[position].number)
                    AppClass.sharedPref.storeList(AppConstants.FAV_SURAHS, favSurahs)
                } else {
                    favSurahs.add(mData[position].number)
                    AppClass.sharedPref.storeList(AppConstants.FAV_SURAHS, favSurahs)
                    mData[position].isFav = true
                }
                allSurahListAdapter.notifyItemChanged(position)
                if (position == currentIndex) {
                    binding.playLayout.ivFav.isSelected = model.isFav
                }
            }
        })
        binding.recyclerView.adapter = allSurahListAdapter
        allSurahListAdapter.setOnItemClickListener(object : OnPlayListener {
            override fun onDownload(model: SurahListModel?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    TedPermission.create().setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            if (isInternetAvailable()) {
                                if (isDownload) model?.audio?.let { downloadAudio(it) }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.turn_on_internet),
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
                                    model.audio.let { downloadAudio(it) }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.turn_on_internet),
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
                    val dialog = Dialog(context)
                    val layoutInflater = LayoutInflater.from(context)
                    val binding = DialogHistoryBinding.inflate(layoutInflater)
                    binding.tvConfirmation.text = getString(R.string.sure_delete)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(binding.root)
                    dialog.setCancelable(false)
                    binding.btnCancel.setOnClickListener {
                        dialog.dismiss()
                    }

                    binding.btnYes.setOnClickListener {
                        fetch!!.remove(model.getDownloadId())
                        dialog.dismiss()
                    }

                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    dialog.window!!.setLayout(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )

                    dialog.show()
                }
            }

            override fun onPause(model: SurahListModel?) {
                AppClass.sharedPref.removeKey(StringUtils.PREV_SURAH_FILEPATH)
                AppClass.sharedPref.removeKey(StringUtils.PREV_SURAH_URL)
                if (model != null) {
                    fetch!!.pause(model.getDownloadId())
                }
            }

            override fun onResume(model: SurahListModel?) {
                if (model != null) {
                    fetch!!.resume(model.getDownloadId())
                }
            }

            override fun onRetryDownload(model: SurahListModel?) {
                if (model != null) {
                    fetch!!.retry(model.getDownloadId())
                }
            }

            override fun onPlayClick(result: SurahListModel?) {
                if (result != null) {
                    model = result
                }
                isPlaySimple = false
                isPlaySuffle = false
                instance.release()
                updateUi()
                playerList
                currentIndex = model.number.toInt() - 1
                gotoDetails()
            }
        })
    }

    private fun updateUi() {
        surahId = model.id
        currentFile = model.download?.file.toString()
        surahName = model.transliteration_en.toString()
        binding.playLayout.surahName.text = surahName
        binding.playLayout.tvSurahNo.text = model.number
        binding.playLayout.ivFav.isSelected = model.isFav
        binding.playLayout.tvMeaning.text =
            "${model.translation_en}(${model.total_verses})"
        allSurahListAdapter.notifyItemChanged(currentIndex)
    }

    private fun gotoDetails() {
        binding.playLayout.root.visibility = View.VISIBLE
        val intent = Intent(context, ChapterDetailActivity::class.java)
        intent.putExtra("ayat_id", model.id)
        intent.putExtra("ayat_name", model.transliteration_en)
        intent.putExtra("ayat_verse", model.total_verses)
        intent.putExtra("ayat_type", model.revelation_type)
        if (!mPlayerList!!.isEmpty()) {
            val args = Bundle()
            args.putSerializable(StringUtils.ARRAY, mPlayerList as Serializable?)
            intent.putExtra(StringUtils.BUNDLE, args)
        }
        startActivityForResult(intent, Constants.ACTIVITY_RESULT_CODE)
    }

    fun downloadAudio(s: String) {
        val child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(s)
        val file = File(getAudioOutputDirectory(), child)
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
        favSurahs = AppClass.sharedPref.getList(AppConstants.FAV_SURAHS)
        try {
            mData!!.clear()
            jsonArr = JSONArray(AppJsonUtils.readRawResource(context, R.raw.allsurahlist))
            val gson = Gson()
            val type = object : TypeToken<List<SurahListModel?>?>() {}.type
            mData = gson.fromJson(jsonArr.toString(), type)
            loadFav()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun initClicks() {
        binding.appbar.ivLeft.setOnClickListener {
            finish()
        }
        binding.playLayout.play.setOnClickListener { v: View? ->
            if (currentFile.isNotEmpty()) {
                startPlaying(currentFile)
                allSurahListAdapter.updateView(surahId)
            } else {
                playerList
                if (mPlayerList!!.isNotEmpty()) {
                    instance.release()
                    isPlaySimple = true
                    isPlaySuffle = false
                    currentIndex = -1
                    playNext()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.no_surah_downloaded),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
//        binding.playLayout.play.setOnClickListener { v: View? ->
//            playerList
//            if (mPlayerList!!.isNotEmpty()) {
//                instance.release()
//                isPlaySimple = true
//                isPlaySuffle = false
//                currentIndex = -1
//                playNext()
//            } else {
//                Toast.makeText(context, getString(R.string.no_surah_downloaded), Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
//        binding!!.playLayout.fastForward.setOnClickListener { v: View? -> if (!mPlayerList!!.isEmpty()) playNext() }
//        binding.playLayout.play.setOnClickListener { v: View? ->
//            playerList
//            if (!mPlayerList!!.isEmpty()) {
//                instance.release()
//                isPlaySimple = true
//                isPlaySuffle = false
//                currentIndex = -1
//                playNext()
//            } else {
//                Toast.makeText(context, getString(R.string.no_surah_downloaded), Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }
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
            for (model in mData) {
                if (model.download?.status == Status.COMPLETED) {
                    val child = StringUtils.SURAH_FOLDER + StringUtils.getNameFromUrl(model.audio)
                    if (isFileExists(child)) {
                        mPlayerList!!.add(model)
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
                if (isFileExists(child)) {
                    surahId = model.id.toString()
                    currentFile = model.download?.file.toString()
                    surahName = model.transliteration_en.toString()
                    binding.playLayout.surahName.text = surahName
                    binding.playLayout.tvSurahNo.text = model.number.toString()
                    startPlaying(currentFile)
                    model.id.let { allSurahListAdapter.updateView(it) }
                }
            }
        }
    }

    private fun loadFav() {
        mData.forEach { model ->
            val isFav = isFavSaved(model.number)
            model.isFav = isFav
        }

        allSurahListAdapter.updateData(mData)
        displayLoading(false)
        downloads
    }

    private fun isFavSaved(id: String): Boolean {
        return favSurahs.contains(id)
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
        Handler(Looper.myLooper()!!).postDelayed({
            MediaNotificationManager.showMediaNotification(
                surahName
            )
        }, 200)
    }

    private fun stopPlaying() {
        instance.stop()
    }

    private fun initAudioPlay() {
        instance.OnItemClickListener(object : OnViewClickListener {
            override fun onPlayStarted(duration: Int) {
                binding.playLayout.play.isSelected = true
                val total_duration = getTimeString(duration.toLong())
                binding.playLayout.play.isSelected = true
            }

            override fun updateDuration(duration: Int, currentPosition: Int, totalTrackTime: Int) {
                binding.playLayout.totalTime.text = getTimeString(totalTrackTime.toLong())

                val current_duration = getTimeString(currentPosition.toLong())
                binding.playLayout.currentTime.text = current_duration
                binding.playLayout.seekbar.progress = duration

                if (!binding.playLayout.play.isSelected) binding.playLayout.play.isSelected = true
            }

            override fun onPause() {
                binding.playLayout.play.isSelected = false
                allSurahListAdapter.updateView(StringUtils.NO_INDEX)
            }

            override fun onCompleted(mp1: MediaPlayer?) {
                binding.playLayout.play.isSelected = false
                allSurahListAdapter.updateView(StringUtils.NO_INDEX)
                if (isPlaySimple || isPlaySuffle) playNext()

                binding.playLayout.seekbar.progress = 0
                binding.playLayout.currentTime.text = "0:00"
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
            allSurahListAdapter.updateView(download)
            isDownload = true
        }

        override fun onAdded(download: Download) {
            allSurahListAdapter.updateView(download)
        }

        override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
            allSurahListAdapter.updateView(download)
        }

        override fun onCompleted(download: Download) {
            allSurahListAdapter.updateView(download)
            isDownload = true
        }

        override fun onProgress(
            download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long
        ) {
            if (isDownload) allSurahListAdapter.updateView(download)
            isDownload = false
            Log.d(TAG, "onProgress: " + downloadedBytesPerSecond / 1024)
        }

        override fun onPaused(download: Download) {
            allSurahListAdapter.updateView(download)
            isDownload = true
        }

        override fun onResumed(download: Download) {
            allSurahListAdapter.updateView(download)
        }

        override fun onCancelled(download: Download) {
            allSurahListAdapter.updateView(download)
        }

        override fun onRemoved(download: Download) {
            isDownload = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                TedPermission.create().setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        allSurahListAdapter.updateView(download)
                        if (download.file.isNotEmpty()) AppClass.deleteFile(
                            download.file, context
                        )
                    }

                    override fun onPermissionDenied(deniedPermissions: List<String>) {}
                })
                    .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                    .setPermissions(
                        Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.POST_NOTIFICATIONS
                    ).check()
            } else {
                TedPermission.create().setPermissionListener(object : PermissionListener {
                    override fun onPermissionGranted() {
                        allSurahListAdapter.updateView(download)
                        if (download.file.isNotEmpty()) AppClass.deleteFile(
                            download.file, context
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
            allSurahListAdapter.updateView(download)
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
        instance.release()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ACTIVITY_RESULT_CODE && resultCode == RESULT_OK) {
            if (data!!.hasExtra(StringUtils.OBJECT)) {
                model = data.getSerializableExtra(StringUtils.OBJECT) as SurahListModel
                favSurahs.clear()
                favSurahs = AppClass.sharedPref.getList(AppConstants.FAV_SURAHS)
                mData[currentIndex] = model
                updateUi()
            }
        }
    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // denyExplanation()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // denyExplanation()
        }
    }
}