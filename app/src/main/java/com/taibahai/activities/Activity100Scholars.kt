package com.taibahai.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelScholars
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.Adapter100Scholars
import com.taibahai.databinding.ActivityActivity100ScholarsBinding
import com.taibahai.models.Model100Scholars
import com.taibahai.models.ModelSettings
import com.taibahai.utils.showToast

class Activity100Scholars : BaseActivity() {
    lateinit var binding:ActivityActivity100ScholarsBinding
    lateinit  var adapter:Adapter100Scholars
    val scholarList:MutableList<ModelScholars.Data> =mutableListOf()
    val viewModel : MainViewModel by viewModels()



    override fun onCreate() {
        binding= ActivityActivity100ScholarsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
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




}