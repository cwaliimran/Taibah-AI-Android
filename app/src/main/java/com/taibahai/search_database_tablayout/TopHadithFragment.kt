package com.taibahai.search_database_tablayout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.network.base.BaseFragment
import com.network.models.ModelDbSearchHadith
import com.network.models.ModelDbSearchQuran
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.adapters.AdapterDBSearchHadith
import com.taibahai.databinding.FragmentTopHadithBinding
import com.taibahai.utils.showToast


class TopHadithFragment : BaseFragment() {
    lateinit var binding: FragmentTopHadithBinding
    val hadithData=ArrayList<ModelDbSearchHadith.Data>()
    val viewModel:MainViewModel by viewModels()
    lateinit var adapter:AdapterDBSearchHadith



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentTopHadithBinding>(
            inflater, R.layout.fragment_top_hadith, container, false
        )

        return binding.getRoot()
    }

    override fun viewCreated() {

    }

    override fun clicks() {
           binding.etSearch.addTextChangedListener(object : TextWatcher {
       override fun beforeTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
       }

       override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
       }

       override fun afterTextChanged(editable: Editable?) {
           if (binding.etSearch.text.toString()!=""){
               viewModel.dbSearch( type = "hadith", keyword = binding.etSearch.text.toString())
           }

       }
   })
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterDBSearchHadith(hadithData)
        binding.rvSearchHadith.adapter=adapter
    }

    override fun initObservers() {

        super.initObservers()
        viewModel.dbSearchLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                   activity?. displayLoading(true)
                }

                is NetworkResult.Success -> {
                    hadithData.clear()
                    val gson = Gson()
                    val responseData = gson.fromJson(gson.toJson(it.data), ModelDbSearchHadith::class.java)
                    hadithData.addAll(responseData.data)
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }
}