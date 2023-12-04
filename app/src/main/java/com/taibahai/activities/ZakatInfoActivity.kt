package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.databinding.ActivityZakatInfoBinding

class ZakatInfoActivity : BaseActivity() {
    lateinit var binding:ActivityZakatInfoBinding


    override fun onCreate() {
        binding=ActivityZakatInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}