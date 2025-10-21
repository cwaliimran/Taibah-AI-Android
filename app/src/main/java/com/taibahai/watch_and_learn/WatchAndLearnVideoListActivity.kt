package com.taibahai.watch_and_learn

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.network.base.BaseActivity
import com.network.models.ModelBooks
import com.network.models.ModelPodcastsResponse
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterBooksAndPDF
import com.taibahai.adapters.AdapterPodcastList
import com.taibahai.databinding.ActivityWatchAndLearnListBinding
import com.taibahai.databinding.ActivityWatchAndLearnVideoListBinding
import com.taibahai.utils.showToast
import kotlin.getValue

class WatchAndLearnVideoListActivity: BaseActivity(){
    lateinit var binding: ActivityWatchAndLearnVideoListBinding
    val viewModel: MainViewModelAI by viewModels()
    var page = 1
    var limit = 30
    val showList = ArrayList<ModelPodcastsResponse.Data.Podcast>()
    lateinit var adapter: AdapterPodcastList
    override fun onCreate() {
        binding = ActivityWatchAndLearnVideoListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.getPodcastsByInfluencerLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showList.clear()
                    val responseData: ModelPodcastsResponse? = it.data as ModelPodcastsResponse?
                    responseData?.data?.podcasts?.let { podcastsList ->
                        showList.addAll(podcastsList)
                    }
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = "Videos"
    }
    override fun apiAndArgs() {
        super.apiAndArgs()
        val categoryId = intent.getStringExtra("categoryId").toString()
        viewModel.getPodcastsByInfluencer(categoryId.toInt(),page,limit)
    }
    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterPodcastList(showList)
        binding.rvBooksPDF.adapter = adapter

    }


}