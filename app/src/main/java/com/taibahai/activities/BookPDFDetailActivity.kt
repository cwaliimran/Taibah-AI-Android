package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.databinding.ActivityBookPdfdetailBinding

class BookPDFDetailActivity : BaseActivity() {
    lateinit var binding:ActivityBookPdfdetailBinding


    override fun onCreate() {
        binding=ActivityBookPdfdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}