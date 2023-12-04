package com.taibahai.activities

import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.databinding.ActivityInheritanceLawBinding

class InheritanceLawActivity : BaseActivity() {
    lateinit var binding:ActivityInheritanceLawBinding


    override fun onCreate() {
        binding=ActivityInheritanceLawBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
    binding.ivBack.setOnClickListener {
    onBackPressed()
    }

    }
}