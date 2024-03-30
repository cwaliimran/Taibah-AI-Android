package com.taibahai.hadiths

import android.content.Intent
import androidx.activity.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelHadithChapter2
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.adapters.AdapterHadithChapter
import com.taibahai.databinding.ActivityHadithChapters2Binding
import com.taibahai.utils.showToast
import android.view.View
class HadithChaptersActivity2 : BaseActivity() {
    lateinit var binding: ActivityHadithChapters2Binding
    val showList = ArrayList<ModelHadithChapter2.Data>()
    lateinit var adapter: AdapterHadithChapter
    var book_id = ""
    var imam_name = ""
    var total_chapter = ""
    var title = ""
    var imamHeading=""
    private val viewModel: MainViewModelTaibahIslamic by viewModels()


    override fun onCreate() {
        binding = ActivityHadithChapters2Binding.inflate(layoutInflater)
       if (!isAdsFree) loadAd() else binding.adView.visibility = View.GONE
        setContentView(binding.root)
    }


    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterHadithChapter(showList, object : OnItemClick {
            override fun onClick(
                position: kotlin.Int,
                type: kotlin.String?,
                data: kotlin.Any?,
                view: android.view.View?
            ) {
                super.onClick(position, type, data, view)
                val intent = Intent(context, ChapterHadithsActivity3::class.java)
                intent.putExtra("ayat_id", showList[position].id)
                intent.putExtra("chapter_name", showList[position].chapter_name)
                intent.putExtra("sequence", showList[position].hadith_number)
                intent.putExtra("title", title)
                context.startActivity(intent)
            }
        })

        binding.rvHadithChapter.adapter = adapter

    }

    override fun initObservers() {
        super.initObservers()
        viewModel.hadithChapterLiveData.observe(this) {
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
                    binding.tvHadithChapter.text = title
                    binding.tvImamHeading.text=imamHeading

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
        if (bundle != null) {
            book_id = intent.getStringExtra("ayat_id").toString()
            imam_name = intent.getStringExtra("imam_name").toString()
            total_chapter = intent.getStringExtra("total_chapter").toString()
            title = intent.getStringExtra("title").toString()
            imamHeading = intent.getStringExtra("imam_heading").toString()
        }

        viewModel.getHadithChapters(1, book_id, "yes")

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