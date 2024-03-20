package com.taibahai.activities


import AudioPlayer
import AudioPlayer.OnViewClickListener
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ScrollView
import android.widget.SeekBar
import androidx.core.content.FileProvider.getUriForFile
import com.network.base.BaseActivity
import com.network.models.ModelSurah
import com.network.models.ModelSurahDetail
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.sharedPref
import com.network.utils.StringUtils
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranDetail
import com.taibahai.databinding.ActivityChapterDetailBinding
import com.taibahai.utils.CustomScrollView
import com.taibahai.utils.JsonUtilss
import org.json.JSONArray
import org.json.JSONException
import java.io.File

class ChapterDetailActivity : BaseActivity() {
    lateinit var binding:ActivityChapterDetailBinding
    val showList=ArrayList<ModelSurahDetail>()
    lateinit var adapter: AdapterQuranDetail
    private var isFling = false
    private var totalVerse = 0
    private  var counter:Int = 0
    var surahId=""
    var mPlayerList: List<ModelSurah>? = null
    var name=""
    var objectAnimator: ObjectAnimator? = null
    private var speed = 1
    private var scroll = 0
    private val STANDARD_SPEED = 50
    private val isRepeat = false
    var currentFile = ""
    var model=ModelSurah()


    private val TAG = "ChapterDetailActivity"
    override fun onCreate() {
        binding=ActivityChapterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // currentFile= intent.getStringExtra("ayat_url").toString()
        initScroll()
        initAudioPlay()

    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.playLayout.ivPlay.setOnClickListener { v ->
            if (!currentFile.isEmpty()) {
                startPlaying(currentFile)
            }
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterQuranDetail(showList)

        binding.rvQuranDetail.adapter=adapter

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initScroll() {
//        binding.scrollView.isFocusableInTouchMode();
        binding.rvQuranDetail.setOnTouchListener { view: View?, event: MotionEvent ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // The user just touched the screen
                    if (binding.playLayout.ivPlay.isSelected()) startScroll()
                    startScroll()

                    Log.d(TAG,"initScroll: ACTION_DOWN $isFling"
                    )
                }

                MotionEvent.ACTION_UP -> {
                    // The touch just ended

                    if (!isFling) {
                        if (binding.playLayout.ivPlay.isSelected()) startScroll()
                        startScroll()

                    } else {
                        isFling = false
                    }
                }
            }
            false
        }
        binding.scrollView.setOnFlingListener(object : CustomScrollView.OnFlingListener {
            override fun onFlingStarted() {
                isFling = true
                if (objectAnimator != null) {
                    objectAnimator!!.cancel()
                }
            }

            override fun onFlingStopped() {
                isFling = false
              //  Log.d(com.taibah.fragments.al_quran.Al_Quran_Details.TAG, "onFlingStopped: ")
               // if (binding.playLayout.play.isSelected()) startScroll()
            }
        })
        binding.playLayout.sliderRange.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                AudioPlayer.getInstance()?.removeCallbacks()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val seekPosition: Long = seekBar.progress.toLong()
                AudioPlayer.getInstance()?.seek(seekPosition)
            }
        })
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            scroll = binding.scrollView.scrollY
            val view = binding.scrollView.getChildAt(0)
            val diff =
                view.bottom - (binding.scrollView.height + binding.scrollView.scrollY)
        }
    }


    private fun initAudioPlay() {
        AudioPlayer.getInstance()?.OnItemClickListener(object : OnViewClickListener {
            override fun onPlayStarted(duration: Int) {
                val total_duration: String? = AppClass.getTimeString(duration)
                binding.playLayout.maxValue.setText(total_duration)
                binding.playLayout.ivPlay.setSelected(true)
                if (objectAnimator != null) {
                    if (!objectAnimator!!.isRunning) {
                        startScroll()
                    }
                }
            }

            override fun updateDuration(duration: Int, currentPosition: Int) {
                val current_duration: String? = AppClass.getTimeString(currentPosition)
                binding.playLayout.minValue.setText(current_duration)
                binding.playLayout.sliderRange.setProgress(duration)
            }

            override fun onPause() {
                binding.playLayout.ivPlay.setSelected(false)
                stopScroll()
            }

            override fun onCompleted(mp1: MediaPlayer?) {

                binding.playLayout.ivPlay.setSelected(false)
                binding.playLayout.sliderRange.setProgress(0)
                stopScroll()
                if (isRepeat) {
                    binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
                    Handler().postDelayed({ binding.playLayout.ivPlay.callOnClick() }, 1000)
                }/* else {
                    if (isPlaySuffle) {
                        binding.playLayout.forward.callOnClick()
                    }
                }*/
            }
        })
    }


    private fun stopScroll() {
        if (objectAnimator != null) {
            objectAnimator!!.cancel()
        }
    }



    private fun showAyatList() {
        class loadAyats :
            AsyncTask<Void?, Void?, List<ModelSurahDetail>>() {
            override fun doInBackground(vararg params: Void?): List<ModelSurahDetail> {
                try {
                    val jsonArr = JSONArray(JsonUtilss.loadQuranJson(context, surahId))
                    //                    Log.d("response jsonArr: ", jsonArr.toString());
                    for (i in 0 until jsonArr.length()) {
                        val surahModel = ModelSurahDetail()
                        if (surahId == jsonArr.getJSONObject(i).getString("surah_number")) {
                            surahModel.position = jsonArr.getJSONObject(i).getString("verse_number")
                            surahModel.arabic = jsonArr.getJSONObject(i).getString("text")
                            surahModel.english_translation =
                                jsonArr.getJSONObject(i).getString("translation_en")
                            surahModel.english_transliteration =
                                jsonArr.getJSONObject(i).getString("transliteration_en")
                            showList.add(surahModel)
                            counter++
                            if (counter == totalVerse) {
                                break
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return showList
            }

            override fun onPreExecute() {
                super.onPreExecute()
                showList.clear()
                adapter.setData(showList)

            }


            override fun onPostExecute(tasks: List<ModelSurahDetail>) {
                super.onPostExecute(tasks)
                counter = 0
                adapter.setData(tasks)

                playSurah()
                Handler().postDelayed({ this@ChapterDetailActivity.startScroll() }, 1000)
            }
        }
        loadAyats().execute()
    }

    private fun playSurah() {
        val downloadId = sharedPref.getLong(StringUtils.PREV_SURAH_URL, -1)
        if (downloadId != -1L) {
            // Check download status before playing
            val status = getDownloadStatus(downloadId!!)
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                // Download completed successfully, get the content Uri
                val contentUri = getDownloadContentUri(downloadId)
                if (contentUri != null) {
                    startPlaying(contentUri.toString())
                } else {
                    Log.e("MediaPlayer", "Content Uri is null")
                }
            } else {
                Log.e("MediaPlayer", "Download not completed. Status: $status")
            }
        } else {
            Log.e("MediaPlayer", "Download ID not found in SharedPreferences")
        }
    }



    private var mediaPlayer: MediaPlayer? = null

    private fun startPlaying(audio_url: String) {
        val player = AudioPlayer.getInstance() ?: return

        val file = File(audio_url)
        val dataSource = if (file.exists()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getUriForFile(context, "com.taibahai.provider", file)
            } else {
                Uri.fromFile(file)
            }.toString()
        } else {
            audio_url
        }

        Log.d("MediaPlayer", "Setting data source: $dataSource")

        try {
            player.setData(dataSource)
            player.playOrPause()
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error during playback: ${e.message}")
            e.printStackTrace()
        }
    }


    @SuppressLint("Range")
    private fun getDownloadStatus(downloadId: Long): Int {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).query(query)
        if (cursor != null && cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            cursor.close()
            return status
        }
        return -1
    }

    @SuppressLint("Range")
    private fun getDownloadContentUri(downloadId: Long): Uri? {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).query(query)
        if (cursor != null && cursor.moveToFirst()) {
            val uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            cursor.close()
            return Uri.parse(uriString)
        }
        return null
    }






    private fun loadJson() {
        showList.clear()
        val intent = intent
        surahId = intent.getStringExtra("ayat_id")!!
        val verse = intent.getStringExtra("ayat_verse")
        totalVerse = verse!!.toInt()
        name = intent.getStringExtra("ayat_name")!!
        val type = intent.getStringExtra("ayat_type")

    }

    fun startScroll() {
        val scrollSpeed: Int = sharedPref.getInt("scroll_speed")
        if (scrollSpeed != 0) {
            speed = scrollSpeed
        }

        objectAnimator?.cancel()  // Cancel the previous animation if it exists

        objectAnimator = ObjectAnimator.ofInt(
            binding.scrollView,
            "scrollY",
            binding.scrollView.getChildAt(0).height + scroll - binding.scrollView.height
        )

        val duration: Int = binding.scrollView.getChildAt(0).height / speed
        val total_duration: Int = duration * STANDARD_SPEED

        Log.d("response", "startScroll: " + total_duration / 1000 + " sec")

        objectAnimator?.duration = total_duration.toLong()
        objectAnimator?.setInterpolator(LinearInterpolator())
        objectAnimator?.start()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Quran")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.heartt))
        loadJson()
        showAyatList()
    }
}