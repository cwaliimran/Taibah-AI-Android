package com.taibahai.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelInheritanceLaw
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterInheritanceLaw
import com.taibahai.databinding.ActivityInheritanceLawBinding
import com.taibahai.utils.showToast

class InheritanceLawActivity : BaseActivity() {
    lateinit var binding:ActivityInheritanceLawBinding
    val viewModel : MainViewModel by viewModels()
    val showList=ArrayList<ModelInheritanceLaw.Data>()
    lateinit var adapter:AdapterInheritanceLaw



    override fun onCreate() {
        binding=ActivityInheritanceLawBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
    binding.appbar.ivLeft.setOnClickListener {
    onBackPressed()
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
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }
}