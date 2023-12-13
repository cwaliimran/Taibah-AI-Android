package com.taibahai.activities


import android.view.View
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
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
       binding.appbar.ivLeft.setOnClickListener {
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
                    Glide.with(this).load(it.data?.data?.firstOrNull()?.icon).into(binding.ivIcon)
                    binding.tvTitle.text=it.data?.data?.firstOrNull()?.title
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.upcoming()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Upcoming Features")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }

}