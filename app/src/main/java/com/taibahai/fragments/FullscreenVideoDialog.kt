package com.taibahai.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.taibahai.R

class FullscreenVideoDialog : DialogFragment() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private var videoUrl: String? = null

    companion object {
        fun newInstance(url: String): FullscreenVideoDialog {
            val fragment = FullscreenVideoDialog()
            val args = Bundle()
            args.putString("video_url", url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoUrl = arguments?.getString("video_url")
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fullscreen_video, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        playerView = view.findViewById(R.id.player_view)
        val btnClose = view.findViewById<ImageButton>(R.id.btnClose)
        btnClose.setOnClickListener { dismiss() }

        initializePlayer()
    }

    private fun initializePlayer() {
        videoUrl?.let {
            player = ExoPlayer.Builder(requireContext()).build()
            playerView.player = player
            val mediaItem = MediaItem.fromUri(it)
            player?.apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }
}
