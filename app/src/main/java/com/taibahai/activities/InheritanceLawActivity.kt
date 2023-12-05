package com.taibahai.activities

import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.databinding.ActivityInheritanceLawBinding
import com.taibahai.utils.showToast

class InheritanceLawActivity : BaseActivity() {
    lateinit var binding:ActivityInheritanceLawBinding
    val viewModel : MainViewModel by viewModels()



    override fun onCreate() {
        binding=ActivityInheritanceLawBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
    binding.ivBack.setOnClickListener {
    onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.aboutILPrivacyTermLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }
}