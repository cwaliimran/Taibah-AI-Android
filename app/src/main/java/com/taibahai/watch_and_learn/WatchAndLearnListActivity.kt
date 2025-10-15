package com.taibahai.watch_and_learn

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.network.base.BaseActivity
import com.taibahai.R
import com.taibahai.databinding.ActivityBooksAndPdfactivityBinding
import com.taibahai.databinding.ActivityWatchAndLearnListBinding

class WatchAndLearnListActivity  : BaseActivity() {
    lateinit var binding: ActivityWatchAndLearnListBinding

    override fun onCreate() {
        binding = ActivityWatchAndLearnListBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun clicks() {
        binding.appbar.tvTitle.text="Watch & Learn"
        binding.appbar.ivLeft.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}