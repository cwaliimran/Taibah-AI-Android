package com.taibahai.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.Adapter100Scholars
import com.taibahai.databinding.ActivityActivity100ScholarsBinding
import com.taibahai.models.Model100Scholars
import com.taibahai.models.ModelSettings

class Activity100Scholars : BaseActivity() {
    lateinit var binding:ActivityActivity100ScholarsBinding
    lateinit  var adapter:Adapter100Scholars
    val scholarList=ArrayList<Model100Scholars>()


    override fun onCreate() {
        binding= ActivityActivity100ScholarsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        scholarList.clear()
        adapter= Adapter100Scholars(scholarList)
        scholarList.add(Model100Scholars(R.drawable.profileicon, "Imam Al-Ghazali","(1058–1111)"))
        scholarList.add(Model100Scholars(R.drawable.profileicon, "Sheikh Ibn Taymiyyah","(1263–1328)"))
        scholarList.add(Model100Scholars(R.drawable.profileicon, "Imam Ibn Kathir","(1301–1373)"))
        scholarList.add(Model100Scholars(R.drawable.profileicon, "Sheikh Yusuf al-Qaradawi","(1926–)"))
        scholarList.add(Model100Scholars(R.drawable.profileicon, "Imam Malik ibn Anas","(711–795)"))
        adapter.setDate(scholarList)
        binding.rv100Scholars.adapter=adapter

    }


}