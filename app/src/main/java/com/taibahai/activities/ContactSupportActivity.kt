package com.taibahai.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.databinding.ActivityContactSupportBinding
import com.taibahai.utils.showToast

class ContactSupportActivity : BaseActivity() {
    lateinit var binding:ActivityContactSupportBinding
    val viewModel : MainViewModel by viewModels()
    var title=""
    var message=""



    override fun onCreate() {
        binding=ActivityContactSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSend.setOnClickListener {
            if(checkField())
            {
                viewModel.support(subject = title, message=message)
            }
        }
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
                    it.data?.message?.let { it1 -> showToast(it1) }
                    binding.etTitle.text.clear()
                    binding.etMessage.text.clear()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    private fun checkField():Boolean
    {
        title=binding.etTitle.text.toString()
        message=binding.etMessage.text.toString()

        if(title.isEmpty())
        {
            binding.etTitle.error="Title is required"
            return false
        }

        if(message.isEmpty())
        {
            binding.etMessage.error="Enter your message"
            return false

        }


        return true
    }
}