package com.taibahai.bottom_navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.taibahai.R
import com.taibahai.databinding.ActivityBottomNavigationBinding
import com.taibahai.fragments.ProfileFragment
import com.taibahai.fragments.HomeFragment
import com.taibahai.fragments.RankFragment
import com.taibahai.fragments.SearchFragment
import com.taibahai.fragments.MoreFragment

class BottomNavigation : AppCompatActivity() {
    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding?.bottomNavigationView?.itemIconTintList = null
        replaceFragment(HomeFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.home -> replaceFragment(HomeFragment())
                R.id.rank -> replaceFragment(RankFragment())
                R.id.search -> replaceFragment(SearchFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.more -> replaceFragment(MoreFragment())

                else -> {

                }

            }

            true

        }


    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()


    }
}