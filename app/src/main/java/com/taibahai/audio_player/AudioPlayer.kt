import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.os.Handler
import android.util.Log
import com.network.utils.AppClass
import com.network.utils.AppConstants
import com.taibahai.audio_player.PlayerContract
import java.io.IOException
import java.util.Objects

class AudioPlayer : PlayerContract.Player, OnPreparedListener {
    var listener: OnViewClickListener? = null
    private val actionsListeners: MutableList<PlayerContract.PlayerCallback> = mutableListOf()
    private var mediaPlayer: MediaPlayer? = null

    //	private Timer timerProgress;
    private var isPrepared = false
    private var seekPos: Long = 0
    private var pausePos: Long = 0
    private var dataSource: String? = null

    fun getInstance(): AudioPlayer? {
        return SingletonHolder.singleton
    }


    override fun addPlayerCallback(callback: PlayerContract.PlayerCallback?) {
        if (callback != null) {
            actionsListeners.add(callback)
        }
    }

    override fun removePlayerCallback(callback: PlayerContract.PlayerCallback?): Boolean {
        return if (callback != null) {
            actionsListeners.remove(callback)
        } else false
    }

    override fun setData(data: String?) {
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
                    AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
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
                if (Objects.requireNonNull(e.message)?.contains("Permission denied") == true) {
                    Log.d(TAG, "restartPlayer: Permission denied")
                } else {
                    Log.d(TAG, "restartPlayer: DataSource Exeption")
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                if (Objects.requireNonNull(e.message)?.contains("Permission denied") == true) {
                    Log.d(TAG, "restartPlayer: Permission denied")
                } else {
                    Log.d(TAG, "restartPlayer: DataSource Exeption")
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
                if (Objects.requireNonNull(e.message)?.contains("Permission denied") == true) {
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
                        } catch (ex: java.lang.IllegalStateException) {
                            ex.printStackTrace()
                            restartPlayer()
                            mediaPlayer!!.setOnPreparedListener(this)
                            try {
                                mediaPlayer!!.prepareAsync()
                            } catch (e: java.lang.IllegalStateException) {
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
                            this@AudioPlayer.onStopPlay()
                        }
                    }
                    pausePos = 0
                }
            }
        } catch (e: java.lang.IllegalStateException) {
            e.printStackTrace()
            Log.d(TAG, "Player is not initialized!")
        }
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
            mediaPlayer!!.seekTo(AppClass.progressToTimer(mills.toInt(), mediaPlayer!!.duration))
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

    override val isPlaying: Boolean
        get() {
            try {
                return mediaPlayer != null && mediaPlayer!!.isPlaying
            } catch (e: IllegalStateException) {
                Log.d(TAG, "Player is not initialized!")
            }
            return false
        }


    override var isPause = false

    override val pauseTime: Long
        get() = seekPos


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



    companion object {
        private const val TAG = "response"
        fun getInstance(): AudioPlayer? {
            return SingletonHolder.singleton
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
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
          /*  MediaNotificationManager.showMediaNotification(
                GlobalClass.sharedPref.getString(
                    StringUtils.SURAH_NAME,
                    ""
                )
            )*/
            stop()
            onStopPlay()
        }
    }

    fun OnItemClickListener(listener: OnViewClickListener?) {
        this.listener = listener
    }

    interface OnViewClickListener {
        //        void onPlay();
        //staff click
        fun onCompleted(mp1: MediaPlayer?)
        fun onPlayStarted(duration: Int)
        fun updateDuration(duration: Int, currentPosition: Int)
        fun onPause()
    }


    private object SingletonHolder {
        val singleton = AudioPlayer()
    }

    var mHandler = Handler()
    private val mUpdateTimeTask: Runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                if (listener != null) {
                    val progressPercentage: Int = AppClass.getProgressPercentage(
                        mediaPlayer!!.currentPosition.toLong(),
                        mediaPlayer!!.duration.toLong()
                    )
                    listener!!.updateDuration(progressPercentage, mediaPlayer!!.getCurrentPosition())
                }
                mHandler.postDelayed(this, AppConstants.VOICE_DELAY)
            }
        }
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


    fun updateProgressBar() {
        mHandler.removeCallbacks(mUpdateTimeTask)
        mHandler.postDelayed(mUpdateTimeTask, AppConstants.START)
    }

    fun removeCallbacks() {
        mHandler.removeCallbacks(mUpdateTimeTask)
    }
}