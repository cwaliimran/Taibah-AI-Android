package com.taibahai.activities

import android.view.View
import com.network.base.BaseActivity
import com.network.models.ModelUpcoming
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.databinding.ActivityUpcomingFeaturesDetailBinding

class UpcomingFeaturesDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityUpcomingFeaturesDetailBinding
    private val TAG = ActivityUpcomingFeaturesDetailBinding::class.java.simpleName


    override fun onCreate() {
        binding = ActivityUpcomingFeaturesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()

        val feature = bundle?.getSerializable(AppConstants.BUNDLE) as? ModelUpcoming.Data
        if (feature != null) {
            binding.data = feature
        }
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = getString(R.string.feature_details)
        
        binding.appbar.ivRight.visibility = View.GONE
    }

}