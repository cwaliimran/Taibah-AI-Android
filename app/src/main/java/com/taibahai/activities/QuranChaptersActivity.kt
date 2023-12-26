package com.taibahai.activities

import android.content.Intent
import android.util.Log
import com.cwnextgen.amnames.utils.getJsonDataFromAsset
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelSurah
import com.network.models.ModelSurahList
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.utils.FileDownloader
import org.json.JSONException


class QuranChaptersActivity : BaseActivity() {
    lateinit var binding: ActivityQuranChaptersBinding
    lateinit var adapter: AdapterQuranChapter
    var modelSurahList = mutableListOf<ModelSurah>()
    //val fileDownloader = FileDownloader(context)



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

                val intent= Intent(context, ChapterDetailActivity::class.java)
                intent.putExtra("ayat_id",modelSurahList[position].id )
                intent.putExtra("ayat_name",modelSurahList[position].transliteration_en )
                intent.putExtra("ayat_verse",modelSurahList[position].total_verses )
                intent.putExtra("ayat_type",modelSurahList[position].revelation_type )

                startActivity(intent)

            }
        })
        binding.rvQuranChapter.adapter = adapter




        loadData()
    }

  /*  private fun initDownload() {
        val savedDownloadRequest = fileDownloader.getSavedDownloadRequest()

        if (savedDownloadRequest != null) {
            val (url, title, _) = savedDownloadRequest
            val downloadId = fileDownloader.downloadFile(url, title, "Download Description")

            // Handle the download or save the downloadId if needed
            // ...

            // Remove the saved download request after initiating the download
            fileDownloader.saveDownloadRequest("", "", "")
        }
    }*/

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

    override fun initData() {
        super.initData()


    }




}