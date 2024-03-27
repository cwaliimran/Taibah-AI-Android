package com.taibahai.quran

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.network.utils.AppClass
import com.taibahai.R
import com.taibahai.databinding.ActivityReaderSettingsBinding

class ReaderSettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivityReaderSettingsBinding
    var isUpdated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReaderSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initClicks()
    }

    private fun initViews() {
        binding.appbar.tvTitle.text = getString(R.string.fonts)
        val textSize = AppClass.sharedPref.getInt(StringUtils.FONT_SIZE)
        if (textSize != 0) {
            binding.seekBarFontSize.progress = textSize
            binding.ayatArabicText.textSize = textSize.toFloat()
        }
        val scrollSpeed = AppClass.sharedPref.getInt(StringUtils.SCROLL_SPEED)
        if (scrollSpeed != 0) {
            binding.seekBarScrollSpeed.progress = scrollSpeed
        }
    }

    private fun initClicks() {
        binding.appbar.ivLeft.setOnClickListener { v -> onBackPressed() }

        binding.seekBarFontSize.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Handle progress change
                isUpdated = true
                binding.ayatArabicText.textSize = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts touching the seek bar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops touching the seek bar
                seekBar?.progress?.let { AppClass.sharedPref.storeInt(StringUtils.FONT_SIZE, it) }
            }
        })

        binding.seekBarScrollSpeed.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Called when the user starts touching the seek bar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Called when the user stops touching the seek bar
                seekBar?.progress?.let {
                    AppClass.sharedPref.storeInt(
                        StringUtils.SCROLL_SPEED,
                        it
                    )
                }
            }
        })


    }

}