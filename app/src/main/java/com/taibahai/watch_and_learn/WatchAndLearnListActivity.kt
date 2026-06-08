package com.taibahai.watch_and_learn

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelBooksCategories
import com.network.models.ModelGetInfluencers
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.activities.BooksAndPDFActivity
import com.taibahai.adapters.AdapterBooksCategories
import com.taibahai.adapters.AdapterGetInfluencers
import com.taibahai.databinding.ActivityBooksAndPdfactivityBinding
import com.taibahai.databinding.ActivityWatchAndLearnListBinding
import com.taibahai.utils.showToast
import kotlin.getValue

class WatchAndLearnListActivity  : BaseActivity() {
    lateinit var binding: ActivityWatchAndLearnListBinding
    val viewModel: MainViewModelAI by viewModels()
    val showList = ArrayList<ModelGetInfluencers.Data.Influencer>()
    lateinit var adapter: AdapterGetInfluencers
    var page = 1
    var limit = 30
    override fun onCreate() {
        binding = ActivityWatchAndLearnListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets(binding.root)
    }

    override fun clicks() {
        binding.appbar.tvTitle.text="Watch & Learn"
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }


    override fun initObservers() {
        super.initObservers()
        viewModel.getInfluencersLiveData.observe(this) {
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
                    val responseData = it.data
                    responseData?.data?.influencers?.let { influencersList ->
                        showList.addAll(influencersList)
                    }
                    adapter.setData(showList)
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }
    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterGetInfluencers(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                val intent = Intent(context, WatchAndLearnVideoListActivity::class.java)
                intent.putExtra("categoryId", showList[position].id)
                intent.putExtra("title", showList[position].name)
                context.startActivity(intent)
            }
        })
        binding.rvScholarList.adapter = adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.getInfluencers(page, limit)
    }
}