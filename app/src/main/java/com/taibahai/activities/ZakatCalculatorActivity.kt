package com.taibahai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        binding.ivZakatInfo.setOnClickListener {
            val intent = Intent(this, ZakatInfoActivity::class.java)
            startActivity(intent)
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}