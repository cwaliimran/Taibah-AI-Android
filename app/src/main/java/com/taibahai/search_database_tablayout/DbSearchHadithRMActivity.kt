package com.taibahai.search_database_tablayout

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelDbSearchHadith
import com.network.viewmodels.MainViewModelAI
import com.taibahai.adapters.AdapterDBSearchHadith
import com.taibahai.databinding.ActivityDbSearchHadithRmactivityBinding

class DbSearchHadithRMActivity : BaseActivity() {
    lateinit var binding:ActivityDbSearchHadithRmactivityBinding
    var hadithNo=""
    var bookName=""
    var type=""
    var arabic=""
    var translation=""
    val viewModel: MainViewModelAI by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDbSearchHadithRmactivityBinding.inflate(layoutInflater)
        binding.appbar.tvTitle.visibility=View.GONE
        binding.appbar.ivRight.visibility=View.GONE

        binding.tvHadithNo.text = "Hadith No: ${hadithNo}"
        binding.tvBookName.text=bookName
        binding.tvHadithType.text=type
        binding.ayatArabicText.text=arabic
        binding.tvEnglishTranslation.text=translation
        setContentView(binding.root)
    }

    override fun onCreate() {
    }

    override fun clicks() {
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if(bundle!=null)
        {
            hadithNo=intent.getStringExtra("hadith_no").toString()
            bookName=intent.getStringExtra("book_name").toString()
            type=intent.getStringExtra("type").toString()
            arabic=intent.getStringExtra("arbi").toString()
            translation=intent.getStringExtra("translation").toString()

        }


    }
}