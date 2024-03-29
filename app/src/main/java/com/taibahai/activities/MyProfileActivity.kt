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
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelUser
import com.network.network.NetworkResult
import com.network.utils.AppClass
import com.network.utils.AppClass.Companion.isGuest
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.ActivityMyProfileBinding
import com.taibahai.databinding.DialogHistoryBinding
import com.taibahai.utils.Constants
import com.taibahai.utils.genericDialog
import com.taibahai.utils.showToast

class MyProfileActivity : BaseActivity() {
    lateinit var binding: ActivityMyProfileBinding
    lateinit var adapter: AdapterHome
    private var mData: MutableList<com.network.models.ModelHome.Data> = mutableListOf()
    val viewModel: MainViewModelAI by viewModels()
    var currentItemAction = -1


    override fun onCreate() {
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        binding.appbar.tvTitle.text = getString(R.string.my_profile)

        show(binding.appbar.ivRight)
        binding.appbar.ivRight.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.pen_new_square
            )
        )
        loadAd()
        setContentView(binding.root)


    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.appbar.ivRight.setOnClickListener {

            if (isGuest()) {
                handleGuestLogic()
                return@setOnClickListener
            }
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.socialLoginLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    showToast(it.data?.message.toString())
                    val profileData: ModelUser.Data = it.data!!.data

                    binding.tvName.text = profileData.name
                    binding.tvEmail.text = profileData.email
                    Glide.with(this).load(profileData.image).placeholder(R.drawable.splashlogo)
                        .into(binding.ivProfileImage)

                    mData.addAll(profileData.feed)
                    if (mData.isEmpty()) {
                        binding.noData.title.text = getString(R.string.no_posts_found)
                        show(binding.noData.root)
                    } else {
                        hideGone(binding.noData.root)
                        adapter.notifyDataSetChanged()
                    }

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

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
                    mData[currentItemAction].is_like = !mData[currentItemAction].is_like
                    if (mData[currentItemAction].is_like) {
                        mData[currentItemAction].likes += 1
                    } else {
                        mData[currentItemAction].likes -= 1
                    }
                    adapter.notifyItemChanged(currentItemAction)

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
        viewModel.deleteFeedLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    mData.removeAt(currentItemAction)
                    adapter.notifyItemRemoved(currentItemAction)
                    if (mData.isEmpty()){
                        show(binding.noData.root)
                        binding.noData.title.text = getString(R.string.no_posts_found)
                    }

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = AdapterHome(mData, isProfileFeed = true, object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {

                if (isGuest()) {
                    handleGuestLogic()
                    return
                }
                currentItemAction = position
                when (type) {
                    "like" -> {
                        viewModel.putLike(mData[position].feed_id)
                    }

                    "comment" -> {
                        val intent = Intent(this@MyProfileActivity, HomeDetailActivity::class.java)
                        intent.putExtra(AppConstants.BUNDLE, mData[position])
                        startActivity(intent)
                    }

                    "delete" -> {
                        //delete post
                        confirmDelete(mData[position].feed_id)
                    }

                    else -> {}
                }

            }
        }) { data, menuItem ->
            false
        }
        binding.rvProfile.adapter = adapter
    }


    private fun loadAd() {
        //load ad
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
        viewModel.profile()

    }


    public override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }

    val handleGuestLogic: () -> Unit = {
        genericDialog(object : OnItemClick {
            override fun onClick(position: Int, type: String?, data: Any?, view: View?) {
                super.onClick(position, type, data, view)
                val aiTokens = AppClass.sharedPref.getInt(AppConstants.AI_TOKENS)
                AppClass.sharedPref.clearAllPreferences()
                AppClass.sharedPref.storeInt(AppConstants.AI_TOKENS, aiTokens)
                AppClass.sharedPref.storeBoolean(AppConstants.IS_FREE_AI_TOKENS_PROVIDED, true)
                viewModel.logout(
                    AppClass.sharedPref.getString(Constants.DEVICE_ID, "").toString(),
                    "android"
                )
            }
        })
    }


    private fun confirmDelete(id: String) {
        val dialog = Dialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val binding = DialogHistoryBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnYes.setOnClickListener {
            viewModel.deleteFeed(id)
            dialog.dismiss()
        }

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        dialog.show()


    }


}