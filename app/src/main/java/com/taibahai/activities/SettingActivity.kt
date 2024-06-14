package com.taibahai.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.network.NetworkResult
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterSettings
import com.taibahai.databinding.ActivitySettingBinding
import com.taibahai.databinding.DialogLogoutBinding
import com.taibahai.models.ModelSettings
import com.taibahai.quran.ReaderSettingsActivity
import com.taibahai.utils.Constants
import com.taibahai.utils.openPlayStoreForRating
import com.taibahai.utils.shareApp

class SettingActivity : BaseActivity() {
    lateinit var binding: ActivitySettingBinding
    val showList = ArrayList<ModelSettings>()
    val viewModel: MainViewModelAI by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.text = "Setting"


    }


    override fun initAdapter() {
        super.initAdapter()
        val showList = ArrayList<ModelSettings>().apply {
            add(ModelSettings(1, R.drawable.profileicon, "My Profile"))
            add(ModelSettings(2, R.drawable.notification, "Notifications"))
            add(ModelSettings(11, R.drawable.baseline_text_fields_24, "Quran Reading Fonts"))
//            add(ModelSettings(3, R.drawable.aivoice, "AI Voice Feedback"))
            add(ModelSettings(4, R.drawable.upcoming, "Upcoming Features"))
            add(ModelSettings(5, R.drawable.share_icon, "Share"))
            add(ModelSettings(6, R.drawable.rate, "Rate App"))
            add(ModelSettings(7, R.drawable.aboutus, "About Us"))
            add(ModelSettings(8, R.drawable.pp, "Privacy Policy"))
            add(ModelSettings(9, R.drawable.cs, "Contact Support"))
            add(ModelSettings(10, R.drawable.logout, "Logout"))
        }
        val adapter = AdapterSettings(showList, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                when (data) {
                    1 -> startActivity(Intent(this@SettingActivity, MyProfileActivity::class.java))
                    2 -> startActivity(
                        Intent(
                            this@SettingActivity,
                            NotificationActivity::class.java
                        )
                    )

                    3 -> startActivity(
                        Intent(
                            this@SettingActivity,
                            AIVoiceFeedbackActivity::class.java
                        )
                    )

                    4 -> startActivity(
                        Intent(
                            this@SettingActivity,
                            UpcomingFeaturesActivity::class.java
                        )
                    )

                    5 -> context.shareApp()
                    6 -> context.openPlayStoreForRating()
                    7 -> startActivity(Intent(this@SettingActivity, AboutUsActivity::class.java))
                    8 -> startActivity(
                        Intent(
                            this@SettingActivity,
                            PrivacyPolicyActivity::class.java
                        )
                    )

                    9 -> startActivity(
                        Intent(
                            this@SettingActivity,
                            ContactSupportActivity::class.java
                        )
                    )

                    10 -> showLogoutDialog()
                    11 -> startActivity(
                        Intent(
                            this@SettingActivity,
                            ReaderSettingsActivity::class.java
                        )
                    )
                }
            }
        })

        adapter.setData(showList)
        binding.rvSettings.adapter = adapter

    }


    override fun initObservers() {
        super.initObservers()
        viewModel.logoutLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    val aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)
                    AppClass.sharedPref.clearAllPreferences()
                    AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
                    AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)
                    startActivity(
                        Intent(
                            this, LoginActivity::class.java
                        )
                    )
                    this.finishAffinity()
                }

                is NetworkResult.Error -> {
                    startActivity(
                        Intent(
                            this, LoginActivity::class.java
                        )
                    )
                    this.finishAffinity()
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
            viewModel.logout(
                AppClass.sharedPref.getString(Constants.DEVICE_ID, "").toString(),
                "android"
            )
            val aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)
            AppClass.sharedPref.clearAllPreferences()
            AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
            AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)

            googleSignInClient.signOut().addOnCompleteListener {
                dialog.dismiss() // Dismiss dialog after initiating the logout action
            }
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialog.show()

    }


}