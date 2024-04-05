package com.taibahai.activities

import android.content.Intent
import androidx.core.content.ContextCompat
import com.network.base.BaseActivity
import com.network.utils.AppConstants
import com.taibahai.R
import com.taibahai.databinding.ActivityZakatCalculatorBinding
import com.taibahai.utils.addTextWatcher

class ZakatCalculatorActivity : BaseActivity() {
    lateinit var binding: ActivityZakatCalculatorBinding

    // Nisab values
    var nisabGold = 0.0
    var nisabSilver = 0.0

    var cashInHand: Double = 0.0
    var cashForFuturePurpose: Double = 0.0
    var cashInLoans: Double = 0.0
    var investments: Double = 0.0
    var goldValue: Double = 0.0
    var silverValue: Double = 0.0
    var stockValue: Double = 0.0
    var borrowedMoney: Double = 0.0
    var wagesDue: Double = 0.0
    var billsDue: Double = 0.0
    

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

        binding.radiogroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbValueOfGold -> {
                    nisabSilver = 0.0
                    if (binding.etValueOfGold.text.toString().isNotEmpty()) {
                        nisabGold = binding.etValueOfGold.text.toString().toDouble()
                        calculateZakat()
                    }
                }

                R.id.rbValueOfSilver -> {
                    nisabGold = 0.0
                    if (binding.etSilverRate.text.toString().isNotEmpty()) {
                        nisabSilver = binding.etSilverRate.text.toString().toDouble()
                        calculateZakat()
                    }
                }

                else -> {}
            }
        }

        binding.btnGoldRate.setOnClickListener {
            startActivity(
                Intent(
                    this@ZakatCalculatorActivity, WebViewActivity::class.java
                ).putExtra(AppConstants.BUNDLE, "gold")
            )
        }
        binding.btnSilverRate.setOnClickListener {
            startActivity(
                Intent(
                    this@ZakatCalculatorActivity, WebViewActivity::class.java
                ).putExtra(AppConstants.BUNDLE, "silver")
            )
        }
//Nisab threshold
        binding.etValueOfGold.addTextWatcher {
            nisabGold = it
            calculateZakat()
        }
        binding.etSilverRate.addTextWatcher {
            nisabSilver = it
            calculateZakat()
        }

//Cash
        binding.cashInHand.addTextWatcher {
            cashInHand = it
            calculateZakat()
        }
        binding.cashForFuturePurpose.addTextWatcher {
            cashForFuturePurpose = it
            calculateZakat()
        }
        binding.cashInLoans.addTextWatcher {
            cashInLoans = it
            calculateZakat()
        }
        binding.investments.addTextWatcher {
            investments = it
            calculateZakat()
        }
//Liabilities
        binding.borrowedMoney.addTextWatcher {
            borrowedMoney = it
            calculateZakat()
        }
        binding.wagesDue.addTextWatcher {
            wagesDue = it
            calculateZakat()
        }
        binding.billsDue.addTextWatcher {
            billsDue = it
            calculateZakat()
        }
//Gold and Silver
        binding.goldValue.addTextWatcher {
            goldValue = it
            calculateZakat()
        }
        binding.silverValue.addTextWatcher {
            silverValue = it
            calculateZakat()
        }
//Trade Goods
        binding.stockValue.addTextWatcher {
            stockValue = it
            calculateZakat()
        }

    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = getString(R.string.zakat_calculator)


        show(binding.appbar.ivRight)
        binding.appbar.ivRight.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.info))


    }


    private fun calculateZakat() {
        // Calculate total assets
        when (binding.radiogroup.checkedRadioButtonId) {
            R.id.rbValueOfGold -> {
                if (nisabGold == 0.0) {
                    return
                }
            }

            R.id.rbValueOfSilver -> {
                if (nisabSilver == 0.0) {
                    return
                }
            }

            else -> {}
        }

        var zakat = 0.0
        val totalAssets =
            cashInHand + cashForFuturePurpose + cashInLoans + investments + goldValue + silverValue + stockValue

        // Calculate total liabilities
        val totalLiabilities = borrowedMoney + wagesDue + billsDue

        // Calculate total net worth
        val totalNetWorth = totalAssets - totalLiabilities
        binding.tvNetWorthRs.text = "USD "+ String.format("%.2f", totalNetWorth)

        // Check if total net worth exceeds Nisab threshold
        if (totalNetWorth < nisabGold || totalNetWorth < nisabSilver) {
            zakat = 0.0 // Zakat not applicable
            binding.tvZakatPayableRs.text ="USD "+ zakat.toString()
        } else {
            // Calculate Zakat payable (2.5% of total net worth)
            binding.tvZakatPayableRs.text ="USD "+ String.format("%.2f", totalNetWorth * 0.025)
        }
    }
}