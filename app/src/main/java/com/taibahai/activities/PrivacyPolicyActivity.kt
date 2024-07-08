package com.taibahai.activities

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.databinding.ActivityPrivacyPolicyBinding
import com.taibahai.utils.showToast

class PrivacyPolicyActivity : BaseActivity() {
    lateinit var binding: ActivityPrivacyPolicyBinding
    val viewModel: MainViewModelAI by viewModels()
    var data = ""


    override fun onCreate() {
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
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
                    data = it.data?.data.toString()
                    if (!data.isNullOrEmpty()) {
                        val spannedText: Spanned =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Html.fromHtml(data, Html.FROM_HTML_MODE_COMPACT)
                            } else {
                                @Suppress("DEPRECATION")
                                Html.fromHtml(data)
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
        bundle = intent.extras
        if (bundle != null) {
            val type = bundle?.getString("type")
            when (type) {
                "privacy" -> {
                    binding.appbar.tvTitle.text = "Privacy Policy"
                    viewModel.privacy()

                }

                "terms" -> {
                    binding.appbar.tvTitle.text = "Terms and Conditions"
                    viewModel.terms()
                }

                "about" -> {
                    binding.appbar.tvTitle.text = "About Us"
                    viewModel.about()
                }

                else -> {}
            }
        }
    }

    override fun initData() {
        super.initData()


    }

}