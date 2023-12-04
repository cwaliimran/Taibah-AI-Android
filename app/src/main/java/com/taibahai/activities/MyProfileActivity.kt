package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.taibahai.R
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.ActivityMyProfileBinding
import com.taibahai.models.ModelHome

class MyProfileActivity : BaseActivity(), OnItemClick {
    lateinit var binding:ActivityMyProfileBinding
    lateinit var adapter: AdapterHome
    val showList = ArrayList<ModelHome>()


    override fun onCreate() {
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        showList.clear()
        adapter = AdapterHome(this, showList)
        showList.add(
            ModelHome(R.drawable.hassan,"Hassan Ali", "12 minutes ago",
                "Discover the spiritual depths and wisdom that illuminate your path with insights on Islamic teachings and practices.",R.drawable.rectangle_92,) )

        adapter.setDate(showList)
        binding.rvProfile.adapter=adapter


    }


}