package com.taibahai.activities

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.databinding.ActivityPrivacyPolicyBinding
import com.taibahai.utils.showToast

class PrivacyPolicyActivity : BaseActivity() {
    lateinit var binding:ActivityPrivacyPolicyBinding
    val viewModel : MainViewModelAI by viewModels()
    var textPrivacy=""



    override fun onCreate() {
        binding=ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.aboutILPrivacyTermLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    textPrivacy= it.data?.data.toString()
                    if (!textPrivacy.isNullOrEmpty()) {
                        val spannedText: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(textPrivacy, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            @Suppress("DEPRECATION")
                            Html.fromHtml(textPrivacy)
                        }

                        binding.tvPrivacyPolicy.text = spannedText
                    }
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        viewModel.privacy()
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Privacy Policy")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }

}