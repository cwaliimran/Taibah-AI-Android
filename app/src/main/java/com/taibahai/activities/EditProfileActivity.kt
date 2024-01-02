package com.taibahai.activities

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.network.base.BaseActivity
import com.network.models.ModelUser
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.databinding.ActivityEditProfileBinding
import com.taibahai.utils.getPicker
import com.taibahai.utils.showToast

class EditProfileActivity : BaseActivity() {
    lateinit var binding:ActivityEditProfileBinding
    val viewModel : MainViewModelAI by viewModels()
    var image=""
    var name=""


    override fun onCreate() {
        binding=ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun clicks() {
        binding.ivUpload.setOnClickListener {
            getPicker().createIntent {
                displayLoading()
                startForImageResult.launch(it)
            }
        }

        binding.btnUpdate.setOnClickListener {
            name=binding.etName.text.toString()
            if(!name.isNullOrEmpty())
            {
                viewModel.postProfile(name,image)
            }
        }

        binding.appbar.ivLeft.setOnClickListener {
            onBackPressed()
        }
    }


    override fun initObservers() {
        super.initObservers()
        viewModel.uploadFileLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    it.data?.message?.let { it1 -> showToast(it1) }
                    Glide.with(this).load(it.data?.data?.url).into(binding.ivProfile)
                    image= it.data?.data?.file.toString()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

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
                    it.data?.message?.let { it1 -> showToast(it1) }
                    val profileData: ModelUser.Data = it.data!!.data

                    updateUI(profileData)





                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }

    }

    private fun updateUI(profileData: ModelUser.Data) {

        binding.etName.setText(profileData.name)
        binding.tvEmail.text = profileData.email
        Glide.with(this).load(profileData.image).into(binding.ivProfile)
    }



    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    val path = uri?.path
                    // TODO: downloaded files folder path not working
                    // val path = uri?.let { getFilePathFormUri(it, this) }
                    Log.d("TAGRESULT", "onActivityResult: $path")
                    if (path!=null){
                        viewModel.uploadFile(path)
                    }else{
                        displayLoading(false)
                    }
                }
                ImagePicker.RESULT_ERROR -> {
                    showToast(ImagePicker.getError(data))
                    displayLoading(false)
                }
                else -> {
                    displayLoading(false)
                }
            }
        }

    override fun initData() {
        super.initData()
        binding.appbar.tvTitle.setText("Edit Profile")
        binding.appbar.ivLeft.setImageDrawable(resources.getDrawable(R.drawable.arrow_back_24))
        binding.appbar.ivRight.setVisibility(View.GONE)
    }

    override fun apiAndArgs() {
        super.apiAndArgs()
    }



}