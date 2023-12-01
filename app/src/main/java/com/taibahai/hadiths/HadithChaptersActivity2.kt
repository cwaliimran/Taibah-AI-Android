package com.taibahai.hadiths

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterHadithBooks
import com.taibahai.adapters.AdapterHadithChapter
import com.taibahai.databinding.ActivityHadithChapters2Binding
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelHadithChapter

class HadithChaptersActivity2 : BaseActivity() {
    lateinit var binding:ActivityHadithChapters2Binding
    val showList=ArrayList<ModelHadithChapter>()
    lateinit var adapter: AdapterHadithChapter



    override fun onCreate() {
        binding=ActivityHadithChapters2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {

    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterHadithChapter(showList)
        showList.add(ModelHadithChapter("Revelation", "From 1 to 7","Arabic"))
        showList.add(ModelHadithChapter("Belief", "From 8 to 58","Arabic"))
        showList.add(ModelHadithChapter("Knowledge", "From 59 to 134","Arabic"))
        showList.add(ModelHadithChapter("Ablutions (Wudu)", "From 135 to 247","Arabic"))


        adapter.setDate(showList)
        binding.rvHadithChapter.adapter=adapter

    }
}