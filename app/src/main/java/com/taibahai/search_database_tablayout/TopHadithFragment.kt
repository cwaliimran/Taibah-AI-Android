package com.taibahai.search_database_tablayout

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.models.ModelDbSearchHadith
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterDBSearchHadith
import com.taibahai.databinding.FragmentTopHadithBinding
import com.taibahai.utils.showToast


class TopHadithFragment : BaseFragment() {
    lateinit var binding: FragmentTopHadithBinding
    val viewModel: MainViewModelAI by viewModels()
    lateinit var adapter: AdapterDBSearchHadith
    val hadithData = ArrayList<ModelDbSearchHadith.Data>()
    val handler = Handler(Looper.getMainLooper())
    var searchRunnable: Runnable? = null

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
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable?) {
                // Cancel the previous search runnable if it exists
                searchRunnable?.let { handler.removeCallbacks(it) }

                // Define a new search runnable
                searchRunnable = Runnable {
                    if (!editable.isNullOrEmpty()) {
                        viewModel.dbSearch(type = "hadith", keyword = editable.toString())
                    }
                }

                // Post the search runnable with a delay of 2 seconds
                handler.postDelayed(searchRunnable!!, 2000)
            }
        })
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter = AdapterDBSearchHadith(hadithData, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                val intent = Intent(context, DbSearchHadithRMActivity::class.java)
                intent.putExtra("hadith_no", hadithData[position].hadith_no)
                intent.putExtra("book_name", hadithData[position].book_name)
                intent.putExtra("type", hadithData[position].type)
                intent.putExtra("arbi", hadithData[position].arabic)
                intent.putExtra("translation", hadithData[position].english_translation)
                startActivity(intent)
            }
        })
        binding.rvSearchHadith.adapter = adapter
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
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {
                    hadithData.clear()
                    val gson = Gson()
                    val responseData =
                        gson.fromJson(gson.toJson(it.data), ModelDbSearchHadith::class.java)
                    hadithData.addAll(responseData.data)
                    adapter.notifyDataSetChanged()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

}