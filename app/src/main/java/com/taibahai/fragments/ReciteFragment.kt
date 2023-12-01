package com.taibahai.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.network.base.BaseFragment
import com.taibahai.R
import com.taibahai.activities.QuranChaptersActivity
import com.taibahai.databinding.FragmentReciteBinding
import com.taibahai.hadiths.HadithBooksActivity1


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
        binding.clQuran.setOnClickListener {
            val intent=Intent(requireContext(),QuranChaptersActivity::class.java)
            startActivity(intent)
        }

        binding.clHadith.setOnClickListener {
            val intent=Intent(requireContext(), HadithBooksActivity1::class.java)
            startActivity(intent)
        }
    }


}