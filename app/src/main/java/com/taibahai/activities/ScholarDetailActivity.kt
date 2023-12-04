package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.databinding.ActivityScholarDetailBinding

class ScholarDetailActivity : BaseActivity() {
    lateinit var binding:ActivityScholarDetailBinding


    override fun onCreate() {
        binding=ActivityScholarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}