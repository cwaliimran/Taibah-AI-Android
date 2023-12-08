package com.taibahai.hadiths

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.adapters.AdapterHadithChapter
import com.taibahai.databinding.ActivityChapterHadiths3Binding
import com.taibahai.models.ModelChapterHadiths
import com.taibahai.models.ModelHadithChapter

class ChapterHadithsActivity3 : BaseActivity() {
    lateinit var binding:ActivityChapterHadiths3Binding
    val showList=ArrayList<ModelChapterHadiths>()
    lateinit var adapter: AdapterChapterHadiths


    override fun onCreate() {
        binding=ActivityChapterHadiths3Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        //adapter= AdapterChapterHadiths(showList)
        showList.add(ModelChapterHadiths("Hadith No: 1", "Sahih al-Bukhari","al-Bukhari","\"إنما الأعمال بالنيات.\"","\"The reward of deeds depends upon the intentions.\""))
        showList.add(ModelChapterHadiths("Hadith No: 1", "Sahih al-Bukhari","al-Bukhari","\"إنما الأعمال بالنيات.\"","\"The reward of deeds depends upon the intentions.\""))


      //  adapter.setDate(showList)
        binding.rvChapterHadiths.adapter=adapter

    }
}