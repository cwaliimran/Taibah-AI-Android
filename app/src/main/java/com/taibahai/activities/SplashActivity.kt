package com.taibahai.activities

import android.content.Intent
import com.network.base.BaseActivity
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivitySplashBinding
import com.taibahai.search_database_tablayout.SearchDatabaseActivity

class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding


    override fun onCreate() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (currentUser == null) {
            if (!AppClass.sharedPref.getBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED)) {
                //provide free tokens to user
                AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, 30)
                AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)
            }
            val intent = Intent(this, SearchWholeQuranActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {

//            val intent = Intent(this, BottomNavigation::class.java)
            val intent = Intent(this, SearchWholeQuranActivity::class.java)
            startActivity(intent)
            finishAffinity()

        }
    }

    override fun clicks() {

    }
}