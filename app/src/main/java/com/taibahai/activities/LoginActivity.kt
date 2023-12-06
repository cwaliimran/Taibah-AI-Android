package com.taibahai.activities

import android.content.Intent

import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModel
import com.taibahai.R
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivityLoginBinding
import com.taibahai.utils.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.network.network.NetworkUtils
import com.network.utils.AppClass
import com.network.utils.AppConstants


class LoginActivity : BaseActivity() {
    lateinit var binding:ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    val viewModel : MainViewModel by viewModels()


    override fun onCreate() {
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        isUserAuthenticated()
    }

    private fun isUserAuthenticated()
    {
        val currentUser = AppClass.getCurrentUser()

        if (currentUser != null)
        {
            val intent = Intent(this, BottomNavigation::class.java)
            startActivity(intent)
            finish()
        }
        else
        {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)

        }
    }

    override fun clicks() {
        binding.clGooglebtn.setOnClickListener {
            signInWithGoogle()


        }

        binding.clAppleBtn.setOnClickListener {
            val intent= Intent(this,BottomNavigation::class.java)
            startActivity(intent)
        }

        binding.clFacebookBtn.setOnClickListener {
            val intent= Intent(this,BottomNavigation::class.java)
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
                    AppClass.sharedPref.storeObject(AppConstants.CURRENT_USER, it.data?.data)
                    AppClass.sharedPref.storeString(AppConstants.ACCESS_TOKEN, it.data?.data?.accesstoken)
                    val intent = Intent(this, BottomNavigation::class.java)
                    startActivity(intent)
                    finish()
                }

                is NetworkResult.Error -> {
                    showToast(it.message.toString())
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {


                    val user = auth.currentUser
                    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
                    val name = googleSignInAccount?.displayName
                    val profileImageUri = googleSignInAccount?.photoUrl

                    if (user != null) {


                       viewModel.socialLogin(user.uid,"google","dummy token","android",user.email?:"",NetworkUtils.timeZone(),name?:"",profileImageUri.toString())
                    }


                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}