package com.taibahai.search_database_tablayout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.network.base.BaseFragment
import com.taibahai.R
import com.taibahai.adapters.AdapterChapterHadiths
import com.taibahai.databinding.FragmentTopHadithBinding
import com.taibahai.models.ModelChapterHadiths


class TopHadithFragment : BaseFragment() {
    lateinit var binding: FragmentTopHadithBinding

    val showList=ArrayList<ModelChapterHadiths>()

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
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterChapterHadiths(showList)
        showList.add(ModelChapterHadiths("Hadith No: 1", "Sahih al-Bukhari","al-Bukhari","\"إنما الأعمال بالنيات.\"","\"The reward of deeds depends upon the intentions.\""))
        showList.add(ModelChapterHadiths("Hadith No: 1", "Sahih al-Bukhari","al-Bukhari","\"إنما الأعمال بالنيات.\"","\"The reward of deeds depends upon the intentions.\""))


        adapter.setDate(showList)
        binding.rvSearchHadith.adapter=adapter
    }



}