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
import com.taibahai.databinding.ActivityAboutUsBinding
import com.taibahai.utils.showToast

class AboutUsActivity : BaseActivity() {
    lateinit var binding:ActivityAboutUsBinding
    val viewModel : MainViewModelAI by viewModels()
    var textAboutUs=""



    override fun onCreate() {
        binding=ActivityAboutUsBinding.inflate(layoutInflater)
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
                    textAboutUs = it.data?.data.toString()
                    if (textAboutUs.isNotEmpty())
                    {
                        if (!textAboutUs.isNullOrEmpty()) {
                            val spannedText: Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(textAboutUs, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                @Suppress("DEPRECATION")
                                Html.fromHtml(textAboutUs)
                            }

                            binding.tvAboutUs.text = spannedText
                        }
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
        viewModel.about()
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("About Us")
        
        
    }

}