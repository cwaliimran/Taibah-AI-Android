package com.taibahai.utils

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView

class CustomScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : NestedScrollView(context, attrs, defStyle) {

    private companion object {
        const val DELAY_MILLIS = 100
    }

    interface OnFlingListener {
        fun onFlingStarted()
        fun onFlingStopped()
    }

    private var mFlingListener: OnFlingListener? = null
    private var mPreviousPosition: Int = 0

    private val mScrollChecker: Runnable = object : Runnable {
        override fun run() {
            val position = scrollY
            if (mPreviousPosition - position == 0) {
                mFlingListener?.onFlingStopped()
                removeCallbacks(this)
            } else {
                mPreviousPosition = scrollY
                postDelayed(this, DELAY_MILLIS.toLong())
            }
        }
    }

    override fun fling(velocityY: Int) {
        super.fling(velocityY)

        mFlingListener?.onFlingStarted()
        post(mScrollChecker)
    }

    fun getOnFlingListener(): OnFlingListener? {
        return mFlingListener
    }

    fun setOnFlingListener(onFlingListener: OnFlingListener) {
        mFlingListener = onFlingListener
    }
}