package com.taibahai.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.databinding.ActivityEditProfileBinding
import com.taibahai.databinding.DialogLogoutBinding
import com.taibahai.utils.getPicker
import com.taibahai.utils.showToast

class EditProfileActivity : BaseActivity() {
    lateinit var binding: ActivityEditProfileBinding
    val viewModel: MainViewModelAI by viewModels()
    var image = ""
    var name = ""

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate() {
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        binding.appbar.tvTitle.text = "Edit Profile"

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

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
            name = binding.etName.text.toString()
            if (!name.isNullOrEmpty()) {
                viewModel.postProfile(name, image)
            }
        }

        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnDeleteAcc.setOnClickListener {
            showDelAccDialog()

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
                    image = it.data?.data?.file.toString()

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
                    AppClass.sharedPref.storeObject(AppConstants.CURRENT_USER, it.data?.data)
                    onBackPressedDispatcher.onBackPressed()

                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }


        //delete acc
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


    private val startForImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val uri = data?.data
                    val path = uri?.path
                    //  downloaded files folder path not working
                    // val path = uri?.let { getFilePathFormUri(it, this) }
                    Log.d("TAGRESULT", "onActivityResult: $path")
                    if (path != null) {
                        viewModel.uploadFile(path)
                    } else {
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
        currentUser.let {
            binding.etName.setText(it?.name)
            binding.tvEmail.text = it?.email
            Glide.with(this).load(it?.image).placeholder(R.mipmap.ic_launcher)
                .into(binding.ivProfile)
        }
    }


    private fun showDelAccDialog() {


        val dialog = Dialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val binding = DialogLogoutBinding.inflate(layoutInflater)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)

        binding.tvConfirmation.text = getString(R.string.data_lost_warn)
        binding.btnLogout.text = getString(R.string.delete_account)
        binding.imageView6.setImageResource(R.drawable.delete)
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        binding.btnLogout.setBackgroundResource(R.drawable.warn_btn_bg)

        binding.btnLogout.setOnClickListener {
            viewModel.deleteAccount()
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