package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.ActivityQuranChaptersBinding
import com.taibahai.models.ModelQuranChapter

class QuranChaptersActivity : BaseActivity() {
    lateinit var binding:ActivityQuranChaptersBinding
    val showList=ArrayList<ModelQuranChapter>()
    lateinit var adapter:AdapterQuranChapter


    override fun onCreate() {
        binding=ActivityQuranChaptersBinding.inflate(layoutInflater)
        setContentView(binding.root)    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterQuranChapter(showList)
        showList.add(ModelQuranChapter(1, "Al-Faatiha","The opening(7) "))
        showList.add(ModelQuranChapter(2, "Al-Faatiha","The opening(7) "))
        showList.add(ModelQuranChapter(3, "Al-Faatiha","The opening(7) "))
        showList.add(ModelQuranChapter(4, "Al-Faatiha","The opening(7) "))
        adapter.setDate(showList)
        binding.rvQuranChapter.adapter=adapter

    }

}