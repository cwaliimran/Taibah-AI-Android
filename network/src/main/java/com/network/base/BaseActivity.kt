package com.network.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.network.models.ModelUser
import com.network.utils.AppClass
import com.network.utils.AppConstants

abstract class BaseActivity : AppCompatActivity() {

    var bundle: Bundle? = null
    val gson = Gson()
    lateinit var context: Context
    var currentUser: ModelUser.Data? = ModelUser.Data()
    var isAdsFree = false
    var isDiamondPurchased = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        isAdsFree = AppClass.sharedPref.getBoolean(AppConstants.IS_ADS_FREE)
        isDiamondPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED)
        currentUser = AppClass.getCurrentUser()
        context = this
        bundle = intent.extras

        onCreate()
        initAdapter()
        initData()
        initObservers()
        clicks()
        apiAndArgs()
    }

    fun applySystemInsets(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    abstract fun onCreate()
    open fun initData() {}
    open fun initAdapter() {}
    open fun initObservers() {}
    abstract fun clicks()
    open fun apiAndArgs() {}

    fun hide(view: View) {
        view.visibility = View.INVISIBLE
    }

    fun hideGone(view: View) {
        view.visibility = View.GONE
    }

    fun show(view: View) {
        view.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onResume() {
        super.onResume()
        isAdsFree = AppClass.sharedPref.getBoolean(AppConstants.IS_ADS_FREE)
        currentUser = AppClass.getCurrentUser()
        isDiamondPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED)
    }
}