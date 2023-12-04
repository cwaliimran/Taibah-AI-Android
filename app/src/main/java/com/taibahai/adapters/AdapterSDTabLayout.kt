package com.taibahai.adapters


import androidx.fragment.app.Fragment

import androidx.fragment.app.FragmentActivity

import androidx.viewpager2.adapter.FragmentStateAdapter
import com.taibahai.search_database_tablayout.TopHadithFragment
import com.taibahai.search_database_tablayout.TopQuranFragment


class AdapterSDTabLayout(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)
            TopHadithFragment()
        else
            TopQuranFragment()
    }
}