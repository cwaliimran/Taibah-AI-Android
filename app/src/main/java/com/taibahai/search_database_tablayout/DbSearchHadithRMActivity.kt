package com.taibahai.search_database_tablayout

import android.view.View
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.viewmodels.MainViewModelAI
import com.taibahai.databinding.ActivityDbSearchHadithRmactivityBinding

class DbSearchHadithRMActivity : BaseActivity() {
    lateinit var binding: ActivityDbSearchHadithRmactivityBinding
    var hadithNo = ""
    var bookName = ""
    var type = ""
    var arabic = ""
    var translation = ""
    val viewModel: MainViewModelAI by viewModels()

    override fun onCreate() {
        binding = ActivityDbSearchHadithRmactivityBinding.inflate(layoutInflater)
        binding.appbar.ivRight.visibility = View.GONE

        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            finish()
        }
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
        if (bundle != null) {
            hadithNo = intent.getStringExtra("hadith_no").toString()
            bookName = intent.getStringExtra("book_name").toString()
            type = intent.getStringExtra("type").toString()
            arabic = intent.getStringExtra("arbi").toString()
            translation = intent.getStringExtra("translation").toString()

            binding.tvHadithNo.text = "Hadith No: ${hadithNo}"

            binding.tvBookName.text = bookName
            binding.tvHadithType.text = type
            binding.ayatArabicText.text = arabic
            binding.tvEnglishTranslation.text = translation

            binding.appbar.tvTitle.text = bookName


        }


    }
}