package com.taibahai.watch_and_learn

import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelPodcastsResponse
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.adapters.AdapterPodcastList
import com.taibahai.databinding.ActivityWatchAndLearnVideoListBinding
import com.taibahai.fragments.FullscreenVideoDialog
import com.taibahai.utils.showToast

class WatchAndLearnVideoListActivity : BaseActivity() {
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
        viewModel.getPodcastsByInfluencer(categoryId.toInt(), page, limit)
    }

    override fun initAdapter() {
        super.initAdapter()
//        adapter = AdapterPodcastList(showList)
        adapter = AdapterPodcastList(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                val videoUrl = showList[position].media_url
                val dialog = FullscreenVideoDialog.newInstance(videoUrl)
                dialog.show(supportFragmentManager, "video_dialog")
            }
        })
        binding.rvBooksPDF.adapter = adapter


//        binding.rvBooksPDF.adapter = adapter

    }


}