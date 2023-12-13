package com.taibahai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.play.integrity.internal.l
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {
    lateinit var binding:ActivitySplashBinding


    override fun onCreate() {
        binding=ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({

        }, 2000)
        if (currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {

            val intent = Intent(this, BottomNavigation::class.java)
            startActivity(intent)
            finishAffinity()

        }
    }

    override fun clicks() {

    }
}