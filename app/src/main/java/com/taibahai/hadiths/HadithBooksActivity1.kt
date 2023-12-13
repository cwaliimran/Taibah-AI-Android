package com.taibahai.hadiths


import android.view.View
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterHadithBooks
import com.taibahai.databinding.ActivityHadithBooks1Binding
import com.taibahai.models.ModelHadithBook

class HadithBooksActivity1 : BaseActivity() {
    lateinit var binding:ActivityHadithBooks1Binding
    val showList=ArrayList<ModelHadithBook>()
    lateinit var adapter:AdapterHadithBooks


    override fun onCreate() {
        binding=ActivityHadithBooks1Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
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

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Hadiths")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }

}