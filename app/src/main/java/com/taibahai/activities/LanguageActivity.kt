package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterLanguage
import com.taibahai.databinding.ActivityLanguageBinding
import com.taibahai.models.ModelLanguages

class LanguageActivity : BaseActivity() {
    lateinit var binding:ActivityLanguageBinding

    override fun onCreate() {
        binding=ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {

    }

    override fun initAdapter() {

        val showLanguage=ArrayList<ModelLanguages>()
        showLanguage.add(ModelLanguages(R.drawable.flag_arab, "Arabic"))
        showLanguage.add(ModelLanguages(R.drawable.flag2, "Bengali"))
        showLanguage.add(ModelLanguages(R.drawable.flag3, "English"))
        showLanguage.add(ModelLanguages(R.drawable.flag4, "French"))
        showLanguage.add(ModelLanguages(R.drawable.flag5, "Indonesion"))
        showLanguage.add(ModelLanguages(R.drawable.flag6, "Hindi"))
        showLanguage.add(ModelLanguages(R.drawable.flag7, "Urdu"))

        val adapter=AdapterLanguage()
        adapter.updateLanguage(showLanguage)
        binding.rvLanguages.adapter=adapter






    }
}