package com.taibahai.activities

import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.network.base.BaseActivity
import com.network.models.ModelNotifications
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterNotification
import com.taibahai.databinding.ActivityNotificationBinding
import com.taibahai.utils.showToast

class NotificationActivity : BaseActivity() {
    lateinit var binding: ActivityNotificationBinding
    lateinit var adapter: AdapterNotification
    val showList = mutableListOf<ModelNotifications.Data>()
    val viewModel: MainViewModelAI by viewModels()
    private var currentPageNo = 1
    private var totalPages: Int = 0
    override fun onCreate() {
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.rvNotification.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                if (dy > 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == showList.size - 1) {
                        if (currentPageNo < totalPages) {
                            currentPageNo += 1
                            viewModel.notifications(pageno = currentPageNo)
                        }
                    }

                }
            }
        })
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterNotification(showList)
        binding.rvNotification.adapter = adapter
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = "Notifications"
        
        
       if (!isAdsFree) loadAd() else binding.adView.visibility = View.GONE
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.notifications(currentPageNo)
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.notificationsLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    totalPages = it.data?.total_pages!!
                    val oldSize = showList.size
                    showList.addAll((it.data?.data ?: listOf()))
                    if (oldSize == 0) {
                        initAdapter()
                    } else {
                        adapter.notifyItemRangeInserted(oldSize, showList.size)
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }



    private fun loadAd() {
        //load ad
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()

    }


    public override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }

}