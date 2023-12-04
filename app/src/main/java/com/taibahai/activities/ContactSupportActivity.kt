package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.databinding.ActivityContactSupportBinding

class ContactSupportActivity : BaseActivity() {
    lateinit var binding:ActivityContactSupportBinding


    override fun onCreate() {
        binding=ActivityContactSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}