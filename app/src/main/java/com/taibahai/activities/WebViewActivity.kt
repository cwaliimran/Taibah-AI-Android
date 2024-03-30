package com.taibahai.activities

import android.webkit.WebViewClient
import com.network.base.BaseActivity
import com.network.utils.AppConstants
import com.taibahai.databinding.ActivityWebViewBinding

class WebViewActivity : BaseActivity() {
    private lateinit var binding: ActivityWebViewBinding
    private val TAG = ActivityWebViewBinding::class.java.simpleName


    override fun onCreate() {
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            finish()
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        var type = bundle?.getString(AppConstants.BUNDLE)
        if (type == "gold") {
            binding.appbar.tvTitle.text = "Gold Rate"

            binding.webView.loadUrl("https://www.google.com/search?q=today+gold+rate+in+usd")

        } else if (type == "silver") {
            binding.appbar.tvTitle.text = "Silver Rate"
            binding.webView.loadUrl("https://www.google.com/search?q=silver+rate+in+usd")
        }
        // Enable JavaScript (optional)
        binding.webView.settings.javaScriptEnabled = true

        binding.webView.webViewClient = WebViewClient()
    }
}