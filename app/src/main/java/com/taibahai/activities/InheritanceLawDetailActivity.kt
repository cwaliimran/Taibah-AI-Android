package com.taibahai.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.network.base.BaseActivity
import com.network.network.NetworkUtils
import com.rajat.pdfviewer.PdfEngine
import com.rajat.pdfviewer.PdfQuality
import com.taibahai.R
import com.taibahai.databinding.ActivityInheritanceLawBinding
import com.taibahai.databinding.ActivityInheritanceLawDetailBinding

class InheritanceLawDetailActivity : BaseActivity() {
    lateinit var binding: ActivityInheritanceLawDetailBinding
    private var fileUrl: String? = null


    companion object {

        var engine = PdfEngine.INTERNAL

    }

    override fun onCreate() {
        binding = ActivityInheritanceLawDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        engine = PdfEngine.INTERNAL

        val type = intent.getStringExtra("type")
        when (type) {
            "text" -> {
                val rawDescription = intent.getStringExtra("description")
                val description = Html.fromHtml(rawDescription).toString()
                binding.tvDescription.text = description
                false.showProgressBar()
            }

            "pdf" -> {
                fileUrl = intent.getStringExtra("pdfUrl")
                if (NetworkUtils.isInternetAvailable()) {
                    loadFileFromNetwork(this.fileUrl)
                } else {
                    Toast.makeText(
                        this,
                        "No Internet Connection. Please Check your internet connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
            else -> {

                finish()
            }
        }


    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }





    private fun loadFileFromNetwork(fileUrl: String?) {
        initPdfViewer(
            fileUrl, BookPDFDetailActivity.engine
        )
    }

    private fun initPdfViewer(fileUrl: String?, engine: PdfEngine) {
        if (TextUtils.isEmpty(fileUrl)) onPdfError()

        //Initiating PDf Viewer with URL
        try {
            binding.pdfView.initWithUrl(
                fileUrl!!, PdfQuality.NORMAL, engine
            )
        } catch (e: Exception) {
            onPdfError()
        }


    }

    private fun onPdfError() {
        Toast.makeText(this, "Pdf has been corrupted", Toast.LENGTH_SHORT).show()
        false.showProgressBar()
        finish()
    }

    private fun Boolean.showProgressBar() {
        binding.progressBar.visibility = if (this) View.VISIBLE else View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.pdfView.closePdfRender()
    }


    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = "Islamic Law of Inheritance"
    }
}