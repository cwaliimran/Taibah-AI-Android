package com.taibahai.activities

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelBooksCategories
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.adapters.AdapterBooksCategories
import com.taibahai.databinding.ActivityBooksAndPdfactivityBinding
import com.taibahai.utils.showToast

class BooksCategoriesActivity : BaseActivity() {
    lateinit var binding: ActivityBooksAndPdfactivityBinding
    val showList = ArrayList<ModelBooksCategories.Data>()
    lateinit var adapter: AdapterBooksCategories
    val viewModel: MainViewModelAI by viewModels()


    override fun onCreate() {
        binding = ActivityBooksAndPdfactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.booksCategoriesLiveData.observe(this) {
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
                    showList.add(ModelBooksCategories.Data("1", "All Books", "1", "", "", ""))
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
        adapter = AdapterBooksCategories(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)

                val intent = Intent(context, BooksAndPDFActivity::class.java)
                intent.putExtra("categoryId", showList[position].id)
                intent.putExtra("title", showList[position].title)
                context.startActivity(intent)
            }
        })

        binding.rvBooksPDF.adapter = adapter

    }

    override fun apiAndArgs() {
        super.apiAndArgs()

        viewModel.booksCategories()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = "Categories"


    }


}