package com.taibahai.quran

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.network.utils.AppClass
import com.taibahai.R
import com.taibahai.activities.ShareActivity
import com.taibahai.audioPlayer.AudioPlayer.Companion.instance
import com.taibahai.audioPlayer.AudioPlayer.OnViewClickListener
import com.taibahai.databinding.ActivityAlQuranDetailsBinding
import com.taibahai.notifications.MediaNotificationManager
import com.taibahai.quran.StringUtils.getNameFromUrl
import com.taibahai.utils.Constants
import com.taibahai.utils.CustomScrollView
import com.tonyodev.fetch2.Status
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.nio.charset.StandardCharsets

class Al_Quran_Details : AppCompatActivity() {
    private var mData: ArrayList<SurahModel>? = null
    private var surahAdapter: SurahAdapter? = null
    var binding: ActivityAlQuranDetailsBinding? = null
    var name: String? = null
    var objectAnimator: ObjectAnimator? = null
    private var totalVerse = 0
    private var counter = 0
    private var scroll = 0
    private var speed = 3
    private var isFling = false
    private val isPlaySuffle = false
    private val isRepeat = false
    private val isUpdate = false
    var currentIndex = 0
    var currentFile = ""
    var surahName = ""
    var surahId: String? = ""
    var model: SurahListModel? = null
    var mPlayerList: List<SurahListModel>? = null

