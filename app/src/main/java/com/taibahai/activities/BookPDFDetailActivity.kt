package com.taibahai.activities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import com.network.base.BaseActivity
import com.network.models.ModelBooks
import com.network.utils.ProgressLoading.displayLoading
import com.rajat.pdfviewer.PdfEngine
import com.rajat.pdfviewer.PdfQuality
import com.rajat.pdfviewer.PdfRendererView
import com.taibahai.databinding.ActivityBookPdfdetailBinding

class BookPDFDetailActivity : BaseActivity() {
    lateinit var binding: ActivityBookPdfdetailBinding
    private var fileUrl: String? = null
    var bookTitle = ""

    companion object {
        var engine = PdfEngine.INTERNAL
    }


    override fun onCreate() {
        binding = ActivityBookPdfdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        engine = PdfEngine.INTERNAL


    }

    override fun initData() {
        super.initData()

        binding.appbar.ivRight.setVisibility(GONE)
    }


    private fun checkInternetConnection(context: Context): Boolean {
        var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            result = 2
                        }

                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            result = 1
                        }

                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            result = 3
                        }
                    }
                }
            }
        } else {
            cm?.run {
                cm.activeNetworkInfo?.run {
                    when (type) {
                        ConnectivityManager.TYPE_WIFI -> {
                            result = 2
                        }

                        ConnectivityManager.TYPE_MOBILE -> {
                            result = 1
                        }

                        ConnectivityManager.TYPE_VPN -> {
                            result = 3
                        }
                    }
                }
            }
        }
        return result != 0
    }

    private fun initPdfViewer(fileUrl: String?, engine: PdfEngine) {
        if (TextUtils.isEmpty(fileUrl)) onPdfError()

        try {
            binding.pdfView.statusListener = object : PdfRendererView.StatusCallBack {
                override fun onDownloadStart() {
                    super.onDownloadStart()
                    displayLoading()
                }

                override fun onDownloadSuccess() {
                    super.onDownloadSuccess()
                    displayLoading(false)

                }

                override fun onError(error: Throwable) {
                    Log.i("statusCallBack", "onError")
                    displayLoading(false)
                }

                override fun onPageChanged(currentPage: Int, totalPage: Int) {
                    //Page change. Not require
                }
            }
            binding.pdfView.initWithUrl(
                fileUrl!!, PdfQuality.NORMAL, engine
            )
        } catch (e: Exception) {
            onPdfError()
        }


    }

    private fun onPdfError() {
        Toast.makeText(this, "Pdf has been corrupted", Toast.LENGTH_SHORT).show()
        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.pdfView.closePdfRender()
    }


    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun apiAndArgs() {

        super.apiAndArgs()
        if (bundle != null) {

            val model: ModelBooks.Data? = intent.getSerializableExtra("model") as? ModelBooks.Data
            if (model != null) {
                bookTitle = model.title
                binding.appbar.tvTitle.text = bookTitle

                fileUrl = model.attachments.firstOrNull()?.file

                if (checkInternetConnection(this)) {
                    initPdfViewer(
                        fileUrl, engine
                    )
                } else {
                    Toast.makeText(
                        this,
                        "No Internet Connection. Please Check your internet connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }
}