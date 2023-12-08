package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelBooks
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterBooksAndPDF
import com.taibahai.adapters.AdapterHadithBooks
import com.taibahai.databinding.ActivityBooksAndPdfactivityBinding
import com.taibahai.databinding.ActivitySettingBinding
import com.taibahai.models.ModelBooksAndPDF
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelSettings
import com.taibahai.utils.showToast

class BooksAndPDFActivity : BaseActivity() {
    lateinit var binding:ActivityBooksAndPdfactivityBinding
    val showList = ArrayList<ModelBooks.Data>()
    lateinit var adapter:AdapterBooksAndPDF
    val viewModel:MainViewModel by viewModels()



    override fun onCreate() {
        binding=ActivityBooksAndPdfactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
        onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.booksLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showList.addAll((it.data?.data ?: listOf()))
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
        adapter= AdapterBooksAndPDF(showList)

        binding.rvBooksPDF.adapter=adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()

        viewModel.books()
    }


}