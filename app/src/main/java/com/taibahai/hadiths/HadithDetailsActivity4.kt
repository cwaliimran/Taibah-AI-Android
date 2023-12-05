package com.taibahai.hadiths

import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.databinding.ActivityHadithDetails4Binding

class HadithDetailsActivity4 : BaseActivity() {
    lateinit var binding:ActivityHadithDetails4Binding


    override fun onCreate() {
        binding=ActivityHadithDetails4Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
       binding.ivBack.setOnClickListener {
           onBackPressed()
       }
    }
}