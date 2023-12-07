package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.databinding.ActivityMyProfileBinding
import com.taibahai.databinding.ActivityPrivacyPolicyBinding
import com.taibahai.utils.showToast

class PrivacyPolicyActivity : BaseActivity() {
    lateinit var binding:ActivityPrivacyPolicyBinding
    val viewModel : MainViewModel by viewModels()
    var textPrivacy=""



    override fun onCreate() {
        binding=ActivityPrivacyPolicyBinding.inflate(layoutInflater)
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
                    textPrivacy= it.data?.data.toString()
                    if(!textPrivacy.isNullOrEmpty())
                        {
                            binding.tvPrivacyPolicy.text=textPrivacy
                        }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.privacy()
    }
}