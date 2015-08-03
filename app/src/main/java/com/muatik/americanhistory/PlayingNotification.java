package com.muatik.americanhistory;

/**
 * Created by alpay on 06.07.2015.
 */
import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.cancel(0);
        /*


        // Retrive the data from MainActivity.java
        Intent i = getIntent();

        title = i.getStringExtra("title");
        text = i.getStringExtra("text");

        // Locate the TextView
        txttitle = (TextView) findViewById(R.id.title);
        txttext = (TextView) findViewById(R.id.text);

        // Set the data into TextView
        txttitle.setText(title);
        txttext.setText(text);*/
    }
}