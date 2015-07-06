package com.muatik.americanhistory.Vocabulary;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import com.muatik.americanhistory.FragmentStory;
import com.muatik.americanhistory.PlayingNotification;
import com.muatik.americanhistory.R;
import com.muatik.americanhistory.Stories.Story;

/**
 * Created by muatik on 23.06.2015.
 */
public class StoryPlayer {

    private static String url;
    private static SeekBar seekbar;
    public static MediaPlayer player;
    public static Application application;

    public static void set(String url, SeekBar seekbar, Application app) {
        StoryPlayer.seekbar = seekbar;
        StoryPlayer.url = url;
        StoryPlayer.application = app;

        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        player = new MediaPlayer();


        try {
            player.setDataSource(url);
            player.prepare();
            player.setVolume(1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        seekbar.setMax(player.getDuration());

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("mustafa", "onProgressChanged + " + String.valueOf(progress / 1000));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("mustafa", "onStartTrackingTouch + " + String.valueOf(seekBar.getProgress() / 1000));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("mustafa", "onStopTrackingTouch + " + String.valueOf(seekBar.getProgress() / 1000));
                player.seekTo(seekBar.getProgress());
            }
        });


    }

    public static void play() {
        player.start();
        StoryPlayer.playingNotification();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (player.isPlaying()) {
                    seekbar.setProgress(player.getCurrentPosition());
                    handler.postDelayed(this, 600);
                }
            }
        }, 600);
    }

    public static void pause() {
        player.pause();
    }

    public static void stop() {
        player.stop();
    }

    public static void resetProgress() {
        seekbar.setProgress(0);
    }

    public static void playingNotification() {
        // Using RemoteViews to bind custom layouts into Notification
        RemoteViews remoteViews = new RemoteViews(StoryPlayer.application.getPackageName(),
                R.layout.playing_notification);

        // Set Notification Title
        String strTitle = "title";
        // Set Notification Text
        String strText = "text";

        // Open NotificationView Class on Notification Click
        //Intent intent = new Intent(this, PlayingNotification.class);
        // Send data to NotificationView Class
        //intent.putExtra("title", strTitle);
        // Open NotificationView.java Activity
        //PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                //PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(StoryPlayer.application.getApplicationContext())
                // Set Icon
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                        // Set Ticker Message
                .setTicker("ticker message")
                        // Dismiss Notification
                .setAutoCancel(true)
                        // Set PendingIntent into Notification
                //.setContentIntent(pIntent)
                        // Set RemoteViews into Notification
                .setContent(remoteViews);

        // Locate and set the Text into customnotificationtext.xml TextViews
        remoteViews.setTextViewText(R.id.title, strTitle);
        remoteViews.setTextViewText(R.id.text, strText);

        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) StoryPlayer.application.getSystemService(StoryPlayer.application.getApplicationContext().NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());

    }

}
