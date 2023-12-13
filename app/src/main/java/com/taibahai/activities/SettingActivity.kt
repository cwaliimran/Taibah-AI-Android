package com.taibahai.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.adapters.AdapterMore
import com.taibahai.adapters.AdapterQuranChapter
import com.taibahai.adapters.AdapterSettings
import com.taibahai.databinding.ActivitySettingBinding
import com.taibahai.databinding.DialogLogoutBinding
import com.taibahai.models.ModelQuranChapter
import com.taibahai.models.ModelSettings
import com.taibahai.utils.showToast

class SettingActivity : BaseActivity() {
    lateinit var binding: ActivitySettingBinding
    val showList = ArrayList<ModelSettings>()
    val viewModel : MainViewModel by viewModels()



    override fun onCreate() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Setting")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
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

                9 -> {
                    showLogoutDialog()
                }
            }


        }
        adapter.setDate(showList)
        binding.rvSettings.adapter = adapter

    }


    override fun initObservers() {
        super.initObservers()
        viewModel.simpleResponseLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }


    private fun showLogoutDialog() {
        val dialog = Dialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val binding = DialogLogoutBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnLogout.setOnClickListener {

           // currentUser?.data?.device_id?.let { it1 -> viewModel.logout(it1) }
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialog.show()
    }
}