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
import com.network.models.ModelDbSearchQuran
import com.network.models.ModelSurah
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterDbSearchQuran
import com.taibahai.databinding.FragmentTopQuranBinding
import com.taibahai.utils.showToast

class TopQuranFragment : BaseFragment() {
    lateinit var binding: FragmentTopQuranBinding
    val showList=ArrayList<ModelSurah>()
    lateinit var adapter: AdapterDbSearchQuran
    val quranData=ArrayList<ModelDbSearchQuran.Data>()
    val viewModel: MainViewModelAI by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentTopQuranBinding>(
            inflater, R.layout.fragment_top_quran, container, false
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
                    viewModel.dbSearch( type = "quran", keyword = binding.etSearch.text.toString())
                }

            }
        })
    }

    override fun initAdapter() {
        super.initAdapter()

        adapter= AdapterDbSearchQuran(quranData)
        binding.rvSearchQuran.adapter=adapter

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
                    quranData.clear()
                    val gson = Gson()
                    val responseData = gson.fromJson(gson.toJson(it.data), ModelDbSearchQuran::class.java)
                    quranData.addAll(responseData.data)
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

}