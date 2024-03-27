/*
 * Copyright 2018 Dmitriy Ponomarenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taibahai.audioPlayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.network.utils.AppClass
import com.taibahai.audioPlayer.PlayerContract.PlayerCallback
import com.taibahai.notifications.MediaNotificationManager
import com.taibahai.quran.StringUtils
import java.io.IOException
import java.util.Objects

class AudioPlayer private constructor() : PlayerContract.Player, OnPreparedListener {
    var listener: OnViewClickListener? = null
    private val actionsListeners: MutableList<PlayerCallback> = ArrayList()
    private var mediaPlayer: MediaPlayer? = null

    //	private Timer timerProgress;
    private var isPrepared = false
    private var isPause = false
    private var seekPos: Long = 0
    private var pausePos: Long = 0
    private var dataSource: String? = null
    override fun addPlayerCallback(callback: PlayerCallback) {
        if (callback != null) {
            actionsListeners.add(callback)
        }
    }

    override fun removePlayerCallback(callback: PlayerCallback): Boolean {
        return if (callback != null) {
            actionsListeners.remove(callback)
        } else false
    }

    override fun setData(data: String) {
        if (mediaPlayer != null && dataSource != null && dataSource == data) {
            Log.d(TAG, "setData: Do nothing ")
        } else {
            dataSource = data
            restartPlayer()
        }
    }

    fun restartPlayer() {
        if (dataSource != null) {
            try {
                isPrepared = false
                stop()
                mediaPlayer = MediaPlayer()
                val uri = Uri.parse(dataSource)
                mediaPlayer!!.setDataSource(AppClass.instance, uri)
                mediaPlayer!!.setAudioAttributes(
                    AudioAttributes.Builder().setContentType(
                        AudioAttributes.CONTENT_TYPE_MUSIC
                    ).build()
                )
                //                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            } catch (e: IOException) {
                e.printStackTrace()
                if (Objects.requireNonNull(e.message)?.contains("Permission denied") == true) {
                    Log.d(TAG, "restartPlayer: Permission denied")
                } else {
                    Log.d(TAG, "restartPlayer: DataSource Exeption")
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
                if (Objects.requireNonNull(e.message)!!.contains("Permission denied")) {
                    Log.d(TAG, "restartPlayer: Permission denied")
                } else {
                    Log.d(TAG, "restartPlayer: DataSource Exeption")
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                if (Objects.requireNonNull(e.message)!!.contains("Permission denied")) {
                    Log.d(TAG, "restartPlayer: Permission denied")
                } else {
                    Log.d(TAG, "restartPlayer: DataSource Exeption")
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                if (Objects.requireNonNull(e.message)!!.contains("Permission denied")) {
                    Log.d(TAG, "restartPlayer: Permission denied")
                } else {
                    Log.d(TAG, "restartPlayer: DataSource Exeption")
                }
            }
        }
    }

    override fun playOrPause() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer!!.isPlaying) {
                    pause()
                } else {
                    isPause = false
                    if (!isPrepared) {
                        try {
                            mediaPlayer!!.setOnPreparedListener(this)
                            mediaPlayer!!.prepareAsync()
                        } catch (ex: IllegalStateException) {
                            ex.printStackTrace()
                            restartPlayer()
                            mediaPlayer!!.setOnPreparedListener(this)
                            try {
                                mediaPlayer!!.prepareAsync()
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                                restartPlayer()
                            }
                        }
                    } else {
                        mediaPlayer!!.start()
                        mediaPlayer!!.seekTo(pausePos.toInt())
                        onStartPlay()
                        mediaPlayer!!.setOnCompletionListener { mp: MediaPlayer? ->
                            stop()
                            onStopPlay()
                        }
                    }
                    pausePos = 0
                }
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.d(TAG, "Player is not initialized!")
        }
    }

    override fun onPrepared(mp: MediaPlayer) {
        if (mediaPlayer !== mp) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = mp
        }
        onPreparePlay()
        isPrepared = true
        mediaPlayer!!.start()
        mediaPlayer!!.seekTo(seekPos.toInt())
        onStartPlay()
        mediaPlayer!!.setOnCompletionListener { mp1: MediaPlayer? ->
            if (listener != null) {
                listener!!.onCompleted(mp1)
            }
            MediaNotificationManager.showMediaNotification(
                AppClass.sharedPref.getString(
                    StringUtils.SURAH_NAME,
                    ""
                )
            )
            stop()
            onStopPlay()
        }

//		timerProgress = new Timer();
//		timerProgress.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				try {
//					if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//						int curPos = mediaPlayer.getCurrentPosition();
//						onPlayProgress(curPos);
//					}
//				} catch(IllegalStateException e){
//					Log.d(TAG, "Player is not initialized!");
//				}
//			}
//		}, 0, AppConstants.VISUALIZATION_INTERVAL);
    }

    override fun seek(mills: Long) {
        seekPos = mills
        if (isPause) {
            pausePos = mills
        }

//        try {
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                removeCallbacks();
//                mediaPlayer.seekTo(Utils.progressToTimer((int) seekPos, mediaPlayer.getDuration()));
//                onSeek((int) seekPos);
//                updateProgressBar();
//            }
//        } catch (IllegalStateException e) {
//            Log.d(TAG, "Player is not initialized!");
//        }
        if (mediaPlayer != null) {
            removeCallbacks()
            mediaPlayer!!.seekTo(progressToTimer(mills.toInt(), mediaPlayer!!.duration))
            updateProgressBar()
        }
    }

    override fun pause() {
//		if (timerProgress != null) {
//			timerProgress.cancel();
//			timerProgress.purge();
//		}
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                onPausePlay()
                seekPos = mediaPlayer!!.currentPosition.toLong()
                isPause = true
                pausePos = seekPos
                if (listener != null) listener!!.onPause()
            }
        }
    }

    override fun stop() {
//		if (timerProgress != null) {
//			timerProgress.cancel();
//			timerProgress.purge();
//		}
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.setOnCompletionListener(null)
            isPrepared = false
            onStopPlay()
            mediaPlayer!!.currentPosition
            seekPos = 0
            //            if (listener != null) {
//                listener.onStop();
//            }
        }
        updateProgressBar()
        isPause = false
        pausePos = 0
    }

    override fun isPlaying(): Boolean {
        try {
            return mediaPlayer != null && mediaPlayer!!.isPlaying
        } catch (e: IllegalStateException) {
            Log.d(TAG, "Player is not initialized!")
        }
        return false
    }

    override fun isPause(): Boolean {
        return isPause
    }

    override fun getPauseTime(): Long {
        return seekPos
    }

    val currentPosition: Int
        get() = mediaPlayer!!.currentPosition

    override fun release() {
        stop()
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
        }
        isPrepared = false
        isPause = false
        dataSource = null
        actionsListeners.clear()
    }

    private fun onPreparePlay() {
        if (!actionsListeners.isEmpty()) {
            for (i in actionsListeners.indices) {
                actionsListeners[i].onPreparePlay()
            }
        }
    }

    private fun onStartPlay() {
        if (listener != null) listener!!.onPlayStarted(mediaPlayer!!.duration)
        updateProgressBar()
        if (!actionsListeners.isEmpty()) {
            for (i in actionsListeners.indices) {
                actionsListeners[i].onStartPlay()
            }
        }
    }

    private fun onPlayProgress(mills: Long) {
        if (!actionsListeners.isEmpty()) {
            for (i in actionsListeners.indices) {
                actionsListeners[i].onPlayProgress(mills)
            }
        }
    }

    private fun onStopPlay() {
        if (!actionsListeners.isEmpty()) {
            for (i in actionsListeners.indices.reversed()) {
                actionsListeners[i].onStopPlay()
            }
        }
    }

    private fun onPausePlay() {
        if (!actionsListeners.isEmpty()) {
            for (i in actionsListeners.indices) {
                actionsListeners[i].onPausePlay()
            }
        }
    }

    private fun onSeek(mills: Long) {
        if (!actionsListeners.isEmpty()) {
            for (i in actionsListeners.indices) {
                actionsListeners[i].onSeek(mills)
            }
        }
    }

    fun OnItemClickListener(listener: OnViewClickListener?) {
        this.listener = listener
    }

    interface OnViewClickListener {
        //        void onPlay();
        fun onCompleted(mp1: MediaPlayer?)
        fun onPlayStarted(duration: Int)
        fun updateDuration(duration: Int, currentPosition: Int, totalTrackTime:Int)
        fun onPause()
    }

    private object SingletonHolder {
        val singleton = AudioPlayer()
    }

    var mHandler = Handler(Looper.getMainLooper())
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                if (listener != null) {
                    val progressPercentage = getProgressPercentage(
                        mediaPlayer!!.currentPosition.toLong(), mediaPlayer!!.duration.toLong()
                    )
                    listener!!.updateDuration(progressPercentage, currentPosition,mediaPlayer!!.duration)
                }
                mHandler.postDelayed(this, VOICE_DELAY)
            }
        }
    }

    fun updateProgressBar() {
        mHandler.removeCallbacks(mUpdateTimeTask)
        mHandler.postDelayed(mUpdateTimeTask, START)
    }

    fun removeCallbacks() {
        mHandler.removeCallbacks(mUpdateTimeTask)
    } //	private void onError(AppException throwable) {

    //		if (!actionsListeners.isEmpty()) {
    //			for (int i = 0; i < actionsListeners.size(); i++) {
    //				actionsListeners.get(i).onError(throwable);
    //			}
    //		}
    //	}
    companion object {
        private const val TAG = "response"
        const val VOICE_DELAY: Long = 1000
        const val START: Long = 0
        val instance: AudioPlayer
            get() = SingletonHolder.singleton

        fun getProgressPercentage(j: Long, j2: Long): Int {
            java.lang.Double.valueOf(0.0)
            return java.lang.Double.valueOf(
                (j / 1000).toInt().toLong().toDouble() / (j2 / 1000).toInt().toLong()
                    .toDouble() * 100.0
            ).toInt()
        }

        fun progressToTimer(i: Int, i2: Int): Int {
            return (i.toDouble() / 100.0 * (i2 / 1000).toDouble()).toInt() * 1000
        }
    }
}