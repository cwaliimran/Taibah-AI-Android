package com.taibahai.hadiths


import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelHadithBooks
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.R
import com.taibahai.adapters.AdapterHadithBooks
import com.taibahai.databinding.ActivityHadithBooks1Binding
import com.taibahai.utils.showToast

class HadithBooksActivity1 : BaseActivity() {
    lateinit var binding: ActivityHadithBooks1Binding
    val showList = ArrayList<ModelHadithBooks.Data>()
    lateinit var adapter: AdapterHadithBooks

    private val viewModel: MainViewModelTaibahIslamic by viewModels()

    override fun onCreate() {
        binding = ActivityHadithBooks1Binding.inflate(layoutInflater)
        binding.appbar.tvTitle.setText("Hadiths")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
        loadAd()
        setContentView(binding.root)
    }


    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterHadithBooks(showList, object : OnItemClick{
            override fun onClick(position: Int, type: String?, data: Any?) {

                val intent = Intent(context, HadithChaptersActivity2::class.java)
                intent.putExtra("ayat_id", showList[position].id)
                intent.putExtra("imam_name",showList[position].imam)
                intent.putExtra("total_chapter",showList[position].total_chapters)
                intent.putExtra("title",showList[position].title)
                intent.putExtra("imam_heading","${showList[position].imam}, ${showList[position].total_chapters} Chapters" )
                startActivity(intent)
            }
        })


        binding.rvHadithBook.adapter = adapter

    }



    override fun initObservers() {
        super.initObservers()
        viewModel.hadithBooksLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showToast(it.data?.message.toString())
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
        viewModel.getHadithBooks()
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