package com.taibahai.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.network.utils.AppClass
import com.taibahai.audioPlayer.AudioPlayer
import com.taibahai.R
import com.taibahai.activities.SplashActivity
import com.taibahai.quran.StringUtils
import com.taibahai.utils.Constants

class MediaNotificationManager : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        action = intent.action
        if (StringUtils.ACTION_PAUSE == action || StringUtils.ACTION_PLAY == action) {
            play()
        } else if (StringUtils.ACTION_CLOSE == action) {
            dismissNotification()
        }
    }

    private fun play() {
        AudioPlayer.instance.playOrPause()
        showMediaNotification(name)
    }

    companion object {
        var action: String? = null
        private var mPendingIntent: PendingIntent? = null
        private var mCloseIntent: PendingIntent? = null
        private var drawable = R.drawable.ic_pause
        private var name: String? = null

        fun dismissNotification() {
            val notificationManager =
                AppClass.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(Constants.NOTIFICATION_ID)
        }

        fun showMediaNotification(currentAudio: String?) {
            name = currentAudio
            val notificationManager =
                AppClass.instance.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                StringUtils.NOTIFICATION_CHANNEL_ID,
                StringUtils.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.description = StringUtils.CHANNEL_DESCRIPTION
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            action = if (AudioPlayer.instance.isPlaying) {
                StringUtils.ACTION_PAUSE
            } else {
                StringUtils.ACTION_PLAY
            }
            drawable = if (AudioPlayer.instance.isPlaying) {
                R.drawable.ic_pause
            } else {
                R.drawable.ic_play
            }

            val notificationIntent = Intent(AppClass.instance, SplashActivity::class.java)
            val contentIntent = PendingIntent.getActivity(AppClass.instance, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

            val notification: Notification = NotificationCompat.Builder(
                AppClass.instance,
                StringUtils.NOTIFICATION_CHANNEL_ID
            )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.splashlogo)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
//                .setStyle(
//                    NotificationCompat.MediaStyle()
//                        .setMediaSession(null)
//                )
                .setColor(AppClass.instance.resources.getColor(R.color.primary))
                .setContentTitle(currentAudio)
                .setSilent(true)
                .setContentText(AppClass.instance.getString(R.string.mishary_rashid_alafasy))
                .setOngoing(AudioPlayer.instance.isPlaying)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        AppClass.instance.resources,
                        R.drawable.mishary_rashid_alafasy
                    )
                )
                .setContentIntent(contentIntent)
                .build()

            notificationManager.notify(Constants.NOTIFICATION_ID, notification)
        }
    }
}

