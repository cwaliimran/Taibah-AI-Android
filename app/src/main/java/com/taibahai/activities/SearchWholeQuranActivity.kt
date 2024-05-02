@file:Suppress("UNNECESSARY_SAFE_CALL")

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
import com.network.models.ModelChapter
import com.network.utils.ProgressLoading.displayLoading
import com.taibahai.R
import com.taibahai.databinding.ActivitySearchWholeQuranBinding
import com.taibahai.quran.ChaptersAdapter
import com.taibahai.quran.SearchResultListener
import com.taibahai.quran.SurahListModel
import com.taibahai.utils.AppJsonUtils
import com.taibahai.utils.AppJsonUtils.containsIgnoreCase
import com.taibahai.utils.AppJsonUtils.removeHtmlTags
import com.taibahai.utils.AppJsonUtils.searchWholeQuran
import com.taibahai.utils.SpinnerAdapterHelper
import com.taibahai.utils.hideKeyboard
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

class SearchWholeQuranActivity : BaseActivity() {
    lateinit var binding: ActivitySearchWholeQuranBinding
    private val TAG = "SearchWholeQuranActivit"
    val handler = Handler(Looper.getMainLooper())
    var searchRunnable: Runnable? = null
    lateinit var verseNumbersAdapter: ArrayAdapter<SurahListModel>
    var surahNumber = "0"
    var moreRecordsUpdate = 0
    lateinit var adapter: ChaptersAdapter
    private var surahList: MutableList<SurahListModel> = mutableListOf()
    private var mDataFiltered: MutableList<ModelChapter> = mutableListOf()
    private var mData: MutableList<ModelChapter> = mutableListOf()
    private var surahListSelected = SurahListModel()
    private var searchText = ""

    override fun onCreate() {
        binding = ActivitySearchWholeQuranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appbar.tvTitle.text = getString(R.string.search_whole_quran)
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = ChaptersAdapter(mData, this@SearchWholeQuranActivity, false)
        binding.rvSearchQuran.adapter = adapter
    }

    override fun clicks() {
        binding.tvMoreRecords.setOnClickListener {
            hideGone(binding.tvMoreRecords)
        }
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.ivVerseNumbers.setOnClickListener {
            binding.spSurahList.performClick()
        }


        binding.etSearch.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchText = binding.etSearch.text.toString().trim()
                binding.progressLoading.setProgress(0, true)
                if (searchText.isNotEmpty()) {
                    hideKeyboard()
                    if (surahNumber == "0") {
                        displayLoading(cancelable = true)
                        //search whole quran
                        mData.clear()
                        adapter.clearList()
                        moreRecordsUpdate = 0
                        hideGone(binding.tvMoreRecords)

                        searchWholeQuran(
                            searchText,
                            this@SearchWholeQuranActivity,
                            object : SearchResultListener {
                                override fun onSearchResultFound(result: ModelChapter) {
                                    // Update the UI on the main thread with a delay
                                    runOnUiThread {
                                        mData.add(result)
                                        hideGone(binding.noData.root)
                                        adapter.updateList(mData)
                                        displayLoading(false)
                                        if (moreRecordsUpdate == 1) {
                                            show(binding.tvMoreRecords)
                                            binding.tvMoreRecords.text =
                                                "More Records Loaded - Total: ${mData.size}"
                                        }
                                        moreRecordsUpdate = 1
                                    }
                                }

                                override fun onProgressUpdate(surahId: Int) {
                                    super.onProgressUpdate(surahId)
                                    binding.progressLoading.setProgress(surahId, true)
                                }

                                override fun onSearchComplete() {
                                    runOnUiThread {
                                        Log.d(TAG, "onSearchComplete:   ")
                                        displayLoading(false)
                                        if (mData.isEmpty()) {
                                            show(binding.noData.root)
                                            hideGone(binding.tvMoreRecords)
                                        } else {
                                            hideGone(binding.noData.root)
                                            show(binding.tvMoreRecords)
                                            binding.tvMoreRecords.text =
                                                "All records Loaded - Total:${mData.size}"

                                        }
                                    }
                                }
                            },
                            isCancelJob = false
                        )

                    } else {
                        //load ayah and search
                        mData.clear()
                        adapter.clearList()
                        getAyahList()
                    }
                } else {
                    //
                    binding.etSearch.error = "Enter text to search"
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

        verseNumbersAdapter =
            SpinnerAdapterHelper.createAdapter(surahList, binding.spSurahList) { it ->
                //cancel whole quran search job
                searchWholeQuran("", this, null, true)
                surahListSelected = surahList[it]
                surahNumber = surahList[it].number
                binding.etSearch.hint = "Search in " + surahList[it].transliteration_en
                Log.d(TAG, "initData: $surahNumber")
                hideGone(binding.tvMoreRecords)
                mData.clear()
                mDataFiltered.clear()
                adapter.clearList()

                Handler(Looper.getMainLooper()).postDelayed({
                    binding.progressLoading.setProgress(0, true)
                }, 300)

            }

    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }


    private fun getAyahList() {
        displayLoading(cancelable = true)
        try {
            lifecycleScope.launch {
                val jsonArr = JSONArray(
                    AppJsonUtils.loadQuranJson(
                        this@SearchWholeQuranActivity, surahNumber
                    )
                )

                for (i in 0 until jsonArr.length()) {
                    val surahModel = ModelChapter()
                    if (surahNumber == jsonArr.getJSONObject(i).getString("surah_number")) {
                        surahModel.surah_number = jsonArr.getJSONObject(i).getString("surah_number")
                        surahModel.verse_number = jsonArr.getJSONObject(i).getString("verse_number")
                        surahModel.text = jsonArr.getJSONObject(i).getString("text")
                        surahModel.translation_en =
                            jsonArr.getJSONObject(i).getString("translation_en")
                        surahModel.transliteration_en =
                            jsonArr.getJSONObject(i).getString("transliteration_en")
                        mData.add(surahModel)
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
                val transliterationEnWithoutHtmlTags = removeHtmlTags(surah.transliteration_en)

                containsIgnoreCase(
                    surah.surah_number,
                    searchText
                ) || containsIgnoreCase(
                    surah.verse_number,
                    searchText
                ) || containsIgnoreCase(
                    surah.text,
                    searchText
                ) || containsIgnoreCase(
                    surah.translation_en,
                    searchText
                ) || containsIgnoreCase(surah.transliteration_en, searchText) || containsIgnoreCase(
                    transliterationEnWithoutHtmlTags,
                    searchText
                ) || containsIgnoreCase(surah.id, searchText)
            }.toMutableList()
        }
        if (mDataFiltered.isEmpty()) {
            show(binding.noData.root)
        } else {
            hideGone(binding.noData.root)
        }
        adapter.updateList(mDataFiltered)
        displayLoading(false)

        show(binding.tvMoreRecords)
        binding.tvMoreRecords.text = "All records Loaded - Total:${mDataFiltered.size}"

        binding.progressLoading.setProgress(113, true)
    }

}