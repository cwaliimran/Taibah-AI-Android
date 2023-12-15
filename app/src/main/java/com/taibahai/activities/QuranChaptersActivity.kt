package com.taibahai.activities

import android.os.Handler
import android.os.Looper
import com.cwnextgen.amnames.utils.getJsonDataFromAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.network.utils.ProgressLoading.displayLoading
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.utils.JsonUtils
import org.json.JSONArray
import org.json.JSONException

class QuranChaptersActivity : BaseActivity() {
    lateinit var binding: ActivityQuranChaptersBinding
    lateinit var adapter: AdapterQuranChapter
    var modelSurahList = mutableListOf<ModelSurah>()
    var jsonArr: JSONArray? = null


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

    override fun initAdapter() {
        super.initAdapter()

        adapter = AdapterQuranChapter(modelSurahList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                super.onClick(position, type, data)
            }
        })
        binding.rvQuranChapter.adapter = adapter
        loadData()
    }

    private fun loadData() {
            try {
//                modelSurahList.clear()
//                jsonArr = JSONArray(JsonUtils.readRawResource(context, R.raw.allsurahlist))
//                val gson = Gson()
//                val type = object : TypeToken<List<ModelSurah?>?>() {}.type
//                modelSurahList = gson.fromJson<ArrayList<ModelSurah>>(jsonArr.toString(), type)
//                (modelSurahList as ArrayList<ModelSurah>?)?.let { adapter.setDate(it) }

                val jsonFileString = getJsonDataFromAsset("allsurahlist")
                //  Log.d(TAG, "prepareDatabase: $jsonFileString")
// Convert JSON string to a Data class
                val dataMap = gson.fromJson(jsonFileString, ModelSurah::class.java)
               // modelSurahList.addAll(dataMap.allah_names)


//                adapter.notifyDataSetChanged()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

    }

    override fun initData() {
        super.initData()


    }

}