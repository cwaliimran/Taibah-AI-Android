package com.taibahai.hadiths

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterHadithBooks
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.databinding.ActivityHadithBooks1Binding
import com.taibahai.models.ModelHadithBook
import com.taibahai.models.ModelQuranChapter

class HadithBooksActivity1 : BaseActivity() {
    lateinit var binding:ActivityHadithBooks1Binding
    val showList=ArrayList<ModelHadithBook>()
    lateinit var adapter:AdapterHadithBooks


    override fun onCreate() {
        binding=ActivityHadithBooks1Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterHadithBooks(showList)
        showList.add(ModelHadithBook(1, "Sahih al-Bukhari","Sahih al-Bukhari, 97 Chapters "))
        showList.add(ModelHadithBook(2, "Sahih Muslim","Imam Muslim ibn al-Jajjaj al-Naysabur"))
        showList.add(ModelHadithBook(3, "Sahih al-Bukhari","Sahih al-Bukhari, 97 Chapters "))
        showList.add(ModelHadithBook(4, "Sahih Muslim","Imam Muslim ibn al-Jajjaj al-Naysabur"))
        showList.add(ModelHadithBook(5, "Sahih al-Bukhari","Sahih al-Bukhari, 97 Chapters "))
        showList.add(ModelHadithBook(6, "Sahih Muslim","Imam Muslim ibn al-Jajjaj al-Naysabur"))

        adapter.setDate(showList)
        binding.rvHadithBook.adapter=adapter

    }

}