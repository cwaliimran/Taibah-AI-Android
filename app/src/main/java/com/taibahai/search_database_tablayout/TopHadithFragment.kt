package com.taibahai.search_database_tablayout

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.network.base.BaseFragment
import com.network.models.ModelDBSearch
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.databinding.FragmentTopHadithBinding
import com.taibahai.models.ModelChapterHadiths
import com.taibahai.utils.showToast


class TopHadithFragment : BaseFragment() {
    lateinit var binding: FragmentTopHadithBinding
    val showList=ArrayList<ModelDBSearch.Data>()
    val viewModel:MainViewModel by viewModels()
    lateinit var adapter:AdapterChapterHadiths



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
               viewModel.dbSearch(type = "hadith", keyword = binding.etSearch.text.toString())
           }

       }
   })
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterChapterHadiths(showList)
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
                    showList.clear()
                showList.addAll(it.data?.data?: listOf())
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



    }



}