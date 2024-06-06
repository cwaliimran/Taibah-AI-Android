package com.network.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.network.models.ModelUser
import com.network.utils.AppClass
import com.network.utils.AppConstants
import org.checkerframework.checker.units.qual.A

abstract class BaseFragment : Fragment() {

    private val TAG = "BaseFragment"
    var fcmToken = ""
    private var mLastClickTime: Long = 0
    var bundle: Bundle? = null
    val gson = Gson()
    var lastClickTime: Long = 0
    var currentUser: ModelUser.Data? = ModelUser.Data()
    var isAdsFree = false
    protected val mContext: Context by lazy {
        requireActivity()
    }

    var isSilverPurchased = false
    var isGoldPurchased = false
    var isDiamondPurchased = false

    protected val fragmentActivity: FragmentActivity by lazy {
        requireActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentUser = AppClass.getCurrentUser()
        isAdsFree = AppClass.sharedPref.getBoolean(AppConstants.IS_ADS_FREE)
        isSilverPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED)
        isGoldPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED)
        isDiamondPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated()
        initAdapter()
        initObservers()
        clicks()
        apiAndArgs()

    }

    abstract fun viewCreated()
    abstract fun clicks()
    open fun initAdapter() {}
    open fun initObservers() {}
    open fun apiAndArgs() {}

    protected fun show(view: View) {
        view.visibility = VISIBLE
    }

    protected fun hideGone(view: View) {
        view.visibility = GONE
    }

    protected fun hideInvisible(view: View) {
        view.visibility = INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        currentUser = AppClass.getCurrentUser()
        isAdsFree = AppClass.sharedPref.getBoolean(AppConstants.IS_ADS_FREE)
        isSilverPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_SILVER_PURCHASED)
        isGoldPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_GOLD_PURCHASED)
        isDiamondPurchased = AppClass.sharedPref.getBoolean(AppConstants.IS_TAIBAH_AI_DIAMOND_PURCHASED)
    }
}