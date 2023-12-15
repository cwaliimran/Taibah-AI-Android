package com.taibahai.activities

import android.content.Intent
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.network.base.BaseActivity
import com.network.interfaces.OnItemClick
import com.network.models.ModelUser
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.adapters.AdapterHome
import com.taibahai.databinding.ActivityMyProfileBinding
import com.taibahai.utils.showToast

class MyProfileActivity : BaseActivity(),OnItemClick {
    lateinit var binding:ActivityMyProfileBinding
    lateinit var adapter: AdapterHome
    private var profileFeedList: MutableList<com.network.models.ModelHome.Data> = mutableListOf()
    val viewModel : MainViewModelAI by viewModels()



    override fun onCreate() {
        binding=ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }

        binding.appbar.ivRight.setOnClickListener {
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
                    profileFeedList.addAll(profileData.feed)
                    updateUI(profileData)
                    adapter.notifyDataSetChanged()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    override fun initAdapter() {
        adapter = AdapterHome(this,profileFeedList)
        binding.rvProfile.adapter = adapter
    }

    override fun apiAndArgs() {

        viewModel.profile()

    }

    private fun updateUI(profileData: ModelUser.Data) {

        binding.tvName.text = profileData.name
        binding.tvEmail.text = profileData.email
        Glide.with(this).load(profileData.image).into(binding.ivProfileImage)
    }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("My profile")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setImageDrawable(resources.getDrawable(R.drawable.pen_new_square))
    }
}