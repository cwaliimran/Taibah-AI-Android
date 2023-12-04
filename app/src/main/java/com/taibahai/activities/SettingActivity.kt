package com.taibahai.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.adapters.AdapterMore
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.adapters.AdapterSettings
import com.taibahai.databinding.ActivitySettingBinding
import com.taibahai.models.ModelQuranChapter
import com.taibahai.models.ModelSettings

class SettingActivity : BaseActivity() {
    lateinit var binding: ActivitySettingBinding
    val showList = ArrayList<ModelSettings>()


    override fun onCreate() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        showList.add(ModelSettings(R.drawable.profileicon, "My Profile"))
        showList.add(ModelSettings(R.drawable.notification, "Notifications"))
        showList.add(ModelSettings(R.drawable.aivoice, "AI Voice Feedback"))
        showList.add(ModelSettings(R.drawable.upcoming, "Upcoming Features"))
        showList.add(ModelSettings(R.drawable.share, "Share"))
        showList.add(ModelSettings(R.drawable.rate, "Rate App"))
        showList.add(ModelSettings(R.drawable.aboutus, "About Us"))
        showList.add(ModelSettings(R.drawable.pp, "Privacy Policy"))
        showList.add(ModelSettings(R.drawable.cs, "Contact Support"))
        showList.add(ModelSettings(R.drawable.logout, "Logout"))
        val adapter = AdapterSettings(showList) { position ->
            when (position) {
                0 -> {
                    startActivity(Intent(this, MyProfileActivity::class.java))
                }
                1 -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                }
                2 -> {
                    startActivity(Intent(this, AIVoiceFeedbackActivity::class.java))
                }
                3 -> {
                    startActivity(Intent(this, UpcomingFeaturesActivity::class.java))
                }
                4 -> {
                    startActivity(Intent(this, ShareActivity::class.java))
                }
                5 -> {
                    startActivity(Intent(this, RateAppActivity::class.java))
                }
                6 -> {
                    startActivity(Intent(this, AboutUsActivity::class.java))
                }
                7 -> {
                    startActivity(Intent(this, PrivacyPolicyActivity::class.java))
                }
                8 -> {
                    startActivity(Intent(this, ContactSupportActivity::class.java))
                }
            }


        }
        adapter.setDate(showList)
        binding.rvSettings.adapter = adapter

    }
}