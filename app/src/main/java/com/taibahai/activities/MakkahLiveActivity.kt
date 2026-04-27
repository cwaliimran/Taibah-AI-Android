package com.taibahai.activities

import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.network.base.BaseActivity
import com.taibahai.databinding.ActivityMakkahLiveBinding

class MakkahLiveActivity : BaseActivity() {

    private lateinit var binding: ActivityMakkahLiveBinding

    private val CHANNEL_ID = "UCos52azQNBgW63_9uDJoPDA"
    override fun onCreate() {
        binding = ActivityMakkahLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWebView()
        loadLive()
    }

    private fun setupWebView() {
        val webView = binding.webView

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    private fun loadLive() {
        val url = "https://www.youtube.com/channel/$CHANNEL_ID/live"
        binding.webView.loadUrl(url)
    }

    override fun clicks() {
        binding.ivBackArrow.setOnClickListener {
            finish()
        }
    }
}