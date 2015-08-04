package com.muatik.americanhistory;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.IOException;
import java.net.URL;

/**
 * Created by alpay on 06.07.2015.
 */
public class MediaPlayerService extends Service  {
    private static MediaPlayer mediaPlayer;
    private String url = "";
    String logTag="MediaService";
    private final IBinder binder = new MyLocalBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        Log.d(logTag, "oncreate");

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }


        mediaPlayer = new MediaPlayer();
    }

    public class MyLocalBinder extends android.os.Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(logTag, "onStartCommand");

        try {
            String url = intent.getStringExtra("url");
        } catch (Exception e){
            Log.d(logTag, e.getMessage());
        }

        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    public void startPlayer(){
        mediaPlayer.start();
        Intent serviceIntent = new Intent("MediaPlayerService");
        serviceIntent.putExtra("status", "playing");
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(serviceIntent);
    }

    public void stopPlayer(){
        mediaPlayer.stop();
    }

    public void pausePlayer(){
        mediaPlayer.pause();
        Intent serviceIntent = new Intent("MediaPlayerService");
        serviceIntent.putExtra("status", "pause");
        LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(serviceIntent);
    }

    public void setMediaPlayer(URL url){
        try {
            mediaPlayer.setDataSource(url.toString());
            mediaPlayer.prepare();
            mediaPlayer.setVolume(1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    public void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    public String getUrl() {
        return this.url;
    }
}
