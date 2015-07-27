package com.muatik.americanhistory;

/**
 * Created by alpay on 06.07.2015.
 */
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.muatik.americanhistory.Vocabulary.StoryPlayer;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alpay on 30.06.2015.
 */
public class PlayingNotification extends Activity {
    String title;
    String text;
    TextView txttitle;
    TextView txttext;
    protected String playerStatus="playing";
    View mainView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.playing_notification, container, false);
        ButterKnife.inject(this, mainView);
        return mainView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playing_notification);
        ButterKnife.inject(this, mainView);
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Dismiss Notification
        notificationmanager.cancel(0);

        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        title = i.getStringExtra("title");
        text = i.getStringExtra("text");

        // Locate the TextView
        txttitle = (TextView) findViewById(R.id.title);
        txttext = (TextView) findViewById(R.id.text);

        // Set the data into TextView
        txttitle.setText(title);
        txttext.setText(text);
    }

    @OnClick(R.id.play)
    public void audioPlay(View view) {
        final ImageButton audioPlay = (ImageButton) view;
        if (playerStatus == "stopped") {
            audioPlay.setImageResource(android.R.drawable.ic_media_pause);
            audioPlay.refreshDrawableState();
            playerStatus = "playing";
            MainActivity.myService.startPlayer();
        } else if (playerStatus =="playing") {
            audioPlay.setImageResource(android.R.drawable.ic_media_play);
            playerStatus="pause";
            MainActivity.myService.pausePlayer();
        } else if (playerStatus =="pause") {
            audioPlay.setImageResource(android.R.drawable.ic_media_pause);
            playerStatus="playing";
            MainActivity.myService.startPlayer();
        }
    }
}