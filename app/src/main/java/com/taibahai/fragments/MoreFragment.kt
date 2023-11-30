package com.taibahai.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ConcatAdapter
import com.network.base.BaseFragment
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterHome
import com.taibahai.adapters.AdapterMore
import com.taibahai.adapters.AdapterMoreLevels
import com.taibahai.databinding.FragmentMoreBinding
import com.taibahai.models.ModelHome
import com.taibahai.models.ModelMore
import com.taibahai.models.ModelMoreLevels

class MoreFragment : BaseFragment() {
    lateinit var binding: FragmentMoreBinding
    val viewModel: MainViewModel by viewModels()
    lateinit var adapter: AdapterMore
    val showList = ArrayList<ModelMore>()


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
        showList.clear()
        adapter = AdapterMore(showList)
        val moreFree=ArrayList<ModelMoreLevels>()

        moreFree.add(ModelMoreLevels(R.drawable.search_icon, "30 AI Tokens (monthly)"))
        moreFree.add(ModelMoreLevels(R.drawable.quran_icon, "Quran"))
        moreFree.add(ModelMoreLevels(R.drawable.hadih_icon, "Hadith"))
        moreFree.add(ModelMoreLevels(R.drawable.ads_icon, "Ads"))
        showList.add(ModelMore("Free" ,"",moreFree ))

            val moreLevel1=ArrayList<ModelMoreLevels>()

        moreLevel1.add(ModelMoreLevels(R.drawable.search_icon, "30 AI Tokens (monthly)"))
        moreLevel1.add(ModelMoreLevels(R.drawable.ads_icon, "Ads"))
        showList.add(ModelMore("Level 1" ,"Basic: Silver Subscription Package",moreLevel1) )


        val moreLevel2=ArrayList<ModelMoreLevels>()

        moreLevel2.add(ModelMoreLevels(R.drawable.search_icon, "700 AI Tokens (monthly) "))
        moreLevel2.add(ModelMoreLevels(R.drawable.ads_icon, "Ads"))
        moreLevel2.add(ModelMoreLevels(R.drawable.zakat_icon, "Zakat Calculator"))
        moreLevel2.add(ModelMoreLevels(R.drawable.scholar_icon, "100 Scholars"))
        showList.add(ModelMore("Level 2" ,"Advance Gold Subscription Package",moreLevel2) )

        val moreLevel3=ArrayList<ModelMoreLevels>()

        moreLevel3.add(ModelMoreLevels(R.drawable.search_icon, "Unlimited AI Tokens"))
        moreLevel3.add(ModelMoreLevels(R.drawable.ads_icon, "Ads"))
        moreLevel3.add(ModelMoreLevels(R.drawable.bookspdf_icon, "Books & PDF"))
        moreLevel3.add(ModelMoreLevels(R.drawable.inheritancelaw_icon, "Inheritance Law"))
        showList.add(ModelMore("Level 3" ,"Exclusive: Diamond Subscription Package",moreLevel3) )



        adapter.setDate(showList)
        binding.rvMoreList.adapter=adapter






    }

    override fun initObservers() {

    }

}