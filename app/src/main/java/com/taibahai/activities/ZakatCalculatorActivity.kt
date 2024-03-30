package com.taibahai.activities

import android.content.Intent
import androidx.core.content.ContextCompat
import com.network.base.BaseActivity
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.databinding.ActivityZakatCalculatorBinding

class ZakatCalculatorActivity : BaseActivity() {
    lateinit var binding: ActivityZakatCalculatorBinding


    override fun onCreate() {
        binding = ActivityZakatCalculatorBinding.inflate(layoutInflater)
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
        binding.btnGoldRate.setOnClickListener {
            startActivity(
                Intent(
                    this@ZakatCalculatorActivity,
                    WebViewActivity::class.java
                ).putExtra(AppConstants.BUNDLE, "gold")
            )
        }
        binding.btnGoldRate.setOnClickListener {
            startActivity(
                Intent(
                    this@ZakatCalculatorActivity,
                    WebViewActivity::class.java
                ).putExtra(AppConstants.BUNDLE, "gold")
            )
        }
        binding.btnSilverRate.setOnClickListener {
            startActivity(
                Intent(
                    this@ZakatCalculatorActivity,
                    WebViewActivity::class.java
                ).putExtra(AppConstants.BUNDLE, "silver")
            )
        }
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = getString(R.string.zakat_calculator)


        show(binding.appbar.ivRight)
        binding.appbar.ivRight.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.info))


    }
}