package com.taibahai.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
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
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_more, container, false
        )

        return binding.root
    }


    override fun viewCreated() {
        if (!isAdsFree) loadAd() else binding.adView.visibility = View.GONE
    }

    override fun clicks() {
        binding.appbar.ivRight.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initAdapter() {
        showList.clear()
        adapter = AdapterMore(requireContext(), showList)
        val moreFree = ArrayList<ModelMoreLevels>()

        moreFree.add(ModelMoreLevels("quran", R.drawable.quran_icon, "Quran"))
        moreFree.add(ModelMoreLevels("hadith", R.drawable.hadih_icon, "Hadith"))
        moreFree.add(ModelMoreLevels("", R.drawable.ads_icon, "Ads"))
        showList.add(ModelMore("Free", "30 AI Tokens (monthly)", moreFree))

        val moreLevel1 = ArrayList<ModelMoreLevels>()

        moreLevel1.add(ModelMoreLevels("", R.drawable.ads_icon, "Ads"))
        showList.add(ModelMore("Level 1", "Silver Package\n300 AI Tokens (monthly)", moreLevel1))


        val moreLevel2 = ArrayList<ModelMoreLevels>()

        moreLevel2.add(
            ModelMoreLevels(
                "zakat_calculator", R.drawable.zakat_icon, "Zakat Calculator"
            )
        )
        moreLevel2.add(ModelMoreLevels("imams", R.drawable.imams_logo, "Four Imams"))
        showList.add(ModelMore("Level 2", "Gold Package\n700 AI Tokens (monthly)", moreLevel2))

        val moreLevel3 = ArrayList<ModelMoreLevels>()

        moreLevel3.add(
            ModelMoreLevels(
                "inheritance_law", R.drawable.inheritancelaw_icon, "Inheritance Law"
            )
        )
        moreLevel3.add(
            ModelMoreLevels(
                "searchdb", R.drawable.sd_icon, "Search Database Hadith, Surah"
            )
        )

        moreLevel3.add(ModelMoreLevels("books_pdfs", R.drawable.bookspdf_icon, "Islamic Literature"))
        showList.add(ModelMore("Level 3", "Diamond Package\nUnlimited AI Tokens (monthly)", moreLevel3))




        adapter.setData(showList)
        binding.rvMoreList.adapter = adapter
    }


    override fun apiAndArgs() {
        super.apiAndArgs()
        binding.appbar.tvTitle.text = getString(R.string.more)
        show(binding.appbar.ivRight)
        binding.appbar.ivRight.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.baseline_settings_24
            )
        )
        binding.appbar.ivLeft.visibility = View.GONE
    }

    private fun loadAd() {
        //load ad
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        adapter.notifyDataSetChanged()

    }


    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }


}