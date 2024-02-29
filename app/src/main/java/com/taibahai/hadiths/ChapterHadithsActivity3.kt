package com.taibahai.hadiths

import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.gson.Gson
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelChapterHadith3
import com.network.models.ModelDbSearchHadith
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.databinding.ActivityChapterHadiths3Binding
import com.taibahai.utils.showToast

class ChapterHadithsActivity3 : BaseActivity() {
    lateinit var binding: ActivityChapterHadiths3Binding
    val showList = ArrayList<ModelChapterHadith3.Data>()
    lateinit var adapter: AdapterChapterHadiths
    val viewModel: MainViewModelTaibahIslamic by viewModels()
    var chapter_id = ""
    var chapterName = ""
    var sequenceNo = ""
    private var currentPageNo = 1
    private var totalPages: Int = 1


    override fun onCreate() {
        binding = ActivityChapterHadiths3Binding.inflate(layoutInflater)
        loadAd()
        setContentView(binding.root)
    }


    override fun clicks() {

        binding.rvChapterHadiths.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = (recyclerView.layoutManager as LinearLayoutManager?)!!
                if (dy > 0) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == showList.size - 1) {
                        if (currentPageNo < totalPages) {
                            currentPageNo += 1
                            viewModel.getChapterHadiths(currentPageNo, chapter_id)
                        }
                    }
                }
            }
        })

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.chapterHadithLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                    totalPages = it.data?.total_pages!!
                    val oldSize = showList.size
                    it.data?.let { it1 -> showList.addAll(it1.data) }
                    binding.tvChapterHadith.text = chapterName
                    binding.tvChaptersFrom.text = sequenceNo
                    if (oldSize == 0) {
                        initAdapter()
                    } else {
                        adapter.notifyItemRangeInserted(oldSize, showList.size)
                    }

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }


    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterChapterHadiths(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?) {
                super.onClick(position, type, data)
                val intent = Intent(context, HadithDetailsActivity4::class.java)
                val currentPosition=showList[position].id
                intent.putExtra("ayat_id",currentPosition)
                intent.putExtra("chapter_id",showList[position].chapter_id )
                intent.putExtra("hadith_number", showList[position].hadith_no)
                intent.putExtra("sequence", sequenceNo)
                intent.putExtra("chapter_name", chapterName)
                intent.putExtra("book_name", showList[position].book_name)
                intent.putExtra("type", showList[position].type)
                startActivity(intent)
            }

        })
        binding.rvChapterHadiths.adapter = adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            chapter_id = intent.getStringExtra("ayat_id").toString()
            chapterName = intent.getStringExtra("chapter_name").toString()
            sequenceNo = intent.getStringExtra("sequence").toString()
        }
        viewModel.getChapterHadiths(currentPageNo, chapter_id)
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