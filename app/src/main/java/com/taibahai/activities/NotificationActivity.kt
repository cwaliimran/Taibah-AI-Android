package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterHome
import com.taibahai.adapters.AdapterNotification
import com.taibahai.databinding.ActivityNotificationBinding
import com.taibahai.databinding.ActivitySettingBinding
import com.taibahai.models.ModelNotification
import com.taibahai.models.ModelSettings

class NotificationActivity : BaseActivity() {
    lateinit var binding:ActivityNotificationBinding
    lateinit var adapter: AdapterNotification
    val showList = ArrayList<ModelNotification>()


    override fun onCreate() {
        binding=ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        adapter= AdapterNotification(showList)
        showList.add(ModelNotification(R.drawable.image_20,"Daily Alert","Recite durood 10 time","10:30 AM"))
        showList.add(ModelNotification(R.drawable.image_20,"Daily Alert","Recite durood 10 time","10:30 AM"))
        adapter.setDate(showList)
        binding.rvNotification.adapter=adapter
    }
}