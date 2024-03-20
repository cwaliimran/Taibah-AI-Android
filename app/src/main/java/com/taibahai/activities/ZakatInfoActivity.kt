package com.taibahai.activities

import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.databinding.ActivityZakatInfoBinding

class ZakatInfoActivity : BaseActivity() {
    val viewModel : MainViewModelAI by viewModels()

    lateinit var binding:ActivityZakatInfoBinding


    override fun onCreate() {
        binding=ActivityZakatInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Zakat Info")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }
}