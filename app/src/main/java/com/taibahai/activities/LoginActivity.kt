package com.taibahai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivityLoginBinding
import com.taibahai.utils.showToast

class LoginActivity : BaseActivity() {
    lateinit var binding:ActivityLoginBinding
    val viewModel : MainViewModel by viewModels()


    override fun onCreate() {
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.clGooglebtn.setOnClickListener {
            val intent= Intent(this,BottomNavigation::class.java)
            startActivity(intent)
        }

        binding.clAppleBtn.setOnClickListener {
            val intent= Intent(this,BottomNavigation::class.java)
            startActivity(intent)
        }

        binding.clFacebookBtn.setOnClickListener {
            val intent= Intent(this,BottomNavigation::class.java)
            startActivity(intent)
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.socialLoginLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }
}