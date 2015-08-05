package com.muatik.americanhistory.Vocabulary;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import com.muatik.americanhistory.FragmentStory;
import com.muatik.americanhistory.MainActivity;
import com.muatik.americanhistory.MediaPlayerService;
import com.muatik.americanhistory.PlayingNotification;
import com.muatik.americanhistory.R;
import com.muatik.americanhistory.Stories.Story;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by muatik on 23.06.2015.
 */
public class StoryPlayer {

    private static Story story;
    private static SeekBar seekbar;
    public static Application application;
    private static NotificationManager notificationmanager;
    private static int notificationId =1;
    private static NotificationCompat.Builder builder;

    public static class PlayingNotificationListener extends BroadcastReceiver {
        private RemoteViews remoteViews;

        @Override
        public void onReceive(Context context, Intent intent) {
            remoteViews = new RemoteViews(StoryPlayer.application.getPackageName(),
                    R.layout.playing_notification);
            if (!MainActivity.myService.getMediaPlayer().isPlaying()) {
                remoteViews.setImageViewResource(R.id.play, android.R.drawable.ic_media_pause);
                builder.setContent(remoteViews);
                notificationmanager.notify(notificationId, builder.build());
                StoryPlayer.play();
            } else if (MainActivity.myService.getMediaPlayer().isPlaying()) {
                remoteViews.setImageViewResource(R.id.play, android.R.drawable.ic_media_play);
                builder.setContent(remoteViews);
                notificationmanager.notify(notificationId, builder.build());
                StoryPlayer.pause();
            }
        }
    }

    private static BroadcastReceiver MediaPlayerServiceListener = new BroadcastReceiver() {
        private RemoteViews remoteViews;
        @Override
        public void onReceive(Context context, Intent intent) {
            remoteViews = new RemoteViews(StoryPlayer.application.getPackageName(),
                    R.layout.playing_notification);
            String status = intent.getStringExtra("status");
            if (status =="playing") {
                remoteViews.setImageViewResource(R.id.play, android.R.drawable.ic_media_pause);
                builder.setContent(remoteViews);
                notificationmanager.notify(notificationId, builder.build());
            } if (status =="pause") {
                remoteViews.setImageViewResource(R.id.play, android.R.drawable.ic_media_play);
                builder.setContent(remoteViews);
                notificationmanager.notify(notificationId, builder.build());
            }
        }
    };

    public static void set(Story story, SeekBar seekbar, Application app) {

        Log.d("storyplayer---------", story.audioUrl.toString());
        LocalBroadcastManager.getInstance(app.getBaseContext()).registerReceiver(MediaPlayerServiceListener,
                new IntentFilter("MediaPlayerService"));
        StoryPlayer.seekbar = seekbar;
        StoryPlayer.story = story;
        StoryPlayer.application = app;
        URL Url= null;
        Url = story.audioUrl;


        MainActivity.myService.setMediaPlayer(Url);
        seekbar.setMax(MainActivity.myService.getMediaPlayer().getDuration());

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
                MainActivity.myService.getMediaPlayer().seekTo(seekBar.getProgress());
            }
        });
    }

    public static void play() {
        MainActivity.myService.startPlayer();
        StoryPlayer.playingNotification();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (MainActivity.myService.getMediaPlayer().isPlaying()) {
                    seekbar.setProgress(MainActivity.myService.getMediaPlayer().getCurrentPosition());
                    handler.postDelayed(this, 600);
                }
            }
        }, 600);
    }
    public static void updateSeekbar() {
        seekbar.setProgress(MainActivity.myService.getMediaPlayer().getCurrentPosition());
    }

    public static void pause() {
        MainActivity.myService.pausePlayer();
    }

    public static void stop() {
        MainActivity.myService.stopPlayer();
    }

    public static MediaPlayer getPlayer() {
        return MainActivity.myService.getMediaPlayer();
    }

    public static String getUrl() {
        return MainActivity.myService.getUrl();
    }

    public static Story getStory() {
        return StoryPlayer.story;
    }


    public static void resetProgress() {
        seekbar.setProgress(0);
    }

    public static void playingNotification() {
        RemoteViews remoteViews = new RemoteViews(StoryPlayer.application.getPackageName(),
                R.layout.playing_notification);

        Intent intent = new Intent(StoryPlayer.application.getApplicationContext(), PlayingNotificationListener.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(StoryPlayer.application.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.play, pIntent);
        Intent mainActivityIntent = new Intent(StoryPlayer.application.getApplicationContext(), MainActivity.class);
        Log.d("------------storyId", StoryPlayer.story.id.toString());
        mainActivityIntent.putExtra("storyId", StoryPlayer.story.id);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(StoryPlayer.application.getApplicationContext(), 0, mainActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(StoryPlayer.application.getApplicationContext())
                .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
                .setTicker("american history")
                .setContentIntent(mainActivityPendingIntent)
                .setContent(remoteViews);

        //remoteViews.setTextViewText(R.id.title, StoryPlayer.story.title);
        remoteViews.setTextViewText(R.id.text, StoryPlayer.story.title);


        notificationmanager = (NotificationManager) StoryPlayer.application.getSystemService(
                StoryPlayer.application.getApplicationContext().NOTIFICATION_SERVICE);
        notificationmanager.notify(notificationId, builder.build());
    }

    public static void clearNotification()
    {
        NotificationManager notificationManager = (NotificationManager)StoryPlayer.application.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
