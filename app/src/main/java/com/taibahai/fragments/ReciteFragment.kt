package com.taibahai.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.network.base.BaseFragment
import com.taibahai.R
import com.taibahai.databinding.FragmentMoreBinding
import com.taibahai.databinding.FragmentReciteBinding


class ReciteFragment : BaseFragment() {

    lateinit var binding: FragmentReciteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentReciteBinding>(inflater, R.layout.fragment_recite, container, false)

        return binding.root
    }

    override fun viewCreated() {
    }

    override fun clicks() {
    }


}