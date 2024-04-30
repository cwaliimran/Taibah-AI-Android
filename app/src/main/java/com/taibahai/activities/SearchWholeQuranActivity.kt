package com.taibahai.activities

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.network.base.BaseActivity
import com.network.models.ModelSurah
import com.network.utils.ProgressLoading.displayLoading
import com.taibahai.R
import com.taibahai.databinding.ActivitySearchWholeQuranBinding
import com.taibahai.quran.SurahAdapter
import com.taibahai.quran.SurahListModel
import com.taibahai.quran.SurahModel
import com.taibahai.utils.AppJsonUtils
import com.taibahai.utils.SpinnerAdapterHelper
import com.taibahai.utils.hideKeyboard
import com.taibahai.utils.showToast
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class SearchWholeQuranActivity : BaseActivity() {
    lateinit var binding: ActivitySearchWholeQuranBinding
    private val TAG = "SearchWholeQuranActivit"
    val handler = Handler(Looper.getMainLooper())
    var searchRunnable: Runnable? = null
    lateinit var verseNumbersAdapter: ArrayAdapter<SurahListModel>
    private var totalVerse = 0
    private var counter = 0
    var surahNumber = "0"
    lateinit var adapter: SurahAdapter
    private var surahList: MutableList<SurahListModel> = mutableListOf()
    private var mDataFiltered: MutableList<ModelSurah> = mutableListOf()
    private var mData: MutableList<ModelSurah> = mutableListOf()
    private var surahListSelected = SurahListModel()
    private var searchText = ""
    override fun onCreate() {
        binding = ActivitySearchWholeQuranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appbar.tvTitle.text = getString(R.string.search_whole_quran)
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = SurahAdapter(this)
        binding.rvSearchQuran.adapter = adapter

    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.ivVerseNumbers.setOnClickListener {
            binding.spSurahList.performClick()
        }


        binding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchText = binding.etSearch.text.toString()
                hideKeyboard()
                if (surahNumber=="0"){
                    //search whole quran
                    showToast("search whole quran")
                }else{
//                    mData.clear()

                    adapter.notifyItemRangeRemoved(0, mData.size)
                    // Get the text from the EditText
                    showFilteredData()
                }

                return@setOnEditorActionListener true
            }
            false
        }

    }

    override fun initData() {
        super.initData()
        val jsonArr = JSONArray(AppJsonUtils.readRawResource(this, R.raw.allsurahlist))
        val gson = Gson()
        val type = object : TypeToken<List<SurahListModel?>?>() {}.type
        surahList = gson.fromJson(jsonArr.toString(), type)
        surahList.add(
            0, SurahListModel("0", "0", total_verses = "0", transliteration_en = "Whole Quran")
        )

        verseNumbersAdapter = SpinnerAdapterHelper.createAdapter(surahList, binding.spSurahList) {
            surahListSelected = surahList[it]
            counter = 0
            totalVerse = surahList[it].total_verses.toInt()
            surahNumber = surahList[it].number
            Log.d(TAG, "initData: $surahNumber")
            mData.clear()
            adapter.notifyItemRangeRemoved(0, mData.size)
            if (totalVerse > 0) {
                getAyahList()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }


    private fun getAyahList() {
        displayLoading()
        counter = 0
        try {
            lifecycleScope.launch {
                val jsonArr =
                    JSONArray(
                        AppJsonUtils.loadQuranJson(
                            this@SearchWholeQuranActivity,
                            surahNumber
                        )
                    )

                    /*{
                        "id": "2",
                        "surah_number": "1",
                        "verse_number": "2",
                        "text": "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ",
                        "transliteration_en": "Al<u>h</U>amdu lill<u>a</U>hi rabbi alAA<u>a</U>lameen<b>a</b>",
                        "translation_en": "[All] praise is [due] to Allah, Lord of the worlds -",
                        "translation_id": "Segala puji bagi Allah, Tuhan semesta alam."
                    },*/

                for (i in 0 until jsonArr.length()) {
                    val surahModel = ModelSurah()
                    if (surahNumber == jsonArr.getJSONObject(i).getString("surah_number")) {
                        surahModel.number = jsonArr.getJSONObject(i).getString("verse_number")
                        surahModel.tex = jsonArr.getJSONObject(i).getString("text")
                        surahModel.englishText =
                            jsonArr.getJSONObject(i).getString("translation_en")
                        surahModel.english_translation =
                            jsonArr.getJSONObject(i).getString("transliteration_en")
                        mData.add(surahModel)
                        counter++
                        if (counter == totalVerse) {
                            break
                        }
                    }
                }
            }
            showFilteredData()

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun showFilteredData() {

        mDataFiltered.clear()

        if (mData.isNotEmpty()) {
            mDataFiltered = mData.filter { surah ->
                surah.englishText?.lowercase()?.contains(searchText) == true ||
                        surah.english_translation?.lowercase()?.contains(searchText) == true ||
                        surah.arabicText?.lowercase()?.contains(searchText) == true ||
                        surah.id?.lowercase()?.contains(searchText) == true
            }.toMutableList()
        }

        adapter.updateList(mDataFiltered)
        displayLoading(false)
    }
}