package com.taibahai.activities


import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.databinding.ActivityUpcomingFeaturesBinding
import com.taibahai.utils.showToast

class UpcomingFeaturesActivity : BaseActivity() {
    lateinit var binding:ActivityUpcomingFeaturesBinding
    val viewModel : MainViewModel by viewModels()



    override fun onCreate() {
        binding=ActivityUpcomingFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
       binding.ivBack.setOnClickListener {
           onBackPressed()
       }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.upcomingLiveData.observe(this) {
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