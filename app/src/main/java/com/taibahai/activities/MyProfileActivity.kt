package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.ActivityMyProfileBinding
import com.taibahai.models.ModelHome
import com.taibahai.utils.showToast

class MyProfileActivity : BaseActivity() {
    lateinit var binding:ActivityMyProfileBinding
    lateinit var adapter: AdapterHome
    val showList = ArrayList<ModelHome>()
    val viewModel : MainViewModel by viewModels()



    override fun onCreate() {
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        showList.clear()
        adapter = AdapterHome(showList)
        showList.add(
            ModelHome(R.drawable.hassan,"Hassan Ali", "12 minutes ago",
                "Discover the spiritual depths and wisdom that illuminate your path with insights on Islamic teachings and practices.",R.drawable.rectangle_92,) )

        adapter.setDate(showList)
        binding.rvProfile.adapter=adapter


    }


    override fun initObservers() {
        super.initObservers()
        viewModel.socialLoginLiveData.observe(this) {
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