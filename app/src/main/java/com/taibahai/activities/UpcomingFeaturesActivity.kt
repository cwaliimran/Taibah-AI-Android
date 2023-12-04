package com.taibahai.activities


import com.network.base.BaseActivity
import com.taibahai.databinding.ActivityUpcomingFeaturesBinding

class UpcomingFeaturesActivity : BaseActivity() {
    lateinit var binding:ActivityUpcomingFeaturesBinding


    override fun onCreate() {
        binding=ActivityUpcomingFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
       binding.ivBack.setOnClickListener {
           onBackPressed()
       }
    }
}