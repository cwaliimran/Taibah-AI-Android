package com.taibahai.activities

import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import com.bumptech.glide.Glide.init
import com.network.base.BaseActivity
import com.network.models.ModelBooks
import com.rajat.pdfviewer.PdfEngine
import com.rajat.pdfviewer.PdfQuality
import com.rajat.pdfviewer.PdfViewerActivity.Companion.FILE_TITLE
import com.taibahai.R
import com.taibahai.databinding.ActivityBookPdfdetailBinding
import com.taibahai.databinding.CustomDialogBinding

class BookPDFDetailActivity : BaseActivity() {
    lateinit var binding:ActivityBookPdfdetailBinding
    private var fileUrl: String? = null
    var bookTitle=""

    companion object {
        const val FILE_URL = "pdf_file_url"
        const val FILE_TITLE = "pdf_file_title"
        var engine = PdfEngine.INTERNAL

    }


    override fun onCreate() {
        binding=ActivityBookPdfdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
       /* intent.extras?.getString(
            FILE_TITLE, "PDF"
        )*/

        engine = PdfEngine.INTERNAL


    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText(bookTitle)
        
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


    private fun loadFileFromNetwork(fileUrl: String?) {
        initPdfViewer(
            fileUrl, engine
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
        binding.progressBar.visibility = if (this) View.VISIBLE else GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.pdfView.closePdfRender()
    }


    private fun pdfInfoDialog() {
        val dialogBinding = CustomDialogBinding.inflate(layoutInflater)
        val dialogView = dialogBinding.root

        dialogBinding.dialogTitle.text = "Document Info."
        dialogBinding.dialogSubtitle.text =
            "This PDF is sourced from the internet and is not the property or creation of Skillzy. All rights belong to their respective owners. Skillzy is designed to compile and organize free resources in a single location."

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
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
                fileUrl = model.attachments.firstOrNull()?.file

                if (checkInternetConnection(this)) {
                    loadFileFromNetwork(this.fileUrl)
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