    //    RoomDatabaseRepository dbRepository;
    var context: Context? = null
    var activity: Activity? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlQuranDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this
        activity = this
        //        initDB();
        initClick()
        initAdapter()
        loadJson()
        initScroll()
        initAudioPlay()
    }

    //    private void initDB() {
    //        dbRepository = new RoomDatabaseRepository(context);
    //    }
    @SuppressLint("ClickableViewAccessibility")
    private fun initScroll() {
//        binding.scrollView.isFocusableInTouchMode();
        binding!!.recyclerView.setOnTouchListener { view: View?, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->                     // The user just touched the screen
                    if (binding!!.playLayout.play.isSelected) startScroll()

                MotionEvent.ACTION_UP ->                     // The touch just ended
                    if (!isFling) {
                        if (binding!!.playLayout.play.isSelected) startScroll()
                    } else {
                        isFling = false
                    }
            }
            false
        }
        binding!!.scrollView.setOnFlingListener(object : CustomScrollView.OnFlingListener {
            override fun onFlingStarted() {
                isFling = true
                if (objectAnimator != null) {
                    objectAnimator!!.cancel()
                }
            }

            override fun onFlingStopped() {
                isFling = false
                if (binding!!.playLayout.play.isSelected) startScroll()
            }
        })
        binding!!.playLayout.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                instance.removeCallbacks()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                instance.seek(seekBar.progress.toLong())
            }
        })
        binding!!.scrollView.viewTreeObserver.addOnScrollChangedListener {
            scroll = binding!!.scrollView.scrollY
            val view = binding!!.scrollView.getChildAt(0)
            val diff = view.bottom - (binding!!.scrollView.height + binding!!.scrollView.scrollY)
        }
    }

    private fun initClick() {
        binding!!.back.setOnClickListener { v: View? -> onBackPressed() }

//        binding.settings.setOnClickListener(v -> startActivityForResult(new Intent(context, ReaderSettingsActivity.class)
//                , Constants.ACTIVITY_RESULT_CODE));
        binding!!.playLayout.play.setOnClickListener { v: View? ->
            if (!currentFile.isEmpty()) {
                startPlaying(currentFile)
            }
        }


//        binding.playLayout.star.setOnClickListener(v -> {
//            isUpdate = true;
//            if (!model.isFav()) {
//                dbRepository.addToFavSurah(new FavModel(Long.parseLong(model.getId())));
//                v.setSelected(true);
//                model.setFav(true);
//                Log.d("response", "park id inserted: " + model.getId());
//            } else {
//                dbRepository.deleteFromFavSurah(Integer.parseInt(model.getId()));
//                v.setSelected(false);
//                model.setFav(false);
//                Log.d("response", "park id deleted: " + model.getId());
//            }
//
//        });
    }

    private fun initAdapter() {
        mPlayerList = ArrayList()
        mData = ArrayList()
        surahAdapter = SurahAdapter(context!!)
        binding!!.recyclerView.adapter = surahAdapter
        binding!!.recyclerView.isNestedScrollingEnabled = false
        surahAdapter!!.setOnItemClickListner(object : SurahAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, homeModel: SurahModel?) {
                val intent1 = Intent(context, ShareActivity::class.java)
                intent1.putExtra("surah_name", name)
                intent1.putExtra("share_type", "quran_ayat")
                intent1.putExtra("arabic_text", homeModel!!.arabicText)
                intent1.putExtra("english_text", homeModel.englishText)
                intent1.putExtra("ayat_number", homeModel.position)
                intent1.putExtra("surah_number", surahId)
                startActivity(intent1)
            }

        })
    }

    private fun loadJson() {
        mData!!.clear()
        val intent = intent
        surahId = intent.getStringExtra("ayat_id")
        val verse = intent.getStringExtra("ayat_verse")
        totalVerse = verse!!.toInt()
        name = intent.getStringExtra("ayat_name")
        val type = intent.getStringExtra("ayat_type")
        val args = intent.getBundleExtra(StringUtils.BUNDLE)!!
        mPlayerList = args.getSerializable(StringUtils.ARRAY) as List<SurahListModel>?
        updateCurrentIndex()
        if (mPlayerList!!.size == 1) {
//            binding.playLayout.forward.setAlpha(.4f);
//            binding.playLayout.backward.setAlpha(.4f);
//            binding.playLayout.shuffle.setAlpha(.4f);
//            binding.playLayout.forward.setEnabled(false);
//            binding.playLayout.backward.setEnabled(false);
//            binding.playLayout.shuffle.setEnabled(false);
        }
        binding!!.detailsAyatName.text = name
        binding!!.detailsVerseNumber.text = verse
        try {
            if (type == StringUtils.MAKKI) {
                binding!!.surahType.text = getString(R.string.makki)
            }
            if (type == StringUtils.MADNI) {
                binding!!.surahType.text = getString(R.string.madni)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        updateUi()
    }


    private fun showAyatList() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.IO) {
                    val jsonArr = JSONArray(loadQuranJson(context, surahId))
                    for (i in 0 until jsonArr.length()) {
                        val surahModel = SurahModel()
                        if (surahId == jsonArr.getJSONObject(i).getString("surah_number")) {
                            surahModel.position = jsonArr.getJSONObject(i).getString("verse_number")
                            surahModel.arabicText = jsonArr.getJSONObject(i).getString("text")
                            surahModel.englishText =
                                jsonArr.getJSONObject(i).getString("translation_en")
                            surahModel.english_translation =
                                jsonArr.getJSONObject(i).getString("transliteration_en")
                            mData!!.add(surahModel)
                            counter++
                            if (counter == totalVerse) {
                                break
                            }
                        }
                    }
                }
                surahAdapter!!.updateList(mData)
                binding!!.progressBar.visibility = View.GONE
                binding!!.playView.visibility = View.VISIBLE
                playSurah()
                delay(1000)
                startScroll()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }


    private fun startPlaying(audio_url: String) {
        instance.setData(audio_url)
        instance.playOrPause()
        Handler(Looper.getMainLooper()).postDelayed({
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
                Log.d(TAG, "onPlayStarted: ")
                val total_duration = getTimeString(duration.toLong())
                binding!!.playLayout.totalTime.text = total_duration
                binding!!.playLayout.play.isSelected = true
                if (objectAnimator != null) {
                    if (!objectAnimator!!.isRunning) {
                        startScroll()
                    }
                }
            }

            override fun updateDuration(duration: Int, currentPosition: Int) {
                val current_duration = getTimeString(currentPosition.toLong())
                binding!!.playLayout.currentTime.text = current_duration
                binding!!.playLayout.seekbar.progress = duration
            }

            override fun onPause() {
                binding!!.playLayout.play.isSelected = false
                stopScroll()
            }

            override fun onCompleted(mp1: MediaPlayer?) {
                Log.d(TAG, "onCompleted: isRepeat $isRepeat isShuffle $isPlaySuffle")
                binding!!.playLayout.play.isSelected = false
                binding!!.playLayout.seekbar.progress = 0
                binding!!.playLayout.currentTime.text = "0:00"
                stopScroll()
                if (isRepeat) {
                    binding!!.scrollView.fullScroll(ScrollView.FOCUS_UP)
                    Handler().postDelayed({ binding!!.playLayout.play.callOnClick() }, 1000)
                } else {
                    if (isPlaySuffle) {
//                        binding.playLayout.forward.callOnClick();
                    }
                }
            }
        })
    }

    private fun stopScroll() {
        if (objectAnimator != null) {
            objectAnimator!!.cancel()
        }
    }

    private fun playNext() {
        currentIndex++
        if (currentIndex > mPlayerList!!.size - 1) {
            currentIndex = 0
        }
        updateUi()
    }

    private fun playBack() {
        currentIndex--
        if (currentIndex < 0) {
            currentIndex = mPlayerList!!.size - 1
        }
        updateUi()
    }

    private fun updateUi() {
        stopScroll()
        binding!!.scrollView.fullScroll(ScrollView.FOCUS_UP)
        model = mPlayerList!![currentIndex]
        if (isPlaySuffle) {
            if (model!!.id == surahId) {
                playNext()
                return
            }
        }
        if (model!!.download != null) {
            if (model!!.download!!.status == Status.COMPLETED) {
                val child = StringUtils.SURAH_FOLDER + getNameFromUrl(
                    model!!.audio
                )
                if (isFileExists(child, context)) {
                    surahId = model!!.id
                    totalVerse = model!!.totalVerses?.toInt()!!
                    showAyatList()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun playSurah() {
        currentFile = model!!.download!!.file
        surahName = model!!.transliterationEn.toString()
        binding!!.playLayout.surahName.text = surahName
        binding!!.detailsAyatName.text = surahName
        binding!!.detailsVerseNumber.text = totalVerse.toString() + ""
        binding!!.playLayout.star.isSelected = model!!.isFav
        try {
            if (model!!.revelationType == StringUtils.MAKKI) {
                binding!!.surahType.text = getString(R.string.makki)
            }
            if (model!!.revelationType == StringUtils.MADNI) {
                binding!!.surahType.text = getString(R.string.madni)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        startPlaying(currentFile)
    }

    fun updateCurrentIndex() {
        for (i in mPlayerList!!.indices) {
            val model = mPlayerList!![i]
            if (!surahId!!.isEmpty()) {
                if (surahId == model.id) {
                    currentIndex = i
                    break
                }
            }
        }
    }

    fun startScroll() {
        val scrollSpeed = AppClass.sharedPref.getInt(StringUtils.SCROLL_SPEED)
        if (scrollSpeed != 0) {
            speed = scrollSpeed
        }
        objectAnimator = ObjectAnimator.ofInt(
            binding!!.scrollView,
            "scrollY",
            binding!!.scrollView.getChildAt(0).height + scroll - binding!!.scrollView.height
        )
        val duration = binding!!.scrollView.getChildAt(0).height / speed
        // int total_duration=duration * Constants.getHeight(ScriptActivity.this);
        val total_duration = duration * STANDARD_SPEED
        //        Log.d("response", "helf height: "+Constants.getHeight(ScriptActivity.this));
        objectAnimator?.setDuration(total_duration.toLong())
        objectAnimator?.setAutoCancel(true)
        objectAnimator?.setInterpolator(LinearInterpolator())
        objectAnimator?.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ACTIVITY_RESULT_CODE && resultCode == RESULT_OK) {
            surahAdapter!!.notifyDataSetChanged()
            if (binding!!.playLayout.play.isSelected) startScroll()
        }
    }

    override fun onBackPressed() {
        if (isUpdate) finishWithUpdate(activity, model) else super.onBackPressed()
    }

    protected fun finishWithUpdate(activity: Activity?, `val`: Any?) {
        val returnIntent = Intent()
        returnIntent.putExtra(StringUtils.OBJECT, `val` as Serializable?)
        setResult(RESULT_OK, returnIntent)
        activity!!.finish()
    }

    companion object {
        private const val STANDARD_SPEED = 50
        private const val TAG = "Al_Quran_Details"
        fun loadQuranJson(context: Context?, id: String?): String? {
            var json = ""
            json = try {
                val `is` = context!!.resources.openRawResource(getFileName(id))
                val size = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, StandardCharsets.UTF_8)
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }
            return json
        }

        private fun getFileName(id: String?): Int {
            val surah_id = id!!.toInt()
            return if (surah_id >= 1 && surah_id <= 2) R.raw.quran_part_0 else if (surah_id >= 3 && surah_id <= 4) R.raw.quran_part_1 else if (surah_id >= 5 && surah_id <= 8) R.raw.quran_part_2 else if (surah_id >= 9 && surah_id <= 16) R.raw.quran_part_3 else if (surah_id >= 17 && surah_id <= 24) R.raw.quran_part_4 else if (surah_id >= 25 && surah_id <= 32) R.raw.quran_part_5 else if (surah_id >= 33 && surah_id <= 40) R.raw.quran_part_6 else if (surah_id >= 41 && surah_id <= 52) R.raw.quran_part_7 else if (surah_id >= 53 && surah_id <= 64) R.raw.quran_part_8 else if (surah_id >= 65 && surah_id <= 80) R.raw.quran_part_9 else if (surah_id >= 81 && surah_id <= 114) R.raw.quran_part_10 else -1
        }

        fun getTimeString(duration: Long): String {
            val minutes = Math.floor((duration / 1000 / 60).toDouble()).toInt()
            val seconds = (duration / 1000 - minutes * 60).toInt()
            return minutes.toString() + ":" + String.format("%02d", seconds)
        }

        fun isFileExists(childPath: String?, context: Context?): Boolean {
            val yourFile = File(getAudioOutputDirectory(context), childPath)
            return yourFile.exists()
        }

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
    }
}