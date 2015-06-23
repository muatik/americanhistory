package com.muatik.americanhistory.Vocabulary;

import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.muatik.americanhistory.R;
import com.muatik.americanhistory.Stories.Story;

/**
 * Created by muatik on 23.06.2015.
 */
public class StoryPlayer {

    private static String url;
    private static SeekBar seekbar;
    public static MediaPlayer player;

    public static void set(String url, SeekBar seekbar) {
        StoryPlayer.seekbar = seekbar;
        StoryPlayer.url = url;

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

}
