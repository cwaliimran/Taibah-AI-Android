package com.taibahai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.databinding.ActivityZakatCalculatorBinding

class ZakatCalculatorActivity : BaseActivity() {
    lateinit var binding:ActivityZakatCalculatorBinding


    override fun onCreate() {
        binding=ActivityZakatCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivRight.setOnClickListener {
            val intent = Intent(this, ZakatInfoActivity::class.java)
            startActivity(intent)
        }

        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Zakat Calculator")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.info))
    }
}