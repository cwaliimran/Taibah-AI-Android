package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterBooksAndPDF
import com.taibahai.adapters.AdapterHadithBooks
import com.taibahai.databinding.ActivityBooksAndPdfactivityBinding
import com.taibahai.databinding.ActivitySettingBinding
import com.taibahai.models.ModelBooksAndPDF
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelSettings

class BooksAndPDFActivity : BaseActivity() {
    lateinit var binding:ActivityBooksAndPdfactivityBinding
    val showList = ArrayList<ModelBooksAndPDF>()
    lateinit var adapter:AdapterBooksAndPDF



    override fun onCreate() {
        binding=ActivityBooksAndPdfactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
        onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterBooksAndPDF(showList)
        showList.add(ModelBooksAndPDF(R.drawable.books, "Al oumou shafai"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Mouwata Malick"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Ousoulou Sounna Ahmad B"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Mousnad Abi Hanifa"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Mousnad Ahmad Boun Hambal"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Aqeedah"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Salah (Prayers)"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Ramadan"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Zakat (Charity)"))
        showList.add(ModelBooksAndPDF(R.drawable.books, "Hajj (Pilgrimage)"))


        adapter.setDate(showList)
        binding.rvBooksPDF.adapter=adapter

    }


}