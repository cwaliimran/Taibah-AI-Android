package com.taibahai.hadiths

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.play.integrity.internal.t
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.databinding.ActivityHadithDetails4Binding
import com.taibahai.utils.showToast

class HadithDetailsActivity4 : BaseActivity() {
    lateinit var binding: ActivityHadithDetails4Binding
    val viewModel: MainViewModelTaibahIslamic by viewModels()
    var id = ""
    var isNext = true
    var totalHadithNo = ""
    var the_id = ""
    var hadithNo = ""
    var chapterName = ""
    var chapterId = ""
    var bookName=""
    var type=""


    override fun onCreate() {
        binding = ActivityHadithDetails4Binding.inflate(layoutInflater)
        loadAd()
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivNext.setOnClickListener {
            isNext = true
            viewModel.nextPreviousHadith(chapterId, the_id, isNext)
        }

        binding.ivPrevious.setOnClickListener {
            isNext = false
            viewModel.nextPreviousHadith(chapterId, the_id, isNext)

        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.hadithDetailLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                    binding.tvHadithChapter.text = chapterName
                    binding.tvNoOfHadiths.text = totalHadithNo
                    binding.tvHadithNo.text = "Hadith No: ${it.data!!.data.hadith_no}"
                    binding.tvArbiAyat.text = it.data?.data?.arabic
                    binding.tvEnglishTranslation.text = it.data?.data?.english_translation
                    binding.tvBookName.text=bookName
                    binding.tvHadithType.text=type
                    the_id = it.data?.data?.id.toString()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }


        viewModel.nextPreviouslLiveData.observe(this) { result ->
            if (result == null) {
                return@observe
            }
            displayLoading(false)

            when (result) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                    binding.tvHadithNo.text = "Hadith No: ${result.data!!.data.hadith_no}"
                    binding.tvArbiAyat.text = result.data?.data?.arabic
                    binding.tvEnglishTranslation.text = result.data?.data?.english_translation
                    binding.tvBookName.text=bookName
                    binding.tvHadithType.text=type
                    the_id = result.data?.data?.id.toString()
                }

                is NetworkResult.Error -> {
                    Toast.makeText(this, "No More Hadith", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            id = intent.getStringExtra("ayat_id").toString()
            chapterName = intent.getStringExtra("chapter_name").toString()
            totalHadithNo = intent.getStringExtra("sequence").toString()
            hadithNo = intent.getStringExtra("hadith_number").toString()
            chapterId = intent.getStringExtra("chapter_id").toString()
            bookName = intent.getStringExtra("book_name").toString()
            type = intent.getStringExtra("type").toString()


            viewModel.getHadithDetail(id)
        }
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