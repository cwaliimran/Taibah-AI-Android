package com.taibahai.activities

import android.view.View
import androidx.activity.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.network.base.BaseActivity
import com.network.models.ModelInheritanceLaw
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterInheritanceLaw
import com.taibahai.databinding.ActivityInheritanceLawBinding
import com.taibahai.utils.showToast

class InheritanceLawActivity : BaseActivity() {
    lateinit var binding:ActivityInheritanceLawBinding
    val viewModel : MainViewModelAI by viewModels()
    val showList=ArrayList<ModelInheritanceLaw.Data>()
    lateinit var adapter:AdapterInheritanceLaw



    override fun onCreate() {
        binding=ActivityInheritanceLawBinding.inflate(layoutInflater)
       if (!isAdsFree) loadAd() else binding.adView.visibility = View.GONE
        setContentView(binding.root)
    }

    override fun clicks() {
    binding.appbar.ivLeft.setOnClickListener {
    onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterInheritanceLaw(showList)
        binding.rvInheritanceLaw.adapter=adapter

    }

    override fun initObservers() {
        super.initObservers()
        viewModel.inheritanceLawLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    it.data?.data?.let { it1 -> showList.addAll(it1) }
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.getInheritanceLaw()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Islamic Law of Inheritance")
        
        
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