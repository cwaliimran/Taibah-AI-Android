package com.taibahai.hadiths

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.play.integrity.internal.t
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelTaibahIslamic
import com.taibahai.databinding.ActivityHadithDetails4Binding
import com.taibahai.utils.showToast

class HadithDetailsActivity4 : BaseActivity() {
    lateinit var binding:ActivityHadithDetails4Binding
    val viewModel: MainViewModelTaibahIslamic by viewModels()
    var id=""
    var isNext=true
    var hadith_id=""



    override fun onCreate() {
        binding=ActivityHadithDetails4Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
       binding.ivBack.setOnClickListener {
           onBackPressed()
       }

        binding.ivNext.setOnClickListener {
            isNext= true
            viewModel.nextPreviousHadith(id,hadith_id,isNext)
        }

        binding.ivPrevious.setOnClickListener {
            isNext=false
            viewModel.nextPreviousHadith(id,hadith_id,isNext)

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
                    binding.tvHadithNo.text = "Hadith No: ${it.data?.data?.hadith_no}"
                    binding.tvArbiAyat.text=it.data?.data?.arabic
                    binding.tvEnglishTranslation.text=it.data?.data?.english_translation
                    hadith_id = it.data?.data?.hadith_no.toString()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }


        viewModel.nextPreviouslLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    binding.tvHadithNo.text = "Hadith No: ${it.data?.data?.hadith_no}"
                    binding.tvArbiAyat.text=it.data?.data?.arabic
                    binding.tvEnglishTranslation.text=it.data?.data?.english_translation
                    hadith_id = it.data?.data?.hadith_no.toString()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }





    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if(bundle!=null)
        {
            id=intent.getStringExtra("id").toString()
            hadith_id=intent.getStringExtra("hadith_id").toString()

        }

        viewModel.getHadithDetail(id)

    }
}