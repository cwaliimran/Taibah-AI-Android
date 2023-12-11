package com.taibahai.hadiths

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.network.base.BaseActivity
import com.network.models.ModelDbSearchHadith
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.adapters.AdapterHadithChapter
import com.taibahai.databinding.ActivityChapterHadiths3Binding
import com.taibahai.models.ModelChapterHadiths
import com.taibahai.models.ModelHadithChapter
import com.taibahai.utils.showToast

class ChapterHadithsActivity3 : BaseActivity() {
    lateinit var binding:ActivityChapterHadiths3Binding
    val showList=ArrayList<ModelDbSearchHadith.Data>()
    lateinit var adapter: AdapterChapterHadiths
    val viewModel: MainViewModel by viewModels()



    override fun onCreate() {
        binding=ActivityChapterHadiths3Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.dbSearchLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    val gson = Gson()
                    val responseData = gson.fromJson(gson.toJson(it.data), ModelDbSearchHadith::class.java)
                    showList.addAll(responseData.data)
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
        adapter= AdapterChapterHadiths(showList)
        binding.rvChapterHadiths.adapter=adapter

    }
}