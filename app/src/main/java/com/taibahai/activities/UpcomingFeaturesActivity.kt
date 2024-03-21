package com.taibahai.activities


import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelUpcoming
import com.network.network.NetworkResult
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterFeatures
import com.taibahai.databinding.ActivityUpcomingFeaturesBinding
import com.taibahai.utils.showToast

class UpcomingFeaturesActivity : BaseActivity() {
    lateinit var binding: ActivityUpcomingFeaturesBinding
    val viewModel: MainViewModelAI by viewModels()
    private lateinit var adapter: AdapterFeatures

    val features = mutableListOf<ModelUpcoming.Data>()


    override fun onCreate() {
        binding = ActivityUpcomingFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterFeatures(features, object : OnItemClick{
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                startActivity(Intent(this@UpcomingFeaturesActivity, UpcomingFeaturesDetailActivity::class.java).putExtra(AppConstants.BUNDLE, features[position]))
            }
        })
        binding.recyclerView.adapter = adapter
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
                    it.data?.data?.let { it1 -> features.addAll(it1) }
                    if (features.isNotEmpty()) {
                        adapter.notifyItemRangeInserted(0, features.size)
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
        viewModel.upcoming()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = "Upcoming Features"
        
        binding.appbar.ivRight.visibility = View.GONE
    }

}