package com.taibahai.quran

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.lifecycle.lifecycleScope
import com.network.models.ModelChapter
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.getTimeString
import com.network.utils.AppClass.Companion.isFileExists
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.audioPlayer.AudioPlayer.Companion.instance
import com.taibahai.audioPlayer.AudioPlayer.OnViewClickListener
import com.taibahai.databinding.ActivityChapterDetailsBinding
import com.taibahai.notifications.MediaNotificationManager
import com.taibahai.quran.StringUtils.getNameFromUrl
import com.taibahai.utils.AppJsonUtils.loadQuranJson
import com.taibahai.utils.Constants
import com.taibahai.utils.CustomScrollView
import com.tonyodev.fetch2.Status
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import java.io.Serializable

class ChapterDetailActivity : AppCompatActivity() {
    private var mData: MutableList<ModelChapter> = mutableListOf()
    private var verseNumbers = mutableListOf<String>()
    private var chaptersAdapter: ChaptersAdapter? = null
    lateinit var binding: ActivityChapterDetailsBinding
    var name: String? = null
    var objectAnimator: ObjectAnimator? = null
    private var totalVerse = 0
    private var counter = 0
    private var scroll = 0
    private var speed = 3
    private var isFling = false
    var currentIndex = 0
    var currentFile = ""
    var surahName = ""
    var surahId: String? = ""
    var model: SurahListModel = SurahListModel()
    var mPlayerList: List<SurahListModel>? = null
    var isFromSearch = false
    var context: Context? = null
    var activity: Activity? = null
    private var favSurahs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChapterDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        favSurahs = AppClass.sharedPref.getList(AppConstants.FAV_SURAHS)
        initClick()
        initAdapter()
        loadJson()
        initScroll()
        initAudioPlay()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initScroll() {
//        binding.scrollView.isFocusableInTouchMode();
        binding.recyclerView.setOnTouchListener { view: View?, event: MotionEvent ->
            when (event.action) {
                MotionEvent.ACTION_DOWN ->                     // The user just touched the screen
                    if (binding.playLayout.play.isSelected) startScroll()

                MotionEvent.ACTION_UP ->                     // The touch just ended
                    if (!isFling) {
                        if (binding.playLayout.play.isSelected) startScroll()
                    } else {
                        isFling = false
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
                if (binding.playLayout.play.isSelected) startScroll()
            }
        })
        binding.playLayout.seekbar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                instance.removeCallbacks()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                instance.seek(seekBar.progress.toLong())
            }
        })
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            scroll = binding.scrollView.scrollY
            val view = binding.scrollView.getChildAt(0)
            val diff = view.bottom - (binding.scrollView.height + binding.scrollView.scrollY)
        }
    }

    private fun initClick() {
        binding.appbar.ivLeft.setOnClickListener { v: View? -> onBackPressed() }
//        binding.appbar.ivRight.visibility = View.VISIBLE
//        binding.appbar.ivRight.setImageResource(R.drawable.baseline_text_fields_24)
//        binding.appbar.ivRight.setOnClickListener {
//            startActivity(Intent(this, ReaderSettingsActivity::class.java))
//        }

//        binding.settings.setOnClickListener(v -> startActivityForResult(new Intent(context, ReaderSettingsActivity.class)
//                , Constants.ACTIVITY_RESULT_CODE));
        binding.playLayout.play.setOnClickListener { v: View? ->
            if (!currentFile.isEmpty()) {
                startPlaying(currentFile)
            }
        }

        binding.playLayout.ivFav.setOnClickListener {
            if (model.isFav) {
                model.isFav = false
                favSurahs.remove(model.number)
                AppClass.sharedPref.storeList(AppConstants.FAV_SURAHS, favSurahs)
            } else {
                favSurahs.add(model.number)
                AppClass.sharedPref.storeList(AppConstants.FAV_SURAHS, favSurahs)
                model.isFav = true
            }
            binding.playLayout.ivFav.isSelected = model.isFav
        }
    }

    private fun initAdapter() {
        mPlayerList = ArrayList()
        mData = ArrayList()
        chaptersAdapter = ChaptersAdapter(mData, context!!, true)
        binding.recyclerView.adapter = chaptersAdapter
        binding.recyclerView.isNestedScrollingEnabled = false
    }

    private fun loadJson() {
        mData.clear()
        val intent = intent
        surahId = intent.getStringExtra("ayat_id")
        val verse = intent.getStringExtra("ayat_verse")
        totalVerse = verse!!.toInt()
        name = intent.getStringExtra("ayat_name")
        val type = intent.getStringExtra("ayat_type")
        val args = intent.getBundleExtra(StringUtils.BUNDLE)
        mPlayerList = args?.getSerializable(StringUtils.ARRAY) as List<SurahListModel>?
        updateCurrentIndex()
        updateUi()
//        if (mPlayerList!!.size == 1) {
//            binding.playLayout.forward.setAlpha(.4f);
//            binding.playLayout.backward.setAlpha(.4f);
//            binding.playLayout.shuffle.setAlpha(.4f);
//            binding.playLayout.forward.setEnabled(false);
//            binding.playLayout.backward.setEnabled(false);
//            binding.playLayout.shuffle.setEnabled(false);
//        }
        binding.appbar.tvTitle.text = name
        binding.makkiMadni.detailsVerseNumber.text = "$verse Ayaat"
        try {
            if (type == StringUtils.MAKKI) {
                binding.makkiMadni.surahType.text = getString(R.string.makki)
                binding.makkiMadni.imageView2.setImageResource(R.drawable.makkah)
            }
            if (type == StringUtils.MADNI) {
                binding.makkiMadni.surahType.text = getString(R.string.madni)
                binding.makkiMadni.imageView2.setImageResource(R.drawable.madina)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun showAyatList() {
            try {
                lifecycleScope.launch {
                    val jsonArr = JSONArray(loadQuranJson(context!!, surahId.toString()))
                    for (i in 0 until jsonArr.length()) {
                        val surahModel = ModelChapter()
                        if (surahId == jsonArr.getJSONObject(i).getString("surah_number")) {
                            surahModel.verse_number = jsonArr.getJSONObject(i).getString("verse_number")
                            surahModel.text = jsonArr.getJSONObject(i).getString("text")
                            surahModel.translation_en =
                                jsonArr.getJSONObject(i).getString("translation_en")
                            surahModel.transliteration_en =
                                jsonArr.getJSONObject(i).getString("transliteration_en")
                            mData.add(surahModel)
                            counter++
                            if (counter == totalVerse) {
                                break
                            }
                        }
                    }
                }
                chaptersAdapter!!.updateList(mData)
                binding.progressBar.visibility = View.GONE
                playSurah()
                binding.playView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    startScroll()
                },1000)

            } catch (e: JSONException) {
                e.printStackTrace()
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
                binding.playLayout.totalTime.text = total_duration
                binding.playLayout.play.isSelected = true
                if (objectAnimator != null) {
                    if (!objectAnimator!!.isRunning) {
                        startScroll()
                    }
                }
            }

            override fun updateDuration(duration: Int, currentPosition: Int, totalTrackTime: Int) {
                val current_duration = getTimeString(currentPosition.toLong())
                binding.playLayout.currentTime.text = current_duration
                binding.playLayout.seekbar.progress = duration
            }

            override fun onPause() {
                binding.playLayout.play.isSelected = false
                stopScroll()
            }

            override fun onCompleted(mp1: MediaPlayer?) {
                binding.playLayout.play.isSelected = false
                binding.playLayout.seekbar.progress = 0
                binding.playLayout.currentTime.text = "0:00"
                stopScroll()
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
        binding.scrollView.fullScroll(ScrollView.FOCUS_UP)
        model = mPlayerList!![currentIndex]
        if (model.download != null) {
            if (model.download!!.status == Status.COMPLETED) {
                val child = StringUtils.SURAH_FOLDER + getNameFromUrl(
                    model.audio
                )
                if (isFileExists(child)) {
                    surahId = model.id
                    totalVerse = model.total_verses.toInt()
                    showAyatList()
                }
            }
        }
    }

    private fun playSurah() {
        currentFile = model.download!!.file
        surahName = model.transliteration_en.toString()
        binding.playLayout.surahName.text = surahName
        binding.playLayout.ivFav.isSelected = model.isFav
        binding.playLayout.tvMeaning.text =
            "${model.translation_en}(${model.total_verses})"

        binding.appbar.tvTitle.text = surahName
        binding.makkiMadni.detailsVerseNumber.text = "$totalVerse Ayaat"
        binding.playLayout.tvSurahNo.text = model.number
        binding.playLayout.ivFav.isSelected = model.isFav

        try {
            if (model.revelation_type == StringUtils.MAKKI) {
                binding.makkiMadni.surahType.text = getString(R.string.makki)
            }
            if (model.revelation_type == StringUtils.MADNI) {
                binding.makkiMadni.surahType.text = getString(R.string.madni)
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
            binding.scrollView,
            "scrollY",
            binding.scrollView.getChildAt(0).height + scroll - binding.scrollView.height
        )
        val duration = binding.scrollView.getChildAt(0).height / speed
        // int total_duration=duration * Constants.getHeight(ScriptActivity.this);
        val total_duration = duration * STANDARD_SPEED
        //        Log.d("response", "helf height: "+Constants.getHeight(ScriptActivity.this));
        objectAnimator?.setDuration(total_duration.toLong())
        objectAnimator?.setAutoCancel(true)
        objectAnimator?.interpolator = LinearInterpolator()
        objectAnimator?.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ACTIVITY_RESULT_CODE && resultCode == RESULT_OK) {
            chaptersAdapter!!.notifyDataSetChanged()
            if (binding.playLayout.play.isSelected) startScroll()
        }
    }

    override fun onBackPressed() {
        finishWithUpdate(activity, model)
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

    }
}