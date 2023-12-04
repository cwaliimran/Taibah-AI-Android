package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.adapters.AdapterQuranDetail
import com.taibahai.databinding.ActivityChapterDetailBinding
import com.taibahai.models.ModelQuranChapter
import com.taibahai.models.ModelQuranDetail

class ChapterDetailActivity : BaseActivity() {
    lateinit var binding:ActivityChapterDetailBinding
    val showList=ArrayList<ModelQuranDetail>()
    lateinit var adapter: AdapterQuranDetail

    override fun onCreate() {
        binding=ActivityChapterDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterQuranDetail(showList)
        showList.add(ModelQuranDetail(1, "قُلْ هُوَ ٱللَّهُ أَحَدٌ ١","Qul huwa Allahu Ahad ","Soy, “O Prophet,”He is Allah_One"))
        showList.add(ModelQuranDetail(2, "ٱللَّهُ ٱلصَّمَدُ ","Allahu As-Samad","Allah—the Sustainer ˹needed by all˺"))
        showList.add(ModelQuranDetail(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ ٣","Allahu As-Samad","Allah—the Sustainer ˹needed by all˺"))
        adapter.setDate(showList)
        binding.rvQuranDetail.adapter=adapter

    }
}