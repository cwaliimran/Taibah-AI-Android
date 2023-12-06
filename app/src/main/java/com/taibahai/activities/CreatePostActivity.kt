package com.taibahai.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivityCreatePostBinding
import com.taibahai.utils.getPicker
import com.taibahai.utils.showToast

class CreatePostActivity : BaseActivity() {
    private var description=""
    lateinit var binding:ActivityCreatePostBinding
    val viewModel : MainViewModel by viewModels()
    var upLoadedFile=""





    override fun onCreate() {
        binding=ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    override fun clicks() {
        binding.btnCreate.setOnClickListener {
            if(isFieldChecked())
            {
                description=binding.etDescription.text.toString()
                viewModel.postFeed(description = description, type = upLoadedFile)
            }
        }

        binding.ivPostUpload.setOnClickListener {
            getPicker().createIntent {
                displayLoading()
                startForImageResult.launch(it)
            }

        }
    }

    private fun isFieldChecked():Boolean
    {
        if(binding.etDescription.text.isEmpty())
        {
            binding.etDescription.error= "Description is required"
            return false
        }
        return true

    }

    override fun initObservers() {
        super.initObservers()
        viewModel.postFeedLiveData.observe(this) {
            if (it == null) {
                return@observe
            }
            displayLoading(false)
            when (it) {
                is NetworkResult.Loading -> {
                    displayLoading(true)
                }

                is NetworkResult.Success -> {
                    it.data?.let { it1 -> showToast(it1.message) }

                    val intent = Intent(this, BottomNavigation::class.java)
                    startActivity(intent)
                    finish()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }


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
                    Glide.with(this).load(it.data?.data?.url).into(binding.ivPostUpload)
                    upLoadedFile= it.data?.data?.file.toString()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
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

}