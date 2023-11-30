package com.taibahai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {
    lateinit var binding:ActivityLoginBinding

    override fun onCreate() {
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.clGooglebtn.setOnClickListener {
            val intent= Intent(this,BottomNavigation::class.java)
            startActivity(intent)
        }
    }
}