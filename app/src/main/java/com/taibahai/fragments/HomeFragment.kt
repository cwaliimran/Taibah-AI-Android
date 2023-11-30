package com.taibahai.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.network.base.BaseFragment
import com.network.interfaces.OnItemClick
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.activities.CreatePostActivity
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.FragmentHomeBinding
import com.taibahai.models.ModelHome
import com.taibahai.models.ModelHomeSlider
import com.taibahai.utils.showToast


class HomeFragment : BaseFragment(), OnItemClick {
    lateinit var binding: FragmentHomeBinding

    lateinit var adapter: AdapterHome
    val showList = ArrayList<ModelHome>()
    val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater, R.layout.fragment_home, container, false
        )

        return binding?.getRoot()
    }


    override fun viewCreated() {
    }

    override fun clicks() {
        binding.ivCreatePostIcon.setOnClickListener {
            val intent= Intent(requireContext(),CreatePostActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.homeLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            activity?.displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    activity?.displayLoading(true)
                }

                is NetworkResult.Success -> {

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        showList.clear()
        adapter = AdapterHome(this, showList)
        showList.add(
            ModelHome(R.drawable.hassan,"Hassan Ali", "12 minutes ago",
                "Discover the spiritual depths and wisdom that illuminate your path with insights on Islamic teachings and practices.",R.drawable.rectangle_92,) )

        adapter.setDate(showList)
        binding.rvHome.adapter=adapter


    }

}