package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil.setContentView
import com.network.base.BaseActivity
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.databinding.ActivityZakatInfoBinding

class ZakatInfoActivity : BaseActivity() {
    val viewModel : MainViewModel by viewModels()

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