package com.taibahai.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.network.base.BaseFragment
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.activities.SettingActivity
import com.taibahai.adapters.AdapterMore
import com.taibahai.databinding.FragmentMoreBinding
import com.taibahai.models.ModelMore
import com.taibahai.models.ModelMoreLevels

class MoreFragment : BaseFragment() {
    lateinit var binding: FragmentMoreBinding
    val viewModel: MainViewModelAI by viewModels()
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
        binding.appbar.ivRight.setOnClickListener {
            val intent= Intent(requireContext(),SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initAdapter() {
        showList.clear()
        adapter = AdapterMore(requireContext(),showList)
        val moreFree=ArrayList<ModelMoreLevels>()

        moreFree.add(ModelMoreLevels("searchFree",R.drawable.search_icon, "30 AI Tokens (monthly)"))
        moreFree.add(ModelMoreLevels("quran",R.drawable.quran_icon, "Quran"))
        moreFree.add(ModelMoreLevels("hadith",R.drawable.hadih_icon, "Hadith"))
        moreFree.add(ModelMoreLevels("",R.drawable.ads_icon, "Ads"))
        showList.add(ModelMore("Free" ,"",moreFree ))

            val moreLevel1=ArrayList<ModelMoreLevels>()

        moreLevel1.add(ModelMoreLevels("searchLevel1",R.drawable.search_icon, "30 AI Tokens (monthly)"))
        moreLevel1.add(ModelMoreLevels("",R.drawable.ads_icon, "Ads"))
        showList.add(ModelMore("Level 1" ,"Silver Subscription Package",moreLevel1) )


        val moreLevel2=ArrayList<ModelMoreLevels>()

        moreLevel2.add(ModelMoreLevels("searchLevel2",R.drawable.search_icon, "700 AI Tokens (monthly) "))
        moreLevel2.add(ModelMoreLevels("zakat_calculator",R.drawable.zakat_icon, "Zakat Calculator"))
        moreLevel2.add(ModelMoreLevels("imams",R.drawable.imams_logo, "4 Sunni Madhabs"))
        showList.add(ModelMore("Level 2" ,"Gold Subscription Package",moreLevel2) )

        val moreLevel3=ArrayList<ModelMoreLevels>()

        moreLevel3.add(ModelMoreLevels("inheritance_law",R.drawable.inheritancelaw_icon, "Inheritance Law"))
        moreLevel3.add(ModelMoreLevels("searchLevel3",R.drawable.search_icon, "Unlimited AI Tokens"))
        moreLevel3.add(ModelMoreLevels("books_pdfs",R.drawable.bookspdf_icon, "Books & PDF"))
        moreLevel3.add(ModelMoreLevels("searchdb",R.drawable.sd_icon, "Search Database Hadith, Surah"))
        showList.add(ModelMore("Level 3" ,"Diamond Subscription Package",moreLevel3) )




        adapter.setDate(showList)
        binding.rvMoreList.adapter=adapter
    }


    override fun apiAndArgs() {
        super.apiAndArgs()
        binding.appbar.tvTitle.setText("More")
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.baseline_settings_24))
        binding.appbar.ivLeft.setVisibility(View.GONE)
    }




}