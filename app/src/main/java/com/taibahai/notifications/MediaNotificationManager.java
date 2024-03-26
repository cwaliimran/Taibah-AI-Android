package com.taibahai.notifications;

import static android.content.Context.NOTIFICATION_SERVICE;

import static com.network.utils.AppClass.sharedPref;
import static com.taibahai.quran.StringUtils.ACTION_CLOSE;
import static com.taibahai.quran.StringUtils.ACTION_PAUSE;
import static com.taibahai.quran.StringUtils.ACTION_PLAY;
import static com.taibahai.quran.StringUtils.CHANNEL_DESCRIPTION;
import static com.taibahai.quran.StringUtils.CHANNEL_NAME;
import static com.taibahai.quran.StringUtils.NOTIFICATION_CHANNEL_ID;
import static com.taibahai.utils.Constants.NOTIFICATION_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.network.utils.AppClass;
import com.taibahai.R;
import com.taibahai.audioPlayer.AudioPlayer;
import com.taibahai.quran.StringUtils;
import com.taibahai.utils.Constants;

public class MediaNotificationManager extends BroadcastReceiver {
    public static PendingIntent mPendingIntent;
    private static PendingIntent mCloseIntent;
    private static int drawable = R.drawable.ic_pause;
    private static boolean isPlaying = true;
    private boolean isCalled = false;
    private static String name;
    private static String action;
    final int flag =  Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT;

    @Override
    public void onReceive(Context context, Intent intent) {
        action = intent.getAction();
//        Log.d(TAG, "doSomething: " + action);

        if (ACTION_PAUSE.equals(action) || ACTION_PLAY.equals(action)) {
            play();
        } else if (ACTION_CLOSE.equals(action)) {
            dismissNotification();
        }
    }

    public static void dismissNotification() {
        // Cancel Notification
        if (!AudioPlayer.Companion.getInstance().isPlaying()) {
            NotificationManager notificationManager = (NotificationManager) AppClass.Companion.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Constants.NOTIFICATION_ID);
        }
    }

    private void play() {
        AudioPlayer.Companion.getInstance().playOrPause();
        new Handler().postDelayed(() -> showMediaNotification(name), 200);
    }

    public static void showMediaNotification(String currentAudio) {
        name = currentAudio;
        sharedPref.storeString(StringUtils.SURAH_NAME, currentAudio);
        NotificationManager notificationManager = (NotificationManager)
                AppClass.Companion.getInstance().getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new
                    NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Configure the notification channel.
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (AudioPlayer.Companion.getInstance().isPlaying()) {
            action = ACTION_PAUSE;
            drawable = R.drawable.ic_pause;
        } else {
            action = ACTION_PLAY;
            drawable = R.drawable.ic_play;
        }

        // While making notification
        Intent intent = new Intent(action);
        Intent intent1 = new Intent(ACTION_CLOSE);
        final int flag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            mPendingIntent = PendingIntent.getBroadcast(getInstance(), 0, intent, flag);
//            mCloseIntent = PendingIntent.getBroadcast(getInstance(), 0, intent1, flag);
//        }else{
            mPendingIntent = PendingIntent.getBroadcast(AppClass.Companion.getInstance(), 0, intent, flag);
            mCloseIntent = PendingIntent.getBroadcast(AppClass.Companion.getInstance(), 0, intent1, flag);
//        }



        Notification notification = new NotificationCompat.Builder(AppClass.Companion.getInstance(), NOTIFICATION_CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.splashlogo)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
//                .addAction(R.drawable.ic_prev, "Previous", null) // #0
                .addAction(drawable, "Pause", mPendingIntent)  // #1
                .addAction(R.drawable.ic_close, "Close", mCloseIntent)  // #1
//                .addAction(R.drawable.ic_next, "Next", null)     // #2
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1)
                        .setMediaSession(null))
                .setColor(AppClass.Companion.getInstance().getResources().getColor(R.color.primary))
                .setContentTitle(currentAudio)
                .setSilent(true)
                .setContentText(AppClass.Companion.getInstance().getString(R.string.mishary_rashid_alafasy))
                .setOngoing(AudioPlayer.Companion.getInstance().isPlaying())
                .setLargeIcon(BitmapFactory.decodeResource(AppClass.Companion.getInstance().getResources(), R.drawable.mishary_rashid_alafasy))
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);

    }


}
