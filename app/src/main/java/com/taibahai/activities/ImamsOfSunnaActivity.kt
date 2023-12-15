package com.taibahai.activities

import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelScholars
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.Adapter100Scholars
import com.taibahai.databinding.ActivityImamsOfSunnaBinding
import com.taibahai.utils.showToast

class ImamsOfSunnaActivity : BaseActivity() {
    lateinit var binding:ActivityImamsOfSunnaBinding
    lateinit  var adapter:Adapter100Scholars
    val scholarList:MutableList<ModelScholars.Data> =mutableListOf()
    val viewModel : MainViewModelAI by viewModels()



    override fun onCreate() {
        binding= ActivityImamsOfSunnaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
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
                    scholarList.addAll((it.data?.data ?: listOf()))
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        scholarList.clear()
        adapter= Adapter100Scholars(scholarList)
        binding.rv100Scholars.adapter=adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.scholars()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("4 major imams of Sunna")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }




}