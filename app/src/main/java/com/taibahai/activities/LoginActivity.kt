package com.taibahai.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.network.base.BaseActivity
import com.network.network.NetworkResult
import com.network.network.NetworkUtils
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.network.utils.ProgressLoading.displayLoading
import com.network.viewmodels.MainViewModelAI
import com.taibahai.R
import com.taibahai.bottom_navigation.BottomNavigation
import com.taibahai.databinding.ActivityLoginBinding
import com.taibahai.utils.Constants
import com.taibahai.utils.showToast
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.Arrays
import java.util.concurrent.Executors


class LoginActivity : BaseActivity() {
    lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    val viewModel: MainViewModelAI by viewModels()
    var image = ""
    var savedImagePath = ""
    var filename = ""
    val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 300
    var fos: OutputStream? = null
    var mImage: Bitmap? = null
    lateinit var user: FirebaseUser
    var name = ""
    var socialType = ""
    private val callbackManager = CallbackManager.Factory.create()
    var deviceID = "123"


    override fun onCreate() {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(application)
        deviceId()
        isUserAuthenticated()
    }

    private fun isUserAuthenticated() {
        val currentUser = AppClass.getCurrentUser()

        if (currentUser != null) {
            val intent = Intent(this, BottomNavigation::class.java)
            startActivity(intent)
            finish()
        } else {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)

        }
    }

    override fun clicks() {
        binding.clGooglebtn.setOnClickListener {
            if (deviceID == "") {
                deviceId()
            }
            // Declaring and initializing an Executor and a Handler
            signInWithGoogle()
        }

        binding.clFacebookBtn.setOnClickListener {
             signInWithFacebook()
        }
        binding.clGuestBtn.setOnClickListener {
            viewModel.socialLogin(
                "abc",
                "guest",
                deviceID,
                "android",
                "guest@taibahai.com",
                NetworkUtils.timeZone(),
                "Guest",
                "noimg.png"
            )
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
                    image = it.data?.data?.file.toString()
                    signUp()
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
                    showToast(it.data?.message.toString())
                    AppClass.sharedPref.storeObject(AppConstants.CURRENT_USER, it.data?.data)
                    AppClass.sharedPref.storeString(
                        AppConstants.ACCESS_TOKEN, it.data?.data?.accesstoken
                    )
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
        socialType = "google"
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    user = auth.currentUser!!
                    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
                    name = googleSignInAccount?.displayName.toString()
                    val profileImageUri = googleSignInAccount?.photoUrl


                    val myExecutor = Executors.newSingleThreadExecutor()
                    val myHandler = Handler(Looper.getMainLooper())
                    if (profileImageUri != null) {
                        myExecutor.execute {
                            mImage = mLoad(profileImageUri.toString())
                            myHandler.post {
                                //binding.imageView3.setImageBitmap(mImage)
                                if (mImage != null) {
                                    mSaveMediaToStorage(mImage)
                                }
                            }
                        }
                    } else {
                        signUp()
                    }


                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signUp() {
        viewModel.socialLogin(
            user.uid ?: "",
            socialType,
            deviceID,
            "android",
            user.email ?: "",
            NetworkUtils.timeZone(),
            name ?: "",
            image
        )

    }

    // Function to establish connection and load image
    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    // Function to save image on the device.
    // Refer: https://www.geeksforgeeks.org/circular-crop-an-image-and-save-it-to-the-file-in-android/
    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        filename = "${System.currentTimeMillis()}.jpg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
                // Obtain the file path using DocumentsContract
                imageUri?.let { uri ->
                    savedImagePath = getFilePathFromUri(uri).toString()
                }

            }
        } else {
            //working with permissions
            /* val imagesDir =
                 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
             val image = File(imagesDir, filename)

             savedImagePath = image.path

             fos = FileOutputStream(image)*/


            // Code for Android versions below 10 (Legacy storage model)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                    )
                } else {
                    // Permission already granted, proceed with the code to save the file
                    saveFileLegacy(filename)
                }
            } else {
                // No runtime permissions needed for versions below 6.0, proceed with the code to save the file
                saveFileLegacy(filename)
            }

        }
        fos?.let { uploadFile(it) }

    }

    private fun uploadFile(fos: OutputStream) {
        fos?.use {
//            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            // Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()

            viewModel.uploadFile(savedImagePath)
        }
    }


    // Function to get the file path from Uri using DocumentsContract
    private fun getFilePathFromUri(uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val filePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return filePath
    }


    // Function to save file in the legacy storage model
    private fun saveFileLegacy(filename: String) {
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)

        savedImagePath = image.path

        fos = FileOutputStream(image)
        fos.use {
            viewModel.uploadFile(savedImagePath)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with the code to save the file in the legacy storage model
                    saveFileLegacy(filename)
                } else {
                    // Permission denied, handle accordingly (e.g., show a message to the user)
                    signUp()
                }
            }
        }
    }


    private fun signInWithFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    // Check if 'email' permission is granted
                    if (result.recentlyGrantedPermissions.contains("email")) {
                        // Continue with the login process
                        handleFacebookAccessToken(result.accessToken)
                    } else {
                        // Handle the case where 'email' permission is not granted
                        Toast.makeText(
                            this@LoginActivity, "Email permission not granted", Toast.LENGTH_SHORT
                        ).show()
                    }
                }


                override fun onCancel() {
                    // User canceled the login process
                    Toast.makeText(
                        this@LoginActivity,
                        "Facebook login canceled",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, "Facebook login error", Toast.LENGTH_SHORT)
                        .show()
                }


            })
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Facebook authentication successful
                    val user = auth.currentUser
                    name = user?.displayName ?: ""
                    val profileImageUri = user?.photoUrl


                    val myExecutor = Executors.newSingleThreadExecutor()
                    val myHandler = Handler(Looper.getMainLooper())
                    if (profileImageUri != null) {
                        myExecutor.execute {
                            mImage = mLoad(profileImageUri.toString())
                            myHandler.post {
                                //binding.imageView3.setImageBitmap(mImage)
                                if (mImage != null) {
                                    mSaveMediaToStorage(mImage)
                                }
                            }
                        }
                    } else {
                        signUp()
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Facebook authentication failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }


    private fun deviceId() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("EEE", "" + task.exception)
                return@OnCompleteListener
            }
            deviceID = task.result
            AppClass.sharedPref?.storeString(Constants.DEVICE_ID, deviceID)
        })
    }


}