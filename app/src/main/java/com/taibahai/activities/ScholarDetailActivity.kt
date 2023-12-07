package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.databinding.ActivityScholarDetailBinding
import com.taibahai.utils.showToast

class ScholarDetailActivity : BaseActivity() {
    lateinit var binding:ActivityScholarDetailBinding
    val viewModel:MainViewModel by viewModels()
    private var scholarName = ""
    private var scholarEra = ""


    override fun onCreate() {
        binding=ActivityScholarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            scholarName = intent.getStringExtra("ScholarName").toString()
            scholarEra = intent.getStringExtra("ScholarEra").toString()

            viewModel.scholars()

        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.scholarsLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    binding.tvScholarName.text=scholarName
                    binding.tvScholarEra.text=scholarEra
                    binding.tvScholarDetail.text=it.data?.data?.firstOrNull()?.description
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }


}