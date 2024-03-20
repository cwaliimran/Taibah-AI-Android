package com.taibahai.search_database_tablayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.adapters.RadioGroupBindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterSDTabLayout
import com.taibahai.databinding.ActivitySearchDatabaseBinding

class SearchDatabaseActivity : BaseActivity() {
    lateinit var binding:ActivitySearchDatabaseBinding
    lateinit var adapter: AdapterSDTabLayout


    override fun onCreate() {
        binding=ActivitySearchDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = AdapterSDTabLayout(this)
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Hadith"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Quran"))
        binding.viewPager.adapter = adapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    binding.viewPager.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })




    }



    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Search Database")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }
}