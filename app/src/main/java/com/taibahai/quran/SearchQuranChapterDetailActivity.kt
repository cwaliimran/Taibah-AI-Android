package com.taibahai.quran

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.network.models.ModelChapter
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.databinding.ActivitySearchQuranChapterDetailsBinding
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.nio.charset.StandardCharsets

class SearchQuranChapterDetailActivity : AppCompatActivity() {
    private var mData: MutableList<ModelChapter> = mutableListOf()
    private var verseNumbers = mutableListOf<String>()
    private var chaptersAdapter: ChaptersAdapter? = null
    lateinit var binding: ActivitySearchQuranChapterDetailsBinding
    var name: String? = null
    private var totalVerse = 0
    private var counter = 0
    var surahId: String? = ""
    var model: SurahListModel = SurahListModel()
    var context: Context? = null
    var activity: Activity? = null
    private var favSurahs = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchQuranChapterDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this
        activity = this
        favSurahs = AppClass.sharedPref.getList(AppConstants.FAV_SURAHS)
        initClick()
        initAdapter()
        loadJson()
    }


    private fun initClick() {
        binding.ivVerseNumbers.setOnClickListener {
            binding.spVerseNumbers.performClick()
        }

        binding.appbar.ivLeft.setOnClickListener { v: View? -> onBackPressed() }
    }

    private fun initAdapter() {
        mData = ArrayList()
        chaptersAdapter = ChaptersAdapter(mData, context!!, true, hideSurahName = true)
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
        showAyatList()
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
                val jsonArr = JSONArray(loadQuranJson(context, surahId))
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
            //from search
            mData.forEach {
                verseNumbers.add(it.verse_number)
            }
            var verseNumbersAdapter: ArrayAdapter<String>? = null
            verseNumbersAdapter = ArrayAdapter(
                this@SearchQuranChapterDetailActivity,
                android.R.layout.simple_list_item_1,
                verseNumbers
            )
            binding.spVerseNumbers.adapter = verseNumbersAdapter
            binding.spVerseNumbers.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, view: View, i: Int, l: Long
                    ) {
                        //go to verse
                        binding.recyclerView.scrollToPosition(i)
                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
                }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    companion object {
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
            val surah_id = id?.toIntOrNull() ?: return -1

            val rangeToRawMap = mapOf(
                1..2 to R.raw.quran_part_0,
                3..4 to R.raw.quran_part_1,
                5..8 to R.raw.quran_part_2,
                9..16 to R.raw.quran_part_3,
                17..24 to R.raw.quran_part_4,
                25..32 to R.raw.quran_part_5,
                33..40 to R.raw.quran_part_6,
                41..52 to R.raw.quran_part_7,
                53..64 to R.raw.quran_part_8,
                65..80 to R.raw.quran_part_9,
                81..114 to R.raw.quran_part_10
            )

            return rangeToRawMap.entries.firstOrNull { surah_id in it.key }?.value ?: -1
        }


    }
}