package com.taibahai.search_database_tablayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.network.base.BaseFragment
import com.taibahai.R
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.FragmentTopQuranBinding
import com.taibahai.models.ModelQuranChapter

class TopQuranFragment : BaseFragment() {
    lateinit var binding: FragmentTopQuranBinding
    val showList=ArrayList<ModelQuranChapter>()
    lateinit var adapter: AdapterQuranChapter



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
    }

    override fun initAdapter() {
        super.initAdapter()

        adapter= AdapterQuranChapter(showList)
        showList.add(ModelQuranChapter(1, "Al-Faatiha","The opening(7) "))
        showList.add(ModelQuranChapter(2, "Al-Faatiha","The opening(7) "))
        showList.add(ModelQuranChapter(3, "Al-Faatiha","The opening(7) "))
        showList.add(ModelQuranChapter(4, "Al-Faatiha","The opening(7) "))
        adapter.setDate(showList)
        binding.rvSearchQuran.adapter=adapter

    }

}