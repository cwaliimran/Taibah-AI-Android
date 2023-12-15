package com.taibahai.activities

import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.models.ModelScholars
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.adapters.AdapterImamsOfSunnaDetail
import com.taibahai.databinding.ActivityScholarDetailBinding
import com.taibahai.utils.showToast

class ScholarDetailActivity : BaseActivity() {
    lateinit var binding:ActivityScholarDetailBinding
    val viewModel:MainViewModelAI by viewModels()
    lateinit  var adapter: AdapterImamsOfSunnaDetail
    val bookList:MutableList<ModelScholars.Data.Book> =mutableListOf()
    private var scholarName = ""
    private var scholarEra = ""


    override fun onCreate() {
        binding=ActivityScholarDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            scholarName = intent.getStringExtra("ScholarName").toString()
            scholarEra = intent.getStringExtra("ScholarEra").toString()

            viewModel.scholars()

        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.scholarsLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    binding.tvScholarName.text=scholarName
                    binding.tvScholarEra.text=scholarEra
                    binding.tvScholarDetail.text=it.data?.data?.firstOrNull()?.description

                    bookList.addAll((it.data?.data?.firstOrNull()?.books!!))
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
        bookList.clear()
        adapter= AdapterImamsOfSunnaDetail(bookList)
        binding.rvBookList.adapter=adapter
    }

}