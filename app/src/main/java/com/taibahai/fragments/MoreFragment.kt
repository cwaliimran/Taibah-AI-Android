package com.taibahai.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.network.base.BaseFragment
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.databinding.FragmentMoreBinding

class MoreFragment : BaseFragment() {
    lateinit var binding: FragmentMoreBinding
    val viewModel: MainViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentMoreBinding>(
            inflater, R.layout.fragment_more, container, false
        )

        return binding.root
    }


    override fun viewCreated() {
    }

    override fun clicks() {
    }

    override fun initAdapter() {

    }

    override fun initObservers() {

    }

}