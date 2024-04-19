package com.taibahai.activities

import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelScholars
import com.network.utils.AppConstants
import com.network.viewmodels.MainViewModelAI
import com.taibahai.adapters.AdapterImamsOfSunnaDetail
import com.taibahai.databinding.ActivityScholarDetailBinding

class ScholarDetailActivity : BaseActivity() {
    lateinit var binding: ActivityScholarDetailBinding
    val viewModel: MainViewModelAI by viewModels()
    lateinit var adapter: AdapterImamsOfSunnaDetail
    val bookList: MutableList<ModelScholars.Data.Book> = mutableListOf()
    private var data = ModelScholars.Data()


    override fun onCreate() {
        binding = ActivityScholarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            data = intent.getSerializableExtra(AppConstants.BUNDLE) as ModelScholars.Data
            binding.tvScholarName.text = data.name
            binding.tvScholarEra.text = data.era
            binding.tvScholarDetail.text = data.description
            bookList.addAll((data.books))
            adapter.notifyDataSetChanged()

        }
    }

    override fun initObservers() {
        super.initObservers()
    }

    override fun initAdapter() {
        super.initAdapter()
        bookList.clear()
        adapter = AdapterImamsOfSunnaDetail(bookList)
        binding.rvBookList.adapter = adapter
    }

}